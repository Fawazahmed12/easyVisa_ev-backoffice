package com.easyvisa

import com.easyvisa.dto.FinancialResponseDto
import com.easyvisa.dto.TimelineDecimalItemResponseDto
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.NotificationType
import com.easyvisa.utils.ExceptionUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

class AttorneyController implements IErrorHandler {

    AttorneyService attorneyService
    MessageSource messageSource
    SpringSecurityService springSecurityService
    OrganizationService organizationService
    AccountService accountService
    PermissionsService permissionsService
    ProfileService profileService

    @Autowired
    USCISEditionDateService uscisEditionDateService

    /**
     * It configures user payments info, performs registration charge automatically and set registration status to
     * complete.
     * @param paymentMethodCommand payment method info
     */
    @Secured(value = [Role.ATTORNEY], httpMethod = 'POST')
    def completePayment(final PaymentMethodCommand paymentMethodCommand) {
        try {
            if (paymentMethodCommand.validate()) {
                LegalRepresentative currentAttorney = attorneyService.
                        findAttorneyByUser(springSecurityService.currentUserId as Long)
                currentAttorney = attorneyService.completePayment(currentAttorney, paymentMethodCommand)
                render(template: '/user/attorney', model: [legalRepresentative: currentAttorney],
                        status: HttpStatus.SC_OK)
            } else {
                throw new ValidationException('', paymentMethodCommand.errors)
            }
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode)
        }
        catch (final ValidationException e) {
            respond e.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    @Secured([Role.ATTORNEY, Role.USER])
    def validate(ValidateAttorneyCommand attorneyCommand) {
        LegalRepresentative representative =
                attorneyService.findRepresentativeByEmailAndEasyVisaId(attorneyCommand.email, attorneyCommand.easyVisaId)
        if (representative) {
            render(template: '/attorney/validate', model: [attorney: representative])
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'attorney.not.found')
        }
    }

    @Secured(Role.EMPLOYEE)
    def isAdmin(ValidateAttorneyCommand attorneyCommand) {
        Employee employee = attorneyService.findEmployeeByEmailAndEasyVisaId(attorneyCommand.email, attorneyCommand.easyVisaId)
        OrganizationEmployee organizationEmployee = OrganizationEmployee.findByEmployeeAndOrganizationAndStatus(employee, attorneyCommand.organization, EmployeeStatus.ACTIVE)
        if (organizationEmployee) {
            render([isAdmin : organizationEmployee.isAdmin,
                    position: organizationEmployee.position.name(),] as JSON)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured(Role.ATTORNEY)
    def createOrganizationInvite() {
        Long representativeId = request.JSON['representativeId'] as Long
        LegalRepresentative representative = LegalRepresentative.createCriteria().get() {
            eq('id', representativeId)
            profile {
                user {
                    eq('paid', true)
                    eq('activeMembership', true)
                    eq('enabled', true)
                }
            }
        }

        if (representative) {
            organizationService.createInviteToCreateOrganizationRequest(representative, springSecurityService.currentUser as User)
            render(['message': messageSource.getMessage('create.organization.invite.sent', null, request.locale)] as JSON)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    @Secured(Role.EMPLOYEE)
    def feeSchedule(Long id) {
        LegalRepresentative specifiedLegalRepresentative = LegalRepresentative.get(id)
        if (specifiedLegalRepresentative == null) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'attorney.not.found')
        }
        Employee legalRepresentative = attorneyService.findEmployeeByUser(springSecurityService.currentUserId as Long)

        permissionsService.assertIsActive(legalRepresentative)
        permissionsService.assertIsPaid()
        permissionsService.assertBelongToOneOrg(specifiedLegalRepresentative.user)

        render(view: '/attorney/feeSchedule', model: [feeSchedule: specifiedLegalRepresentative.feeScheduleForUI])
    }

    @Secured([Role.EMPLOYEE])
    def marketingDashboardData(Long representativeId, Long organizationId) {
        LegalRepresentative attorney = LegalRepresentative.get(representativeId)
        Organization organization = Organization.get(organizationId)
        financialValidation(attorney, organizationId, organization)
        List<Organization> organizations = getOrganizations(organization, attorney)
        render(attorneyService.getAttorneyProspectInfo(attorney, organizations) as JSON)
    }

    @Secured([Role.EMPLOYEE])
    def financialDashboardData(Long representativeId, Long organizationId) {
        LegalRepresentative attorney = LegalRepresentative.get(representativeId)
        Organization organization = Organization.get(organizationId)
        financialValidation(attorney, organizationId, organization)
        List<Organization> organizations = getOrganizations(organization, attorney)
        FinancialResponseDto result = new FinancialResponseDto()
        result.articleBonuses = accountService.timelineArticleTransactions(attorney.user.id, organizations)
        result.clientRevenue = attorneyService.attorneyRevenue(attorney, organizations)
        result.referralBonuses = attorneyService.attorneyReferralBonuses(attorney)
        render(result as JSON)
    }

    private List<Organization> getOrganizations(Organization organization, LegalRepresentative attorney) {
        List<Organization> result = organization ? [organization] : null
        User currentUser = springSecurityService.currentUser as User
        if (!result && attorney.user != currentUser) {
            result = organizationService.getUsersOrganization(attorney,
                    attorneyService.findEmployeeByUser(currentUser.id))
        }
        result
    }

    @Secured([Role.EMPLOYEE])
    def getNotifications(Long id, FindAttorneyNotificationsCommand command) {
        LegalRepresentative attorney = LegalRepresentative.get(id)
        if (!attorney) {
            throw ExceptionUtils.createNotFoundException('attorney.not.found')
        }
        permissionsService.assertEditAccess(attorney.user, null, Boolean.FALSE, Boolean.FALSE)
        List<EmailTemplate> emails = attorneyService.findAttorneyNotifications(attorney, command)
        render(view: '/attorney/notifications', model: [emails: emails])
    }

    @Secured([Role.EMPLOYEE])
    def updateNotifications(Long id) {
        def props = request.JSON
        Long activeOrgId = props.activeOrganizationId
        AttorneyNotificationsCommand attorneyNotificationsCommand = new AttorneyNotificationsCommand()
        bindData(attorneyNotificationsCommand, props, [exclude: 'activeOrganizationId'])

        if (attorneyNotificationsCommand.validate()) {
            LegalRepresentative attorney = LegalRepresentative.get(id)
            if (!attorney) {
                throw ExceptionUtils.createNotFoundException('attorney.not.found')
            }
            Organization org = Organization.get(activeOrgId)
            permissionsService.assertEditAccess(attorney.user, [org], Boolean.FALSE)
            List<EmailTemplate> emails = attorneyService.updateAttorneyNotifications(attorney,
                    attorneyNotificationsCommand.attorneyNotifications)
            render(view: '/attorney/notifications', model: [emails: emails])
        } else {
            respond attorneyNotificationsCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    @Secured([Role.EMPLOYEE])
    def notificationTypes() {
        render([clientInactivity  : convertNotificationTypes(NotificationType.clientInactivity),
                deadline          : convertNotificationTypes(NotificationType.deadline),
                importantDocuments: convertNotificationTypes(NotificationType.importantDocuments),
                blocked           : convertNotificationTypes(NotificationType.blocked),] as JSON)
    }


    @Secured([Role.OWNER])
    def updateUSCISEditionDates(final USCISEditionDatesCommand uscisEditionDatesCommand) {
        uscisEditionDatesCommand.validateUSCISEditionDates()
        User currentUser = springSecurityService.currentUser as User
        uscisEditionDateService.updateUSCISEditionDates(currentUser, uscisEditionDatesCommand)
        def uscisEditionDates = uscisEditionDateService.getUSCISEditionDates(uscisEditionDatesCommand.sort, uscisEditionDatesCommand.order)
        render uscisEditionDates as JSON
    }

    @Secured([Role.EMPLOYEE, Role.ATTORNEY])
    def getUSCISEditionDates(USCISEditionDatesGetCommand uscisEditionDatesGetCommand) {
        uscisEditionDatesGetCommand.validateUSCISEditionDateParam()
        def uscisEditionDates = uscisEditionDateService.getUSCISEditionDates(uscisEditionDatesGetCommand.sort, uscisEditionDatesGetCommand.order)
        render uscisEditionDates as JSON
    }

    @Secured([Role.EMPLOYEE, Role.ATTORNEY])
    def referral(AttorneyReferralCommand attorneyReferralCommand) {
        if (attorneyReferralCommand.validate()) {
            attorneyService.referral(attorneyReferralCommand.email)
            render([email: attorneyReferralCommand.email] as JSON)
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'default.invalid.email.message', [attorneyReferralCommand.email])
        }
    }

    @Secured([Role.EMPLOYEE, Role.ATTORNEY])
    def inviteColleagues(InviteColleaguesCommand inviteColleaguesCommand) {
        try {
            final LegalRepresentative representative = inviteColleaguesCommand.representative
            if (representative) {
                this.validateInviteColleaguesEmail(inviteColleaguesCommand)
                attorneyService.sendInviteToColleagues(inviteColleaguesCommand, representative.id)
                render(status: HttpStatus.SC_OK)
            } else {
                throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'attorney.not.found')
            }
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode, e.params)
        }
        catch (ValidationException e) {
            respond e.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    private void validateInviteColleaguesEmail(InviteColleaguesCommand inviteColleaguesCommand) {
        List<String> emailListWithoutProfile = [];
        inviteColleaguesCommand.splitEmails.each {
            String emailId = it.trim();
            Profile profile = profileService.findProfileWithUserByEmail(emailId);
            if (profile) {
                emailListWithoutProfile.add(emailId)
            }
        }
        if (emailListWithoutProfile.size()) {
            String emails = emailListWithoutProfile.join(", ");
            throw new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: 'email.already.registered.with.ev', params: [emails])
        }
    }

    private List convertNotificationTypes(List<NotificationType> types) {
        types.collect { [displayName: it.displayName, value: it.name()] }
    }

    private void financialValidation(LegalRepresentative attorney, Long organizationId, Organization organization) {
        if (!attorney) {
            throw ExceptionUtils.createUnProcessableDataException('attorney.not.found')
        }
        if (organizationId != null && !organization) {
            throw ExceptionUtils.createUnProcessableDataException('organization.not.found', null, [organizationId])
        }
        if (organization && !organizationService.doesEmployeeBelongToOrganization(attorney.id, organizationId, false)) {
            throw ExceptionUtils.createUnProcessableDataException('representative.not.active.in.organization')
        }
        permissionsService.assertEditAccess(attorney.user, organization ? [organization] : null)
    }

}

