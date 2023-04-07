package com.easyvisa

import com.easyvisa.dto.CardDetailsResponseDto
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.NumberUtils
import com.easyvisa.utils.StringUtils
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

@Secured(['ROLE_EV'])
class UserController implements IErrorHandler {

    MessageSource messageSource
    SpringSecurityService springSecurityService
    UserService userService
    AttorneyService attorneyService
    ApplicantService applicantService
    ProfileService profileService
    EmployeeService employeeService
    PaymentService paymentService
    PermissionsService permissionsService
    AccountService accountService

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def me() {
        User user = springSecurityService.currentUser
        render(template: '/user/user', model: getProfileTemplateModel(user), status: HttpStatus.SC_OK)
    }

    @Secured(value = [Role.EMPLOYEE], httpMethod = 'PATCH')
    def manageUserMembership(ActivationCommand activationCommand) {
        if (activationCommand.validate()) {
            User user = springSecurityService.currentUser
            if (activationCommand.activeMembership) {
                //validate the user is inactive, has correct payment method and do re-activation
                if (user.activeMembership) {
                    throw ExceptionUtils.createUnProcessableDataException('user.membership.already.active')
                }
                if (user.isRepresentative()) {
                    PaymentMethod paymentMethod = paymentService.getPaymentMethod(user)
                    if (!paymentMethod || paymentMethod.expired) {
                        throw ExceptionUtils.createUnProcessableDataException('payment.method.missed.for.reactivation')
                    }
                }
                userService.reactivateUser(user)
            } else {
                //validate the user is active and do deactivation
                if (!user.activeMembership) {
                    throw ExceptionUtils.createUnProcessableDataException('user.membership.already.canceled')
                }
                userService.deactivateUser(user)
            }
            render(template: '/user/user', model: getProfileTemplateModel(user), status: HttpStatus.SC_OK)
        } else {
            respond activationCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    @Secured(value = ['IS_AUTHENTICATED_FULLY'], httpMethod = 'DELETE')
    def deleteUser() {
        User user = springSecurityService.currentUser
        Long userId = user.id
//        Long applicantId
//        if (user.isApplicant()) {
//            applicantId = applicantService.findApplicantByUser(userId).id
//        }
        userService.deleteUser(user)
//        if (applicantId) {
//            applicantService.restoreDefaultQuestions(applicantId)
//        }
        render([id: userId] as JSON)
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def uploadProfilePicture(Long id, ProfilePictureCommand pictureCommand) {
        if (springSecurityService.currentUserId == id) {
            Profile profile = profileService.setProfilePicture(Profile.findByUser(springSecurityService.currentUser), pictureCommand.profilePhoto)
            render([url: profile.profilePhotoUrl] as JSON)
        } else {
            renderError(HttpStatus.SC_FORBIDDEN, 'can.only.upload.profile.picture.for.yourself')
        }
    }

    private Map getProfileTemplateModel(User user) {
        String profileTemplate = null
        Map profileMap = [:]
        List<String> roles = userService.getUserRoles(user)
        if (roles.contains(Role.ATTORNEY) || roles.contains(Role.OWNER)) {
            profileTemplate = '/user/attorneyBalance'
            profileMap = [legalRepresentative: attorneyService.findAttorneyByUser(user.id),
                          userBalance        : accountService.getBalance(user.id)]
            profileMap = addPendingProcessRequestsToMap(profileMap, user.profile)

        } else if (roles.contains(Role.USER)) {
            profileTemplate = '/user/applicantProfile'
            profileMap = [applicant: applicantService.findApplicantByUser(user.id)]
        } else if (roles.contains(Role.EMPLOYEE) || roles.contains(Role.EV)) {
            profileTemplate = '/user/employeeProfile'
            profileMap = [employee: attorneyService.findEmployeeByUser(user.id)]
        }
        [user: user, profileTemplate: profileTemplate, profileMap: profileMap]
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def getProfile() {
        User user = springSecurityService.currentUser
        if (user.isRepresentative()) {
            Map profileMap = [legalRepresentative: attorneyService.findAttorneyByUser(user.id)]
            InviteToCreateOrganizationRequest createOrgRequest = InviteToCreateOrganizationRequest.findByRequestedByAndState(user.profile, ProcessRequestState.PENDING)
            profileMap = addPendingProcessRequestsToMap(profileMap, user.profile)
            render(template: '/user/attorneyComplete', model: profileMap)
        } else if (user.isApplicant()) {
            render(template: '/user/applicantProfile', model: [applicant: applicantService.findApplicantByUser(user.id)])
        } else if (user.isEmployee()) {
            render(template: '/user/employeeProfile', model: [employee: attorneyService.findEmployeeByUser(user.id)])
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def editProfile(ProfileCommand profileCommand) {
        User user = springSecurityService.currentUser
        if (user.isRepresentative()) {
            LegalRepresentative representative = attorneyService.findAttorneyByUser(user.id)
            representative = attorneyService.edit(representative, profileCommand)
            attorneyService.calculateAttorneyMaxYearsLicensed(representative.id)
            render(template: '/user/attorneyComplete', model: [legalRepresentative: representative])
        } else if (user.isEmployee()) {
            Employee employee = attorneyService.findEmployeeByUser(user.id)
            employee = employeeService.updateEmployee(employee, profileCommand)
            render(template: '/user/employeeProfile', model: [employee: employee])
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def changePassword(ChangePasswordCommand changePasswordCommand) {
        if (changePasswordCommand.validate()) {
            User user = springSecurityService.currentUser as User
            render(['access_token': userService.changePassword(user, changePasswordCommand)] as JSON)
        } else {
            respond changePasswordCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    @Secured(['IS_AUTHENTICATED_FULLY'])
    def editEmail(ProfileCommand profileCommand) {
        String email = profileCommand.email
        if (StringUtils.isValidEmail(email)) {
            User user = springSecurityService.currentUser as User
            Profile profile = user.profile
            profileService.updateProfileEmail(profile, email)
            render([email: email] as JSON)
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'default.invalid.email.message', [email])
        }
    }

    @Secured([Role.EMPLOYEE])
    def getNotifications(Long id) {
        User user = User.get(id)
        if (user) {
            permissionsService.assertEditAccess(user, null, Boolean.FALSE, Boolean.FALSE)
            LegalRepresentative attorney = attorneyService.findAttorneyByUser(id)
            renderAttorneyPreferences(attorney.profile)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'user.not.found.with.id')
        }
    }

    @Secured([Role.EMPLOYEE])
    def updateNotifications(Long id) {
        // param activeOrg was not getting bound hence this workaround
        def props = request.JSON
        Long activeOrgId = props.activeOrganizationId

        EmailPreferencesCommand emailPreferenceCommand = new EmailPreferencesCommand()
        bindData(emailPreferenceCommand, props, [exclude: 'activeOrganizationId'])
        if (emailPreferenceCommand.validate()) {
            User user = User.get(id)
            Organization org = Organization.get(activeOrgId)
            permissionsService.assertEditAccess(user, [org], Boolean.FALSE)
            Profile profile = profileService.updateProfileEmailPreferences(user.profile, emailPreferenceCommand)
            renderAttorneyPreferences(profile)

        } else {
            respond emailPreferenceCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    private void renderAttorneyPreferences(Profile profile) {
        Set<EmailPreference> preferences = profile.emailPreferences
        List<EmailPreference> progress = prepareAttorneyPreferences(NotificationType.clientProgressTypes, preferences)
        List<EmailPreference> taskQueue = prepareAttorneyPreferences(NotificationType.taskQueueTypes, preferences)
        render(view: '/user/emailPreferences', model: [taskQueue: taskQueue, progress: progress])
    }

    private List<EmailPreference> prepareAttorneyPreferences(List<NotificationType> types,
                                                             Set<EmailPreference> preferences) {
        List<EmailPreference> result = []
        types.each {
            NotificationType type = it
            EmailPreference preference = preferences.find { it.type == type }
            result << (preference ?: new EmailPreference(type: type, preference: Boolean.FALSE))
        }
        result
    }

    /**
     * Provides payment method info.
     * @param id user id
     */
    @Secured(value = [Role.EMPLOYEE], httpMethod = 'GET')
    def getPaymentMethod(Long id) {
        checkUser(id)
        CardDetailsResponseDto cardDetails = paymentService.getCardDetails(id)
        render cardDetails as JSON
    }

    /**
     * Saves payment method info. Goes to Fattmerchant for crabbing details and saves to DB.
     * Returns collected info in the response.
     * @param id user id
     * @param paymentMethodCommand payment details to store
     */
    @Secured(value = [Role.EMPLOYEE], httpMethod = 'PUT')
    def savePaymentMethod(Long id, PaymentMethodCommand paymentMethodCommand) {
        if (paymentMethodCommand.validate()) {
            User user = User.findById(id)
            checkUser(user)
            CardDetailsResponseDto cardDetails
            try {
                cardDetails = paymentService.saveToken(id, paymentMethodCommand)
            } catch (EasyVisaException e) {
                //deleting created token on FM side if Avalara failed to validate billing address
                paymentService.deletePaymentToken(paymentMethodCommand.fmPaymentMethodId, id)
                throw e
            }
            String message
            ErrorMessageType type
            try {
                BigDecimal charged = accountService.payBalance(user)
                if (charged > 0) {
                    message = messageSource.getMessage('payment.charged.successful', [NumberUtils.formatMoneyNumber(charged)] as Object[], request.locale)
                    type = ErrorMessageType.PAYMENT_CHARGED
                }
            } catch (EasyVisaException e) {
                message = messageSource.getMessage(e.errorMessageCode, e.params as Object[], request.locale)
                type = ErrorMessageType.PAYMENT_FAILED
            }
            Map<String, Object> messageParam = [type: type?.name(), text: message]
            render([paymentMethod: cardDetails, message: messageParam, balance: accountService.getBalanceWithEstTaxes(id)] as JSON)
        } else {
            respond paymentMethodCommand.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    private void checkUser(Long userId) {
        User user = User.findById(userId)
        checkUser(user)
    }

    private void checkUser(User user) {
        if (!user) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'user.not.found.with.id')
        }
        User currentUser = springSecurityService.currentUser
        if (currentUser.activeMembership) {
            permissionsService.assertEditAccess(user)
        } else if (user.id != currentUser.id) {
            throw ExceptionUtils.createAccessDeniedException('notpermitted.error')
        }
    }

    /**
     * Returns user id by attorney id.
     * @param id easy visa id of the legal representative
     * @return user id
     */
    @Secured(value = ['permitAll'], httpMethod = 'GET')
    def getUserId(String id) {
        LegalRepresentative attorney = attorneyService.findRepresentativeByEasyVisaId(id)
        if (!attorney) {
            renderError(HttpStatus.SC_NOT_FOUND, 'attorney.not.found')
        } else if (!permissionsService.isBlessed(springSecurityService.currentUser)) {
            render status: HttpStatus.SC_FORBIDDEN
        } else {
            render(['id': attorney.profile.user.id] as JSON)
        }
    }

    @Secured(value = [Role.ATTORNEY, Role.EMPLOYEE], httpMethod = 'PATCH')
    def updateAttorney(final Long id, final AttorneyCommand attorneyCommand) {
        LegalRepresentative currentAttorney =
                attorneyService.findAttorneyByUser(springSecurityService.currentUserId as Long)
        if (id == currentAttorney?.id) {
            if (attorneyCommand.updatable) {
                attorneyService.updateAttorney(currentAttorney, attorneyCommand)
                render(template: '/user/attorney', model: [legalRepresentative: LegalRepresentative.get(id)],
                        status: HttpStatus.SC_OK)
            } else {
                respond 'Not updatable', [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
            }
        } else {
            LegalRepresentative targetAttorney = LegalRepresentative.get(id)
            if (targetAttorney) {
                permissionsService.assertEditAccess(targetAttorney.user)
                attorneyService.updateAttorneyFeeSchedule(targetAttorney, attorneyCommand.feeSchedule)
            } else {
                render status: HttpStatus.SC_FORBIDDEN
            }
        }
    }

    private Map addPendingProcessRequestsToMap(Map paramsMap, Profile profile) {
        List<ProcessRequest> pendingRequests = ProcessRequest.findAllByRequestedByAndState(profile, ProcessRequestState.PENDING) ?: []
        pendingRequests.each { ProcessRequest processRequest ->
            switch (processRequest.class) {
                case InviteToCreateOrganizationRequest: paramsMap['newFirmJoinDetails'] = processRequest; break;
                case JoinOrganizationRequest: paramsMap['firmRequestDetailsObj'] = processRequest; break;
            }
        }
        paramsMap
    }
}
