package com.easyvisa

import com.easyvisa.enums.*
import com.easyvisa.utils.ExceptionUtils
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.http.HttpStatus

class ProcessService {

    PackageService packageService
    OrganizationService organizationService
    AlertService alertService
    AttorneyService attorneyService
    PermissionsService permissionsService
    SpringSecurityService springSecurityService
    UserService userService
    AccountService accountService

    @Transactional
    void handlePackageTransferRequest(PackageTransferRequest packageTransferRequest) {
        LegalRepresentative representative = packageTransferRequest.representative
        Organization organization = packageTransferRequest.representativeOrganization
        LegalRepresentative oldAssigneee = packageTransferRequest.oldAssignee
        if (organizationService.doesEmployeeBelongToOrganization(representative.id, organization.id)) {
            packageTransferRequest.packages.each {
                if (it.status == PackageStatus.LEAD) {
                    packageService.changePackageRepresentative(it, representative, oldAssigneee, organization)
                } else {
                    packageService.transferPackage(it, representative, oldAssigneee, organization, packageTransferRequest.requestedBy)
                }
            }
            alertService.createProcessRequestAlert(packageTransferRequest, EasyVisaSystemMessageType.PACKAGE_TRANSFER_ACCEPTED_RECIPIENT, representative.user, Alert.EASYVISA_SOURCE)
            alertService.createProcessRequestAlert(packageTransferRequest, EasyVisaSystemMessageType.PACKAGE_TRANSFER_ACCEPTED_OWNER, oldAssigneee.user, representative.user.profile?.fullName)
            packageTransferRequest.state = ProcessRequestState.ACCEPTED
            packageTransferRequest.save(failOnError: true)
        } else {
            throw ExceptionUtils.createUnProcessableDataException('process.invalid.representative.changed.organizations')
        }
    }

    /**
     * Deny invitation sent to create organization
     * @param request InviteToCreateOrganizationRequest object to change state
     */
    @Transactional
    void denyInvitationToCreateOrganization(InviteToCreateOrganizationRequest request) {
        LegalRepresentative inviter = attorneyService.findAttorneyByUser(request.requestedBy.user.id)
        LegalRepresentative invitee = request.representative
        validateAttorneyIsFreeAgent(inviter, 'user.not.representative')
        validateAttorneyIsFreeAgent(invitee, 'user.not.representative')

        request.state = ProcessRequestState.DECLINED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert?.isRead = true
        alert?.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_DENIED, request.requestedBy.user)
    }

    /**
     * Accepting invitation sent to create organization and declining all other invitations
     * @param request InviteToCreateOrganizationRequest object which holds organization information
     */
    @Transactional
    void acceptInvitationToCreateOrganization(InviteToCreateOrganizationRequest request) {
        LegalRepresentative inviter = attorneyService.findAttorneyByUser(request.requestedBy.user.id)
        LegalRepresentative invitee = request.representative

        validateAttorneyIsFreeAgent(inviter, 'user.not.representative')
        validateAttorneyIsFreeAgent(invitee, 'user.not.representative')

        Organization organization = organizationService.create("${inviter.profile.lastName} & ${invitee.profile.lastName}", OrganizationType.LAW_FIRM)

        inviter.attorneyType = AttorneyType.MEMBER_OF_A_LAW_FIRM
        organizationService.addAttorneyToOrganization(organization, inviter, EmployeePosition.PARTNER, true)
        inviter.save()

        invitee.attorneyType = AttorneyType.MEMBER_OF_A_LAW_FIRM
        organizationService.addAttorneyToOrganization(organization, invitee, EmployeePosition.PARTNER, true)
        invitee.save()

        request.state = ProcessRequestState.ACCEPTED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert.isRead = true
        alert.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_ACCEPTED, request.requestedBy.user)

        InviteToCreateOrganizationRequest.findAllByRepresentativeAndState(invitee, ProcessRequestState.PENDING).each {
            if (it.id != request.id) {
                it.state = ProcessRequestState.DECLINED
                it.save()
                alertService.createProcessRequestAlert(it, EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_DENIED, it.requestedBy.user)
            }
        }
    }

    /**
     * Withdraw an invite sent to a legal rep for creating an organization. Does following things -
     * 1. Set process request to CANCELLED
     * 2. Send an alert to the invited attorney indicating that the invite is withdrawn
     * @param processRequest - InviteOrganizationRequest that will be withdrawn
     * @return Updated processRequest with CANCELLED state
     */
    @Transactional
    InviteToCreateOrganizationRequest withDrawLegalRepInvite(InviteToCreateOrganizationRequest processRequest) {
        processRequest.state = ProcessRequestState.CANCELLED
        alertService.createProcessRequestAlert(processRequest,
                EasyVisaSystemMessageType.INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_CANCELLED,
                processRequest.representative.user, Alert.EASYVISA_SOURCE)
        processRequest.save()
    }

    /**
     * Accepting invitation sent to join organization
     * @param request InviteToOrganizationRequest object which holds invitation information
     */
    @Transactional
    void acceptInvitationToJoinOrganization(InviteToOrganizationRequest request) {
        Employee invitee = request.employee
        OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganizationAndStatus(invitee, request.organization, EmployeeStatus.PENDING)
        organizationEmployee.status = EmployeeStatus.ACTIVE

        if (invitee.instanceOf(LegalRepresentative)) {
            organizationEmployee.position = EmployeePosition.ATTORNEY
            User employeeUser = organizationEmployee.employee.user
            LegalRepresentative attorney = attorneyService.findAttorneyByUser(employeeUser.id);
            attorney.attorneyType = AttorneyType.MEMBER_OF_A_LAW_FIRM
        } else {
            organizationEmployee.position = EmployeePosition.TRAINEE
        }

        organizationEmployee.save()
        userService.assignRoleForBlessedOrg(request.organization, organizationEmployee.position, invitee.user, Role.findByAuthority(Role.EV))

        request.state = ProcessRequestState.ACCEPTED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert.isRead = true
        alert.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_ACCEPTED, request.requestedBy.user, invitee.user?.profile?.fullName)
    }

    @Transactional
    void validateAndAcceptInvitationToJoinOrganization(InviteToOrganizationRequest request) {
        validateInviteEmployeeToJoinOrganization(request, true)
        acceptInvitationToJoinOrganization(request)
    }

    /**
     * Deny invitation sent to join organization
     * @param request InviteToOrganizationRequest object to change state
     */
    @Transactional
    void denyInvitationToJoinOrganization(InviteToOrganizationRequest request) {
        OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganizationAndStatus(request.employee, request.organization, EmployeeStatus.PENDING)
        organizationEmployee.delete()

        request.state = ProcessRequestState.DECLINED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert?.isRead = true
        alert?.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_DENIED, request.requestedBy.user)
    }

    @Transactional
    void validateAndDeclineInvitationToJoinOrganization(InviteToOrganizationRequest request) {
        validateInviteEmployeeToJoinOrganization(request, false)
        denyInvitationToJoinOrganization(request)
    }

    void validateInviteEmployeeToJoinOrganization(ProcessRequest processRequest, Boolean isAccepted) {
        Employee invitee = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)
        Employee inviter = attorneyService.findEmployeeByUser(processRequest.requestedBy.user.id)
        if (!invitee) {
            throw ExceptionUtils.createAccessDeniedException('current.user.is.not.an.employee')
        }
        if (!inviter) {
            throw ExceptionUtils.createAccessDeniedException('requested.user.is.not.an.employee')
        }

        permissionsService.assertIsPaid(invitee.profile.user)
        permissionsService.assertIsActive(invitee)

        if (isAccepted) {
            InviteToOrganizationRequest request = InviteToOrganizationRequest.get(processRequest.id)

            if (!OrganizationEmployee.findByEmployeeAndOrganizationAndStatus(invitee, request.organization, EmployeeStatus.PENDING)) {
                alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_ALREADY_WITHDRAWN, invitee.user)
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'invitation.not.found.or.withdrawn')
            }
            if (invitee.instanceOf(LegalRepresentative)) {
                if (organizationService.isEmployeeActiveInLawFirm(invitee)) {
                    alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_ATTORNEY_ALREADY_ACTIVE, invitee.user)
                    throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'leave.your.law.firm.to.join.another')
                }
            } else {
                if (OrganizationEmployee.findByEmployeeAndStatus(invitee, EmployeeStatus.ACTIVE)) {
                    alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.INVITE_TO_ORGANIZATION_EMPLOYEE_ALREADY_ACTIVE, invitee.user)
                    throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'leave.your.organization.to.join.another')
                }
            }
        }
    }

    /***
     * Check following conditions for a legal rep -
     * 1. Paid
     * 2. Is active
     * 3. Is solo practitioner
     * 4. Is not an active member of any org.
     * @param legalRepresentative - legal rep to validate
     * @param messageCode - Error message code which will be passed with the exception
     */
    void validateAttorneyIsFreeAgent(LegalRepresentative legalRepresentative, String messageCode) {
        Profile profile = legalRepresentative?.profile
        permissionsService.assertIsPaid(profile.user)
        permissionsService.assertIsActive(legalRepresentative)
        permissionsService.assertSoloPractitioner(legalRepresentative)
        if (organizationService.isEmployeeActiveInLawFirm(legalRepresentative)) {
            throw ExceptionUtils.createAccessDeniedException(messageCode)
        }

    }

    /**
     * Accepting request to join organization by Admin
     * @param request JoinOrganizationRequest object which holds join request information
     */
    @Transactional
    def acceptJoinOrganizationRequest(JoinOrganizationRequest request) {
        Employee requester = request.employee
        Employee admin = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)
        User adminUser = admin?.profile?.user

        OrganizationEmployee organizationEmployee = new OrganizationEmployee(organization: request.organization, employee: requester, status: EmployeeStatus.ACTIVE)

        if (requester.instanceOf(LegalRepresentative)) {
            organizationEmployee.position = EmployeePosition.ATTORNEY
        } else {
            organizationEmployee.position = EmployeePosition.TRAINEE
        }

        organizationEmployee.save()
        userService.assignRoleForBlessedOrg(request.organization, organizationEmployee.position, requester.user, Role.findByAuthority(Role.EV))

        request.state = ProcessRequestState.ACCEPTED
        request.save()

        Alert alert = Alert.findByProcessRequestAndRecipient(request, adminUser)
        alert.isRead = true
        alert.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_ACCEPTED, request.requestedBy.user, alert.recipient.profile.name)
    }

    @Transactional
    void validateAndAcceptJoinOrganizationRequest(JoinOrganizationRequest request) {
        validateJoinOrganizationRequestForAccept(request)
        acceptJoinOrganizationRequest(request)
    }

    /**
     * Denying request to join organization by Admin
     * @param request JoinOrganizationRequest object to change state
     */
    @Transactional
    def denyJoinOrganizationRequest(JoinOrganizationRequest request) {
        request.state = ProcessRequestState.DECLINED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert?.isRead = true
        alert?.save()

        alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_DENIED, request.requestedBy.user)
    }

    @Transactional
    void validateAndDenyJoinOrganizationRequest(JoinOrganizationRequest request) {
        validateJoinOrganizationRequestForDeny(request)
        denyJoinOrganizationRequest(request)
    }

    def validateJoinOrganizationRequestForAccept(JoinOrganizationRequest request) {
        Employee requester = request.employee
        Employee admin = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)

        permissionsService.assertIsActive(admin.profile.user)
        permissionsService.assertIsPaid(admin.profile.user)

        if (request.state == ProcessRequestState.CANCELLED) {
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_WITHDRAWN, admin.user)
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'request.withdrawn.to.join.firm')
        }

        if (!OrganizationEmployee.findByOrganizationAndEmployeeAndStatusAndIsAdmin(request.organization, admin, EmployeeStatus.ACTIVE, true)) {
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_ADMIN, admin.user)
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_REQUESTER, request.requestedBy.user)
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'you.are.no.longer.admin')
        }

        if (requester.instanceOf(LegalRepresentative)) {
            if (organizationService.isEmployeeActiveInLawFirm(requester)) {
                alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_REQUESTER_ALREADY_IN_LAW_FIRM, admin.user)
                throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'requester.has.already.joined.another.law.firm')
            }
        } else {
            if (OrganizationEmployee.findByEmployeeAndStatus(requester, EmployeeStatus.ACTIVE)) {
                alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_REQUESTER_ALREADY_IN_LAW_FIRM, admin.user)
                throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'requester.has.already.joined.another.law.firm')
            }
        }
    }

    def validateJoinOrganizationRequestForDeny(JoinOrganizationRequest request) {
        Employee requester = request.employee
        Employee admin = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)

        permissionsService.assertIsActive(admin.profile.user)
        permissionsService.assertIsPaid(admin.profile.user)

        if (!OrganizationEmployee.findByOrganizationAndEmployeeAndStatusAndIsAdmin(request.organization, admin, EmployeeStatus.ACTIVE, true)) {
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_ADMIN, admin.user)
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_REQUESTER, request.requestedBy.user)
            throw ExceptionUtils.createAccessDeniedException('you.are.no.longer.admin')
        }

        if (request.state == ProcessRequestState.CANCELLED) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'request.withdrawn.to.join.firm')
        }

        if (requester.instanceOf(LegalRepresentative)) {
            if (organizationService.isEmployeeActiveInLawFirm(requester)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'requester.has.already.joined.another.law.firm')
            }
        } else {
            if (OrganizationEmployee.findByEmployeeAndStatus(requester, EmployeeStatus.ACTIVE)) {
                throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'requester.has.already.joined.another.law.firm')
            }
        }
    }

    /**
     * Accepting request to be added in Package for Petitioner
     * @param request PackageOptInForPetitionerRequest object which holds join request information
     */
    @Transactional
    def acceptPackageOptInForPetitionerRequest(PackageOptInForPetitionerRequest request) {
        request.petitioner.optIn = ProcessRequestState.ACCEPTED
        request.state = ProcessRequestState.ACCEPTED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        if (alert) {
            alert.isRead = true
            alert.save()
        }

        User recipient = request.requestedBy.user
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_ACCEPTED
        String aSubject = getPackageOptInSubject(recipient, easyVisaSystemMessageType)
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, recipient, request.petitioner.name, null, aSubject)
    }

    /**
     * Accepting request to be added in Package for ImmigrationBenefit.
     * Also it performs other different actions, for instance attorney charge.
     * @param request PackageOptInForImmigrationBenefitRequest object which holds join request information
     */
    def acceptPackageOptInForImmigrationBenefitRequest(PackageOptInForImmigrationBenefitRequest request) {
        acceptBeneficiaryOptIn(request)
        // We just don’t need to charge Attorneys if a package is in LEAD status
        Package aPackage = request.aPackage
        if (aPackage.status != PackageStatus.LEAD) {
            chargeImmigrationBenefit(request)
        }
    }

    /**
     * Accepting request to be added in Package for ImmigrationBenefit
     * @param request PackageOptInForImmigrationBenefitRequest object which holds join request information
     */
    @Transactional
    private void acceptBeneficiaryOptIn(PackageOptInForImmigrationBenefitRequest request) {
        request.immigrationBenefit.optIn = ProcessRequestState.ACCEPTED
        request.state = ProcessRequestState.ACCEPTED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert.isRead = true
        alert.save()

        User recipient = request.requestedBy.user
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_ACCEPTED
        String aSubject = getPackageOptInSubject(recipient, easyVisaSystemMessageType)
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, recipient, request.immigrationBenefit.applicant.name, null, aSubject)
    }

    /**
     * Charges attorney to new Applicant/changed benefit category.
     * @param request request
     */
    private void chargeImmigrationBenefit(PackageOptInForImmigrationBenefitRequest request) {
        Package aPackage = request.aPackage
        User user = aPackage.attorney?.user
        ImmigrationBenefit benefit = request.immigrationBenefit
        try {
            permissionsService.assertIsActive(user, 'payment.inactive.user')
            accountService.calculateBenefitCategoryToCharge(aPackage, benefit, user)
            chargeImmigrationBenefit(user, benefit)
        } catch (EasyVisaException e) {
            aPackage = aPackage.refresh()
            alertService.createProcessRequestAlert(request, EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED_ALERT,
                    user, EvSystemMessage.EASYVISA_SOURCE)
            Boolean sentWarnEmail = !aPackage.attorney.profile.getEmailPreference(NotificationType.ALERT)
            alertService.createPackageWarning(aPackage, benefit.applicant,
                    EasyVisaSystemMessageType.PACKAGE_ATTORNEY_CHARGE_FAILED, null, null, null, null,
                    EvSystemMessage.EASYVISA_SOURCE, sentWarnEmail)
        }
    }

    @Transactional
    private void chargeImmigrationBenefit(User user, ImmigrationBenefit benefit) {
        accountService.charge(user)
        benefit.paid = true
        benefit.save(failOnError: true)
    }

    /**
     * Denying request to be added in Package for Petitioner
     * @param request PackageOptInForPetitionerRequest object which holds join request information
     */
    @Transactional
    def denyPackageOptInForPetitionerRequest(PackageOptInForPetitionerRequest request) {
        request.petitioner.optIn = ProcessRequestState.DECLINED
        request.state = ProcessRequestState.DECLINED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert.isRead = true
        alert.save()

        User recipient = request.requestedBy.user
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_DENIED
        String aSubject = getPackageOptInSubject(recipient, easyVisaSystemMessageType)
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, recipient, request.petitioner.name, null, aSubject)
    }

    /**
     * Denying request to be added in Package for ImmigrationBenefit
     * @param request PackageOptInForImmigrationBenefitRequest object which holds join request information
     */
    @Transactional
    def denyPackageOptInForImmigrationBenefitRequest(PackageOptInForImmigrationBenefitRequest request) {
        request.immigrationBenefit.optIn = ProcessRequestState.DECLINED
        request.state = ProcessRequestState.DECLINED
        request.save()

        Alert alert = Alert.findByProcessRequest(request)
        alert.isRead = true
        alert.save()

        User recipient = request.requestedBy.user
        EasyVisaSystemMessageType easyVisaSystemMessageType = EasyVisaSystemMessageType.PACKAGE_OPTIN_REQUEST_DENIED
        String aSubject = getPackageOptInSubject(recipient, easyVisaSystemMessageType)
        alertService.createProcessRequestAlert(request, easyVisaSystemMessageType, recipient, request.immigrationBenefit.applicant.name, null, aSubject)
    }

    @Transactional
    void acceptHandleApplicantPackageTransferRequest(ApplicantPackageTransferRequest request) {
        LegalRepresentative representative = request.representative
        Organization organization = request.representativeOrganization
        LegalRepresentative oldAssignee = request.oldAssignee
        Package aPackage = request.aPackage
        if ([PackageStatus.BLOCKED].contains(aPackage.status)) {
            throw ExceptionUtils.createUnProcessableDataException('applicant.blocked.package.transfer')
        }
        if (organizationService.doesEmployeeBelongToOrganization(representative.id, organization.id)) {
            if (request.oldOrganization.id == organization.id) {
                packageService.changePackageRepresentative(aPackage, representative, oldAssignee, organization,
                        false)
            } else {
                packageService.transferPackage(aPackage, representative, oldAssignee, organization, request.requestedBy,
                        false)
            }
            alertService.createProcessRequestAlert(request,
                    EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_ACCEPTED, request.requestedBy.user, representative?.profile?.fullName)
            request.state = ProcessRequestState.ACCEPTED
            request.save(failOnError: true)
        } else {
            throw ExceptionUtils.createUnProcessableDataException('process.invalid.representative.changed.organizations')
        }
    }

    @Transactional
    void declineHandleApplicantPackageTransferRequest(ApplicantPackageTransferRequest request) {
        declineRequest(request)
        alertService.createProcessRequestAlert(request,
                EasyVisaSystemMessageType.APPLICANT_PACKAGE_TRANSFER_REQUEST_DENIED, request.requestedBy.user, request.representative?.profile?.fullName)
    }

    @Transactional
    void declineRequest(ApplicantPackageTransferRequest request) {
        request.state = ProcessRequestState.DECLINED
        request.save(failOnError: true)
    }

    @Transactional
    void denyPackageTransferRequest(PackageTransferRequest packageTransferRequest) {
        packageTransferRequest.state = ProcessRequestState.DECLINED
        LegalRepresentative representative = packageTransferRequest.representative
        Organization organization = packageTransferRequest.representativeOrganization
        LegalRepresentative oldAssigneee = packageTransferRequest.oldAssignee
        alertService.createProcessRequestAlert(packageTransferRequest, EasyVisaSystemMessageType.PACKAGE_TRANSFER_REJECTED_RECIPIENT,
                representative.user, springSecurityService.currentUser.profile.name)
        alertService.createProcessRequestAlert(packageTransferRequest, EasyVisaSystemMessageType.PACKAGE_TRANSFER_REJECTED_OWNER,
                packageTransferRequest.oldAssignee.user)
    }

    private String getPackageOptInSubject(User recipient, EasyVisaSystemMessageType easyVisaSystemMessageType) {
        Profile profile = Profile.findByUser(recipient)
        String aSubject = String.format(easyVisaSystemMessageType.subject, "${profile.firstName}, ${profile.lastName}")
        return aSubject
    }

    /**
     * Deletes process request and nullifies Alerts if exist.
     * @param request request to delete.
     */
    @Transactional
    void deleteProcessRequest(ProcessRequest request) {
        if (request) {
            List<Alert> alerts = Alert.findAllByProcessRequest(request)
            if (alerts) {
                alerts.each {
                    it.body = it.body ?: alertService.renderSystemMessageContent(it)
                    it.processRequest = null
                    it.save(failOnError: true)
                }
            }
            request.delete(failOnError: true)
        }
    }

}
