package com.easyvisa

import com.easyvisa.enums.*
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.StringUtils
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import org.apache.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

import static com.easyvisa.enums.EmployeePosition.ATTORNEY

@Transactional
class OrganizationService {

    GrailsApplication grailsApplication
    AddressService addressService
    AlertService alertService
    FileService fileService
    def dataSource
    PermissionsService permissionsService
    AttorneyService attorneyService
    EmployeeService employeeService
    SqlService sqlService
    AsyncService asyncService

    Organization addOrganizationId(Organization organization) {
        String prefix = ""
        if (OrganizationType.SOLO_PRACTICE.equals(organization.organizationType)) {
            prefix = "S"
        } else if (OrganizationType.LAW_FIRM.equals(organization.organizationType)) {
            prefix = "L"
        } else if (OrganizationType.RECOGNIZED_ORGANIZATION.equals(organization.organizationType)) {
            prefix = "R"
        }

        organization.easyVisaId = "${prefix}${StringUtils.padEasyVisaId(sqlService.getNextSequenceId('organization_ev_id_seq'))}"
        organization
    }

    Organization create(String organizationName, OrganizationType organizationType) {
        Organization organization = new Organization(organizationType: organizationType, name: organizationName)
        organization = addOrganizationId(organization)
        organization.save(failOnError: true)
    }

    OrganizationEmployee addAttorneyToOrganization(Organization organization, LegalRepresentative attorney, EmployeePosition position = null, boolean isAdmin = false) {
        OrganizationEmployee organizationEmployee = new OrganizationEmployee(organization: organization, employee: attorney, status: EmployeeStatus.ACTIVE, position: position ?: ATTORNEY, isAdmin: isAdmin).save(failOnError: true)
        organization.save(failOnError: true)
        organizationEmployee
    }

    List<Employee> getEmployees(Long orgId) {
        OrganizationEmployee.createCriteria().list {
            projections {
                property('employee')
            }
            'organization' {
                eq('id', orgId)
            }
            eq('status', EmployeeStatus.ACTIVE)
        } as List<Employee>
    }

    List getRepresentativeMenu(Long orgId, Boolean includeInactive = false) {
        final Sql sql = new Sql(dataSource)

        String query = "select l.id, oemp.status, p.first_name, p.last_name, p.middle_name, p.user_id " +
                "from legal_representative l " +
                "inner join organization_employee oemp on l.id = oemp.employee_id inner join profile p on p.id = " +
                "(select e.profile_id from employee e where e.id = oemp.employee_id) where l.id in " +
                "(select oe.employee_id from organization_employee oe where oe.organization_id=:orgId and oe.status in "

        if (includeInactive) {
            query += "('ACTIVE', 'INACTIVE') and oe.position in('ATTORNEY','PARTNER')) and oemp.status in ('ACTIVE', 'INACTIVE') and oemp.organization_id = :orgId"
        } else {
            query += "('ACTIVE') and oe.position in('ATTORNEY','PARTNER') and oe.inactive_date is NULL) and oemp.status in ('ACTIVE') and oemp.organization_id = :orgId"
        }

        Map queryParams = [orgId: orgId]

        def result = sql.rows(query, queryParams)
        return result
    }

    List<LegalRepresentative> getRepresentatives(Long orgId) {
        final String query = '''FROM LegalRepresentative l where l.id in
            (select oe.employee.id from OrganizationEmployee oe where oe.organization.id=:org_id and oe.status=:status and oe.position in(:attorney,:partner) and oe.inactiveDate is NULL)
'''
        LegalRepresentative.executeQuery(query, [org_id: orgId, status: EmployeeStatus.ACTIVE, attorney: ATTORNEY, partner: EmployeePosition.PARTNER]) as List<LegalRepresentative>
    }

    Boolean doesEmployeeBelongToOrganization(Long employeeId, Long organizationId, Boolean activeOnly = true) {
        OrganizationEmployee.createCriteria().count {
            employee {
                eq('id', employeeId)
            }
            organization {
                eq('id', organizationId)
            }
            if (activeOnly) {
                eq('status', EmployeeStatus.ACTIVE)
                isNull('inactiveDate')
            }
        } > 0
    }

    Organization getBlessedOrganization() {
        String orgId = grailsApplication.config.easyvisa.blessedOrganizationEVId
        if (orgId) {
            Organization.findByEasyVisaId(orgId)
        }
    }

    List<User> getBlessedOrgAdminsUsers() {
        OrganizationEmployee.findAllByOrganizationAndIsAdminAndStatus(blessedOrganization, Boolean.TRUE, EmployeeStatus.ACTIVE)*.employee*.user
    }

    Organization updateOrganization(Organization organization, OrganizationCommand changes) {
        organization.with {
            name = changes.name ?: name
            profileSummary = changes.summary
            awards = changes.awards
            experience = changes.experience
            officePhone = changes.officePhone
            mobilePhone = changes.mobilePhone
            faxNumber = changes.faxNumber
            email = changes.email
            twitterUrl = changes.twitterUrl
            facebookUrl = changes.facebookUrl
            youtubeUrl = changes.youtubeUrl
            linkedinUrl = changes.linkedinUrl
            websiteUrl = changes.websiteUrl
            spokenLanguages = changes.languages
            practiceAreas = changes.practiceAreas
            rosterNames = changes.rosterNames
            yearFounded = changes.yearFounded
        }
        if (changes.officeAddress) {
            organization.address = addressService.updateAddress(organization.address, changes.officeAddress)
        }
        if (changes.workingHours) {
            def itemsToDelete = organization.workingHours
            itemsToDelete?.each {
                it.delete(failOnError: true)
            }
            organization.workingHours = []
            changes.workingHours.each {
                organization.addToWorkingHours(new WorkingHour(startHour: it.start.hour, startMinutes: it.start.minutes, endHour: it.end.hour, endMinutes: it.end.minutes, dayOfWeek: it.dayOfWeek).save(failOnError: true))
            }
        }
        organization.save(failOnError: true)
    }

    /**
     * This method validates and sends invitation to member to join organization
     * @param organization This is the instance of Organization
     * @param requestedBy This is the instance of User
     * @param evId EasyVisa Id of a member
     * @param email Email of a member
     * @return InviteToOrganizationRequest This returns information of invitation
     */
    InviteToOrganizationRequest validateAndCreateInviteToOrganizationRequest(Organization organization, User requestedBy, String evId, String email) {
        permissionsService.assertIsAdmin(requestedBy, organization)
        permissionsService.assertIsActive(requestedBy)
        permissionsService.assertIsPaid()

        Employee inviter = Employee.findByProfile(requestedBy.profile)
        Profile profile = Profile.findByEasyVisaId(evId)
        if (!profile) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'no.user.with.evid')
        }
        if (profile == inviter.profile) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'you.cant.invite.yourself.to.an.org')
        }
        if (!profile.email?.equalsIgnoreCase(email)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'input.email.does.not.match.evid')
        }
        Long applicantCount = Applicant.countByProfile(profile)
        if (applicantCount > 0) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'user.is.applicant.not.attorney')
        }

        Employee invitee = Employee.findByProfile(profile)
        if (!invitee) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'attorney.not.found')
        }
        if (organization.soloPractice && invitee.legalRepresentative) {
            throw ExceptionUtils.createUnProcessableDataException('solo.two.attorneys')
        }
        if (OrganizationEmployee.countByEmployeeAndOrganizationAndStatus(invitee, organization, EmployeeStatus.ACTIVE)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'employee.already.exists.in.organization')
        }
        if (OrganizationEmployee.countByEmployeeAndOrganizationAndStatus(invitee, organization, EmployeeStatus.PENDING)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'invitation.already.sent')
        }

        permissionsService.assertIsPaid(profile.user)
        permissionsService.assertIsActive(invitee)

        if (invitee.instanceOf(LegalRepresentative)) {
            if (isEmployeeActiveInLawFirm(invitee)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'invitee.must.first.leave.their.law.firm.before.joining.your.organization')
            }
        } else {
            if (OrganizationEmployee.findByEmployeeAndStatus(invitee, EmployeeStatus.ACTIVE)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'invitee.must.first.leave.their.current.organization.before.joining.your.organization')
            }
        }

        OrganizationEmployee organizationEmployee = new OrganizationEmployee(employee: invitee, organization: organization, status: EmployeeStatus.PENDING, position: EmployeePosition.TRAINEE).save()
        InviteToOrganizationRequest request = new InviteToOrganizationRequest(requestedBy: inviter.profile, employee: invitee, organization: organization).save()
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION
        String subject = String.format(easyVisaSystemMessageType.subject, organization.name)
        String source = "Admin of ${organization.name}"
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, invitee.user, source, null, subject)
        return request
    }

    JoinOrganizationRequest validateAndCreateJoinOrganizationRequest(Organization organization, Employee requester, String adminEmail) {
        Employee admin = attorneyService.findEmployeeByEmailWithIgnoreCase(adminEmail)
        if (!admin) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'the.email.entered.does.not.match.an.admin.at.that.organization')
        }

        permissionsService.assertIsActive(requester.user)
        permissionsService.assertIsPaid()
        permissionsService.assertIsAdmin(admin.user, organization)

        if (requester.instanceOf(LegalRepresentative)) {
            LegalRepresentative legalRepresentative = LegalRepresentative.get(requester.id)
            if (legalRepresentative.representativeType == RepresentativeType.ATTORNEY) {
                if (organization.organizationType != OrganizationType.LAW_FIRM) {
                    throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'attorneys.can.only.join.law.firms')
                }
            }
            if (legalRepresentative.representativeType == RepresentativeType.ACCREDITED_REPRESENTATIVE) {
                if (organization.organizationType != OrganizationType.RECOGNIZED_ORGANIZATION) {
                    throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'ar.can.only.join.ro')
                }
            }
            if (isEmployeeActiveInLawFirm(requester)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'you.must.leave.your.current.law.firm.before.joining.another')
            }
            if (organization.organizationType == OrganizationType.SOLO_PRACTICE) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'you.cannot.join.a.solo.practice')
            }
        } else {
            if (OrganizationEmployee.findByEmployeeAndStatus(requester, EmployeeStatus.ACTIVE)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'you.must.leave.your.current.organization.before.requesting.to.join.a.new.one')
            }
        }

        JoinOrganizationRequest organizationRequest = new JoinOrganizationRequest(organization: organization, employee: requester)
        organizationRequest.requestedBy = requester.profile
        organizationRequest.save(failOnError: true)
        getOrganizationAdmins(organization).each { Employee employee ->
            EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST
            String subject = String.format(easyVisaSystemMessageType.subject, organization.name)
            alertService.createProcessRequestAlert(organizationRequest, easyVisaSystemMessageType , employee.user, requester.user.profile.fullName, null, subject)
        }
        organizationRequest
    }

    JoinOrganizationRequest cancelJoinOrganizationRequest(JoinOrganizationRequest organizationRequest) {
        organizationRequest.state = ProcessRequestState.CANCELLED
        organizationRequest.save(failOnError: true)
    }

    InviteToCreateOrganizationRequest createInviteToCreateOrganizationRequest(LegalRepresentative representative, User invitedBy) {
        Profile profile = Profile.findByUser(invitedBy)
        InviteToCreateOrganizationRequest request = new InviteToCreateOrganizationRequest(representative: representative, requestedBy: profile)
        request.save(failOnError: true)
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION
        String subject = String.format(easyVisaSystemMessageType.subject, representative.profile.name)
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, representative.user, invitedBy.profile.fullName, null, subject)
        request
    }


    List<Employee> getOrganizationAdmins(Organization organization) {
        OrganizationEmployee.createCriteria().list() {
            projections {
                property('employee')
            }
            eq('organization', organization)
            eq('isAdmin', true)
            eq('status', EmployeeStatus.ACTIVE)
        } as List<Employee>
    }

    @Transactional
    Organization setProfilePicture(Organization organization, MultipartFile file, Profile profile) {
        organization.logoFile = fileService.addOrganizationPhoto(organization, file, profile)
        organization.save()
    }

    /**
     * It handles operations for leaving an org.
     * Regular employee will be deactivated in an org.
     * Attorney will be validated on existing assigned package, if there are no non CLOSED package
     * an Attorney will be terminated.
     * However, if there are only two Attorneys a law firm will be deactivated and
     * their packages will be moved the solo practice orgs respectively.
     * @param organization organization
     * @param employee employee
     */
    @Transactional
    void leaveOrganization(Organization organization, Employee employee) {
        OrganizationEmployee organizationEmployee =
                OrganizationEmployee.findByOrganizationAndEmployeeAndStatus(organization, employee,
                        EmployeeStatus.ACTIVE)
        if (organizationEmployee) {
            if (employee.legalRepresentative) {
                deactivateAttorneyInOrg(organizationEmployee)
            }
            //deactivate employee
            deactivateEmployee(organizationEmployee)
        } else {
            throw ExceptionUtils.createUnProcessableDataException('employee.not.active.in.organization',null,[organization.name])
        }
    }

    /**
     * Performs actions to deactivate Attorney in an org.
     * Please, be careful, it doesn't make all validation and it should be done in a caller method.
     * @param organizationEmployee OrganizationEmployee record
     */
    private deactivateAttorneyInOrg(OrganizationEmployee organizationEmployee) {
        Organization organization = organizationEmployee.organization
        if (organization.soloPractice
                && [ATTORNEY, EmployeePosition.PARTNER].contains(organizationEmployee.position)) {
            //Attorney can't leave own solo practice
            throw ExceptionUtils.createAccessDeniedException('you.cannot.leave.a.solo.practice')
        }
        Employee employee = organizationEmployee.employee
        ((LegalRepresentative) employee).attorneyType = AttorneyType.SOLO_PRACTITIONER
        employee.save(failOnError: true)
        List<LegalRepresentative> attorneys = getRepresentatives(organization.id)
        if (attorneys.size() > 2
                || [EmployeePosition.MANAGER, EmployeePosition.EMPLOYEE,
                    EmployeePosition.TRAINEE].contains(organizationEmployee.position)) {
            //deactivate attorney from a law firm or check that current legal rep with employee position
            //does not have any package dependencies
            if (organizationEmployee.isAdmin) {
                employeeService.checkOtherActiveAdmin(employee, organization)
            }
            deactivateAttorneyInLawFirm(organizationEmployee)
        } else if (organization.lawFirm) {
            //deactivate law firm
            deactivateLawFirmStuff(attorneys, employee as LegalRepresentative, organization)
        }
    }

    void deactivateAttorneyInLawFirm(OrganizationEmployee organizationEmployee) {
        Employee employee = organizationEmployee.employee
        Organization organization = organizationEmployee.organization
        validateAssignedPackages(employee, organization)
        PackageAssignee.executeUpdate('update PackageAssignee set status = :status, endDate = now() ' +
                'where representative = :attorney and organization = :org and status = :active',
                [status: PackageAssignmentStatus.INACTIVE, attorney: employee, org: organization,
                 active: PackageAssignmentStatus.ACTIVE,])
    }

    private void deactivateEmployee(OrganizationEmployee organizationEmployee) {
        organizationEmployee.status = EmployeeStatus.INACTIVE
        organizationEmployee.inactiveDate = new Date()
        organizationEmployee.save(failOnError: true)
    }

    private void validateAssignedPackages(LegalRepresentative representative, Organization organization) {
        List packages = Package.executeQuery('''select count(p.id) from Package p
                                    where p.attorney = :attorney
                                    and p.organization = :org
                                    and p.status <> :status''',
                [attorney: representative, org: organization, status: PackageStatus.CLOSED])
        if (packages && packages.first() > 0) {
            throw ExceptionUtils.createUnProcessableDataException('organization.not.transferred.packages', null,
                    [organization.organizationType.displayName], null, null,
                    ErrorMessageType.ASSIGNED_PACKAGES)
        }
    }

    void deactivateLawFirmStuff(List<LegalRepresentative> attorneys, LegalRepresentative requester, Organization organization) {
        OrganizationEmployee.executeUpdate('update OrganizationEmployee set status = :status, inactiveDate = now() ' +
                'where organization = :org and status = :active',
                ['status': EmployeeStatus.INACTIVE, org: organization, active: EmployeeStatus.ACTIVE])
        organization.save(flush: true)
        Date date = new Date()
        attorneys.each {
            LegalRepresentative attorney = it
            it.attorneyType = AttorneyType.SOLO_PRACTITIONER
            it.save(failOnError: true)
            //assumes only solo practice will be available
            Organization attorneySolo = OrganizationEmployee.findByEmployeeAndStatus(it, EmployeeStatus.ACTIVE)
                    .organization
            List<Package> packages = Package.findAllByAttorneyAndOrganization(it, organization)
            packages.each {
                PackageAssignee assignee = it.currentAssignee
                it.addToAssignees(new PackageAssignee(aPackage: it, representative: attorney,
                        organization: attorneySolo, startDate: date, status: PackageAssignmentStatus.ACTIVE))
                it.organization = attorneySolo
                assignee.endDate = date
                assignee.status = PackageAssignmentStatus.INACTIVE
                assignee.save(failOnError: true)
                it.save(failOnError: true)
            }
            if (attorney.id != requester.id) {
                doAsync({
                    sendOrgRetirementEmail(organization.id, attorney.profile.user.id)
                }, "Organization [${organization.id}] retirement ")
            }
        }
    }

    private void sendOrgRetirementEmail(Long organizationId, Long userId) {
        Organization organization = Organization.findById(organizationId)
        User user = User.findById(userId)
        EasyVisaSystemMessageType type = EasyVisaSystemMessageType.LEAVE_PARTNER_FROM_ORG
        String body = alertService.renderTemplate(type.templatePath, [organization: organization])
        alertService.createAlert(type, user, EvSystemMessage.EASYVISA_SOURCE, body)
    }

    List getOrganizationEmployees(Organization organization, FindOrganizationEmployeeCommand command) {
        final Sql sql = new Sql(dataSource)

        String query = 'select oe.*, e.id as employee_id, e.mobile_phone, e.office_phone, p.id as profile_id, ' +
                'p.first_name, p.middle_name, p.last_name, p.easy_visa_id, p.email, ' +
                '(SELECT array_agg(r.authority) FROM user_role ur INNER JOIN role r on r.id = ur.role_id WHERE ur.user_id = u.id) as roles ' +
                'from organization_employee oe ' +
                'inner join employee as e on oe.employee_id = e.id ' +
                'inner join profile as p on e.profile_id = p.id ' +
                'left join ev_user as u on p.user_id = u.id ' +
                'where oe.id = (select max(_oe.id) from organization_employee _oe ' +
                'where oe.employee_id = _oe.employee_id ' +
                'and _oe.organization_id = :organizationId) ' +
                'and oe.organization_id = :organizationId '

        Map queryParams = [organizationId: organization.id]

        if (!command.includeAll) {
            query += ' and oe.status = :status'
            queryParams['status'] = EmployeeStatus.ACTIVE.name()
        }

        if ('name' == (command.sort)) {
            query += ' order by p.last_name ' + command.sortOrder
        } else if ('status' == (command.sort)) {
            query += " order by array_position(ARRAY['PENDING', 'INACTIVE', 'ACTIVE']::varchar[], oe.status) " + command.sortOrder
        }

        def results = sql.rows(query, queryParams, command.offset, command.max)
        int totalCount = sql.rows(query, queryParams).size()
        return [results, totalCount]
    }

    @Transactional
    OrganizationEmployee setEmployeeToAdmin(OrganizationEmployee organizationEmployee) {
        if (organizationEmployee) {
            organizationEmployee.isAdmin = true
            organizationEmployee.save()
        } else {
            throw new EasyVisaException(errorMessageCode: 'employee.not.in.organization')
        }
    }

    @Transactional
    InviteToCreateOrganizationRequest validateAndCreateNewOrganizationCreationInvite(LegalRepresentative inviter, String email, String easyVisaId) {
        permissionsService.assertSoloPractitioner(inviter, 'Inviter')
        Profile profile = Profile.findByEasyVisaId(easyVisaId)
        if (!profile) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'no.user.with.evid')
        }
        permissionsService.assertIsPaid(profile.user)
        if (profile == inviter.profile) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'you.cant.invite.yourself.to.an.org')
        }
        if (!profile.email?.equalsIgnoreCase(email)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'input.email.does.not.match.evid')
        }
        Long applicantCount = Applicant.countByProfile(profile)
        if (applicantCount > 0) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'user.is.applicant.not.attorney')
        }
        LegalRepresentative invitee = LegalRepresentative.findByProfile(profile)
        if (!invitee) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'user.not.representative')
        }
        if (isEmployeeActiveInLawFirm(invitee)) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'representative.not.solo.practitioner', params: ['Invitee'])
        }
        OrganizationMenuCommand command = new OrganizationMenuCommand(status: EmployeeStatus.ACTIVE)
        if (organizations(inviter, command).size() > 1 || organizations(invitee, command).size() > 1) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY,
                    errorMessageCode: 'representative.multiple.orgs')
        }
        permissionsService.assertSoloPractitioner(inviter, 'Invitee')
        permissionsService.assertIsActive(invitee)
        createNewOrganizationCreationInvite(inviter, invitee)
    }

    @Transactional
    InviteToCreateOrganizationRequest createNewOrganizationCreationInvite(LegalRepresentative inviter, LegalRepresentative invitee) {
        if (InviteToCreateOrganizationRequest.countByRepresentativeAndState(inviter, ProcessRequestState.PENDING) > 0) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'inviter.has.pending.invite')
        }
        InviteToCreateOrganizationRequest createOrganizationRequest = new InviteToCreateOrganizationRequest(requestedBy: inviter.profile, representative: invitee)
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION
        String subject = String.format(easyVisaSystemMessageType.subject, inviter.profile.name)
        alertService.createProcessRequestAlert(createOrganizationRequest, easyVisaSystemMessageType, invitee.user, inviter.profile.fullName, null, subject)
        createOrganizationRequest
    }

    /**
     * This method finds distinct Organizations of logged in User
     * @param employee This is the instance of Employee
     * @param command This is the instance of OrganizationMenuCommand which has query params
     * @return List < Organization >  This returns matched Organization List
     */
    List<OrganizationEmployee> organizations(Employee employee, OrganizationMenuCommand command) {
        OrganizationEmployee.createCriteria().list() {
            eq('employee', employee)
            if (command.status) {
                eq('status', command.status)
            }
        } as List<OrganizationEmployee>
    }

    /**
     * This method finds attorney's solo organization.
     * @param attorney legal representative
     * @return Organization
     */
    Organization getSoloOrganization(LegalRepresentative attorney) {
        findOrg(attorney, OrganizationType.SOLO_PRACTICE)
    }

    /**
     * This method finds attorney's law firm organization.
     * @param attorney legal representative
     * @return Organization
     */
    Organization getLawFirmOrganization(LegalRepresentative attorney) {
        findOrg(attorney, OrganizationType.LAW_FIRM)
    }

    /**
     * This method checks if an employee is active in any organization that is a law firm
     * @param employee This is the instance of Employee
     * @return Boolean This returns true if employee exists and vice versa
     */
    Boolean isEmployeeActiveInLawFirm(Employee employee) {
        OrganizationEmployee.createCriteria().count {
            'organization' {
                eq('organizationType', OrganizationType.LAW_FIRM)
            }
            eq('status', EmployeeStatus.ACTIVE)
            eq('employee', employee)
        } > 0
    }

    /**
     * This method withdraws invitation sent to join organization by admin and deletes pending record on OrganizationEmployee
     * Note that InviteRequest and alerts are only created when inviting any attorney, and are null when inviting any new
     * employees. So, this method makes conditional checks for these fields, and only processes them when they are present.
     * @param invitee This is the instance of OrganizationEmployee which holds invitee information
     * @param user This is the instance of User which is the admin who withdraws the invitation
     */
    @Transactional
    void withdrawInvitation(OrganizationEmployee invitee, User user) {
        InviteToOrganizationRequest request = InviteToOrganizationRequest.findByOrganizationAndEmployee(invitee.organization, invitee.employee)
        if (request) {
            request.state = ProcessRequestState.CANCELLED
            request.save()
        }
        Employee employee = invitee.employee;
        Profile empProfile = employee.profile;

        invitee.delete(flush: true, failOnError: true);

        List<OrganizationEmployee> organizationEmployeeList = OrganizationEmployee.findAllByEmployee(employee);
        if(organizationEmployeeList.size() == 0){
            employee.delete();
            empProfile.delete();
        }

        RegistrationCode.executeUpdate('delete from RegistrationCode where easyVisaId = :evid', [evid: empProfile.easyVisaId])

        if (invitee.employee.user) {
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_WITHDRAWN, invitee.employee.user, user.profile.fullName)
        }
    }

    private void doAsync(Runnable command, String name) {
        asyncService.runAsync(command, name)
    }

    /**
     * Searches for an organization where admin and target user all together.
     * @param targetUser target user
     * @param admin admin
     * @return organization
     */
    List<Organization> getUsersOrganization(Employee targetUser, Employee admin) {
        List<Organization> organizations = OrganizationEmployee.createCriteria().list {
            projections {
                property('organization')
            }
            eq('employee', targetUser)
        } as List<Organization>
        OrganizationEmployee.createCriteria().list {
            projections {
                property('organization')
            }
            eq('employee', admin)
            isNull('inactiveDate')
            eq('status', EmployeeStatus.ACTIVE)
            eq('isAdmin', true)
            'in'('organization', organizations)
        } as List<Organization>
    }

    private Organization findOrg(LegalRepresentative attorney, OrganizationType type) {
        OrganizationEmployee.createCriteria().get {
            projections {
                property('organization')
            }
            eq('employee', attorney)
            isNull('inactiveDate')
            eq('status', EmployeeStatus.ACTIVE)
            'organization' {
                eq('organizationType', type)
            }
            if (type == OrganizationType.SOLO_PRACTICE) {
                eq('position', ATTORNEY)
            }
        } as Organization
    }

}
