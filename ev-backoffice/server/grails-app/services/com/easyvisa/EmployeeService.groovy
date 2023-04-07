package com.easyvisa

import com.easyvisa.enums.AttorneyType
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.OrganizationType
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.utils.ExceptionUtils
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer

class EmployeeService {

    ProfileService profileService
    EvMailService evMailService
    ProcessService processService
    UserService userService
    OrganizationService organizationService
    AlertService alertService
    PageRenderer groovyPageRenderer
    AdminService adminService
    AddressService addressService
    AsyncService asyncService

    @Transactional
    Employee updateEmployee(Employee employee, ProfileCommand changes) {
        employee.profile.with {
            firstName = changes.firstName ?: firstName
            middleName = changes.middleName
            lastName = changes.lastName ?: lastName
            practiceName = changes.practiceName  ?: practiceName
        }
        profileService.updateProfileEmail(employee.profile, changes.email)
        employee.with {
            officePhone = changes.officePhone
            mobilePhone = changes.mobilePhone
            faxNumber = changes.faxNumber
            spokenLanguages = changes.languages
        }
        if (changes.officeAddress) {
            employee.profile.address = addressService.updateAddress(employee.officeAddress, changes.officeAddress)
        }
        employee.save(failOnError: true)
    }

    @Transactional
    OrganizationEmployee createEmployee(EmployeeCommand createEmployeeCommand, Employee createdBy, Organization organization) {
        Employee employee = new Employee()
        employee.profile = new Profile()
        employee.profile.with {
            firstName = createEmployeeCommand.firstName
            lastName = createEmployeeCommand.lastName
            middleName = createEmployeeCommand.middleName
            email = createEmployeeCommand.email
        }
        employee.with {
            mobilePhone = createEmployeeCommand.mobilePhone
            officePhone = createEmployeeCommand.officePhone
        }
        employee = profileService.addEasyVisaId(employee)
        Boolean isAdmin = false

        // If position is Manager or Partner then default Admin flag to true
        if (createEmployeeCommand.position in [EmployeePosition.MANAGER, EmployeePosition.PARTNER]) {
            isAdmin = true
        }
        OrganizationEmployee organizationEmployee = new OrganizationEmployee(organization: organization,
                employee: employee,
                status: EmployeeStatus.PENDING,
                position: createEmployeeCommand.position,
                isAdmin: isAdmin).save()

        RegistrationCode registrationCode = new RegistrationCode(easyVisaId: employee.profile.easyVisaId)
        registrationCode.save()
        evMailService.sendAddNewEmployeeEmail(employee, registrationCode, createdBy, organization)
        return organizationEmployee
    }

    @Transactional
    OrganizationEmployee updateEmployee(EmployeeCommand changes, Employee employee, Organization organization, User currentUser) {
        if ([EmployeePosition.PARTNER, EmployeePosition.ATTORNEY].contains(changes.position) && !(employee instanceof LegalRepresentative)) {
            throw ExceptionUtils.createUnProcessableDataException('update.employee.attorney.position.to.employee')
        }
        OrganizationEmployee organizationEmployee = getLastOrganizationEmployee(organization, employee)
        EmployeePosition currentPosition = organizationEmployee.position
        List<LegalRepresentative> attorneys = organizationService.getRepresentatives(organization.id)
        if (organization.organizationType == OrganizationType.SOLO_PRACTICE) {
            Employee soloAttorney = OrganizationEmployee.findByOrganizationAndPositionInList(organization,
                    EmployeePosition.attorneyPositions).employee
            //check that at least one attorney is available for solo org
            if (soloAttorney.id == employee.id
                    && (!EmployeePosition.attorneyPositions.contains(changes.position)
                    || changes.status == EmployeeStatus.INACTIVE)) {
                throw ExceptionUtils.createUnProcessableDataException('you.cannot.leave.a.solo.practice')
            }
            //check that no two or more attorneys in solo org
            if (soloAttorney.id != employee.id && EmployeePosition.attorneyPositions.contains(changes.position)) {
                throw ExceptionUtils.createUnProcessableDataException('solo.two.attorneys')
            }
        }

        if (changes.isAdmin == false || changes.status == EmployeeStatus.INACTIVE) {
            checkOtherActiveAdmin(employee, organization)
        }

        employee.profile.with {
            firstName = changes.firstName
            lastName = changes.lastName
            middleName = changes.middleName
            email = changes.email
        }
        employee.with {
            mobilePhone = changes.mobilePhone
            officePhone = changes.officePhone
        }
        if (changes.status) {
            organizationEmployee.status = changes.status
            if (changes.status == EmployeeStatus.INACTIVE) {
                organizationEmployee.inactiveDate = new Date()
            }
        } else {
            organizationEmployee.status = EmployeeStatus.ACTIVE
        }
        if (currentPosition != changes.position) {
            userService.assignRoleForBlessedOrg(organization, changes.position, employee.user,
                    Role.findByAuthority(Role.EV))

            // If position is changed TO Manager, then default Admin flag to true
            // otherwise, admin flag will be carried over from the UI.
            if (changes.position in [EmployeePosition.MANAGER]) {
                changes.isAdmin = true
            }
        }
        organizationEmployee.position = changes.position
        organizationEmployee.isAdmin = changes.isAdmin
        profileService.updateProfileEmail(employee.profile, changes.email, !employee.user)

        organizationEmployee.save(failOnError: true)
        employee.save(failOnError: true, flush: true)

        if (organization.lawFirm && employee.legalRepresentative) {
            LegalRepresentative attorney = employee as LegalRepresentative
            if (changes.status == EmployeeStatus.ACTIVE
                    && EmployeePosition.attorneyPositions.contains(changes.position)
                    && attorney.attorneyType != AttorneyType.MEMBER_OF_A_LAW_FIRM) {
                attorney.attorneyType = AttorneyType.MEMBER_OF_A_LAW_FIRM
            }
            if (EmployeePosition.attorneyPositions.contains(currentPosition) &&
                    (changes.status == EmployeeStatus.INACTIVE || (changes.status == EmployeeStatus.ACTIVE
                            && EmployeePosition.employeePositions.contains(changes.position)))) {
                if (attorneys.size() == 2) {
                    //if there are two attorneys in a firm and one is terminating then show an error for now.
                    throw ExceptionUtils.createUnProcessableDataException('update.employee.deactivation.last.of.two.attorneys')
                } else {
                    //if inactivating attorney or changing currentPosition to Employee one and attorney count > 2 then
                    //just deactivates attorneys stuff
                    attorney.attorneyType = AttorneyType.SOLO_PRACTITIONER
                    organizationService.deactivateAttorneyInLawFirm(organizationEmployee)
                }
            }
        }

        if (currentUser.id != employee.getUser().id) {
            EasyVisaSystemMessageType messageType = EasyVisaSystemMessageType.CHANGED_PERMISSIONS;
            alertService.createAlert(messageType, employee.getUser(), currentUser.profile.getName());
        }

        organizationEmployee
    }

    OrganizationEmployee getLastOrganizationEmployee(Organization organization, Employee employee) {
        List<OrganizationEmployee> employeeList = OrganizationEmployee.createCriteria().list(max: 1, sort: "id", order: "desc") {
            eq('organization', organization)
            eq('employee', employee)
        }
        return employeeList.get(0);
    }

    void checkOtherActiveAdmin(Employee employee, Organization organization) {
        Long count = OrganizationEmployee.createCriteria().get() {
            projections {
                rowCount()
            }
            ne('employee', employee)
            eq('organization', organization)
            eq('status', EmployeeStatus.ACTIVE)
            eq('isAdmin', true)
            isNull('inactiveDate')
        }
        if (count == 0) {
            throw (ExceptionUtils.createAccessDeniedException('organization.must.have.at.least.one.active.admin',
                    null, null, ErrorMessageType.ALONE_ADMIN))
        }
    }

    /**
     * Deletes an employee and related stuff. Uses for delete account action.
     * @param employee employee
     */
    void deleteEmployee(Employee employee) {
        //workaround to not send Alerts to deleting user. Not ideal...
        employee.profile.user.activeMembership = Boolean.FALSE
        denyRequests(employee.profile, employee)
        deactivateOrganizationEmployee(employee)
        employee.with {
            officePhone = null
            mobilePhone = null
            faxNumber = null
            spokenLanguages?.clear()
        }
        employee.save(failOnError: true)
        validateAndNotifyAdminOrgs(employee)
    }

    /**
     * Deactivates an employee in all organizations.
     * @param employee employee.
     */
    void deactivateOrganizationEmployee(Employee employee) {
        OrganizationEmployee.executeUpdate(
                'update OrganizationEmployee oe set oe.status = :status, inactiveDate = now() ' +
                        'where oe.status <> :status and oe.employee = :employee',
                [status: EmployeeStatus.INACTIVE, employee: employee])
    }

    /**
     * Validate if user alone admin in any and sends alerts to admin orgs or and if so throws EasyVisaException.
     * @param employee employee.
     */
    void validateAndNotifyAdminOrgs(Employee employee) {
        List<Organization> admOrgs = getEmployeeOrgsForDisabling(employee, true)
        List<Organization> nonAdminOrgs = getEmployeeOrgsForDisabling(employee, false)

        //checking non alone admin in non solo orgs
        admOrgs.each {
            checkOtherActiveAdmin(employee, it)
        }
        //notifying admin of non solo orgs
        admOrgs.addAll(nonAdminOrgs)

        asyncService.runAsync({
            sendAlertsToAdmins(admOrgs.collect { it.id }, employee.id)
        }, "Send admin notifications about leaving/deactivating Employee [$employee.id]")
    }

    private void sendAlertsToAdmins(List<Long> admOrgs, Long employeeId) {
        Map<String, Alert> alerts = [:]
        EasyVisaSystemMessageType alertType = EasyVisaSystemMessageType.PACKAGE_ATTORNEY_LEFT
        Employee employee = Employee.get(employeeId)
        Profile profile = employee.profile
        String subject = String.format(alertType.subject, "${profile.firstName}, ${profile.lastName}")
        BigDecimal reactivationFee = adminService.adminSettings.adminConfig.membershipReactivationFee
        admOrgs.each {
            Organization org = Organization.get(it)
            String body = groovyPageRenderer.render(template: alertType.templatePath, model: [profile         : profile,
                                                                                              organizationType: org.organizationType.displayName,
                                                                                              reactivationFee : reactivationFee])
            organizationService.getOrganizationAdmins(org).each {
                if (it.id != employee.id && it.user?.activeMembership) {
                    alerts.put(it.user.profile.email, new Alert(recipient: it.user, subject: subject,
                            source: EvSystemMessage.EASYVISA_SOURCE, messageType: alertType, body: body)
                            .save(failOnError: true))
                }
            }
        }
        alerts.values().first().save(flush: true)
        alerts.each {
            Alert alert = it.value
            User.withNewSession {
                evMailService.sendAlertEmail(alert)
            }
        }
    }

    private List<Organization> getEmployeeOrgsForDisabling(Employee employee, boolean isAdmin) {
        OrganizationEmployee.createCriteria().list() {
            projections {
                property('organization')
            }
            'organization' {
                ne('organizationType', OrganizationType.SOLO_PRACTICE)
            }
            eq('employee', employee)
            eq('status', EmployeeStatus.ACTIVE)
            eq('isAdmin', isAdmin)
        } as List<Organization>
    }

    private void denyRequests(Profile profile, Employee employee) {
        //decline join requests, 1. where the employee is a requester 2. where the employee is a receiver
        List<JoinOrganizationRequest> joinRequesterList = JoinOrganizationRequest
                .findAllByRequestedByAndState(profile, ProcessRequestState.PENDING)
        denyJoinRequests(joinRequesterList)
        List<JoinOrganizationRequest> joinReceiveList = JoinOrganizationRequest
                .findAllByEmployeeAndState(employee, ProcessRequestState.PENDING)
        denyJoinRequests(joinReceiveList)

        //decline join invites, 1. where the employee is a requester 2. where the employee is a receiver
        List<InviteToOrganizationRequest> inviteRequesterList = InviteToOrganizationRequest
                .findAllByRequestedByAndState(profile, ProcessRequestState.PENDING)
        declineJoinInvites(inviteRequesterList)
        List<InviteToOrganizationRequest> inviteReceiveList = InviteToOrganizationRequest
                .findAllByEmployeeAndState(employee, ProcessRequestState.PENDING)
        declineJoinInvites(inviteReceiveList)
    }

    private List<JoinOrganizationRequest> denyJoinRequests(List<JoinOrganizationRequest> requestsList) {
        requestsList.each {
            processService.denyJoinOrganizationRequest(it)
        }
    }

    private List<InviteToOrganizationRequest> declineJoinInvites(List<InviteToOrganizationRequest> invitesList) {
        invitesList.each {
            processService.denyInvitationToJoinOrganization(it)
        }
    }

}
