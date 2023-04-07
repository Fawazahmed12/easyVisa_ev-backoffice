package com.easyvisa

import com.easyvisa.dto.AttorneySearchResponseDto
import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.utils.StringUtils
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.util.Environment
import grails.validation.ValidationException
import org.apache.http.HttpStatus
import org.grails.web.json.JSONObject
import org.grails.web.servlet.mvc.exceptions.ControllerExecutionException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.security.web.csrf.CsrfToken

import javax.servlet.http.Cookie

/***
 * Actions in this controller should be publicly available
 */
@Secured(['permitAll'])
class PublicController implements IErrorHandler {

    SpringSecurityService springSecurityService
    MessageSource messageSource
    AdminService adminService
    UserService userService
    ProfileService profileService
    EvMailService evMailService
    AttorneyService attorneyService
    FileService fileService
    ArticleService articleService
    def accessTokenJsonRenderer
    ApplicantService applicantService

    @Value('${frontEndAppURL}')
    String frontEndAppURL

    @Value('${nonLegalRegistrationLink}')
    String nonLegalRegistrationLink

    @Value('${loginUrl}')
    String loginUrl

    def showAdminConfig() {
        AdminSettings adminSettings = adminService.adminSettings
        if (adminSettings.adminConfig) {
            render(view: '/admin/feeConfig', model: [feeConfig: adminSettings.adminConfig], status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'feeconfig.not.present')
        }
    }

    def showGovFee() {
        AdminSettings adminSettings = adminService.adminSettings
        if (adminSettings.adminConfig) {
            render(template: '/admin/govFeeConfig', model: [feeConfig: adminSettings.adminConfig], status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'feeconfig.not.present')
        }
    }

    def showFeeSchedule() {
        AdminSettings adminSettings = adminService.adminSettings
        if (adminSettings?.adminConfig?.attorney?.feeSchedule) {
            render(view: '/admin/feeSchedule', model: [feeSchedule: adminSettings.adminConfig.attorney.feeScheduleForUI],
                    status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'feeconfig.not.present')
        }
    }

    def verify() {
        JSONObject jsonObject = (JSONObject) request.JSON
        String token = jsonObject.token
        try {
            userService.verifyUser(token)
            response.status = HttpStatus.SC_OK
            render(['message': messageSource.getMessage('user.account.verified', null, request.locale)] as JSON)
        }
        catch (VerifyTokenExpiredException e) {
            RegistrationCode.withNewTransaction {
                profileService.createUserVerifyTokenAndSendEmail(e.profile)
            }
            renderError(e.errorCode, e.errorMessageCode, e.params)
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode, e.params)
        }
    }

    def register(UserRegistrationCommand command) {
        String validUserName = command.username.toLowerCase()
        command.username = validUserName
        try {
            Profile profile = profileService.createUserForProfile(command)
            UserDevice device = new UserDevice(user: profile.user, userAgent: request.getHeader('User-Agent'))
            profile.user.devices.add(device)
            profile.save(failOnError: true)
            AccessToken accessToken = profileService.loginUser(profile)
            response.setContentType('application/json')
            response.setStatus(HttpStatus.SC_OK)
            render accessTokenJsonRenderer.generateJson(accessToken)
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode)
        }
    }

    def validateToken() {
        JSONObject jsonObject = (JSONObject) request.JSON
        String token = jsonObject.token
        try {
            Profile profile = userService.findProfileByToken(token)
            render(template: '/user/profile', model: [profile: profile])
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode)
        }
    }

    def resetPassword() {
        JSONObject jsonObject = (JSONObject) request.JSON

        String token = jsonObject.token
        String password = jsonObject.password
        try {
            userService.changePassword(password, token)
            response.status = HttpStatus.SC_OK
            render(['message': messageSource.getMessage('password.reset.successfully', null, request.locale)] as JSON)
        }
        catch (EasyVisaException e) {
            renderError(e.errorCode, e.errorMessageCode)
        }
    }

    //TODO:FIX_TXN
    @Transactional
    def forgotUsername() {
        final JSONObject jsonObject = request.JSON as JSONObject
        String email = jsonObject.email
        if (email) {
            Profile profile = profileService.findProfileWithUserByEmail(email)
            if (profile) {
                User user = profile.user
                RegistrationCode registrationCode = new RegistrationCode(username: user.username)
                registrationCode.save()
                evMailService.sendForgotUsernameEmail(profile, registrationCode)
                response.status = HttpStatus.SC_OK
                render(['message': messageSource.getMessage('forgot.username.email.sent', null, request.locale)] as JSON)

            } else {
                renderError(HttpStatus.SC_NOT_FOUND, 'forgot.username.email.not.found')
            }
        } else {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'forgot.username.email.required')
        }
    }

    def showUsername() {
        JSONObject jsonObject = request.JSON as JSONObject
        final String token = jsonObject.token
        User user = userService.getUserNameByToken(token)
        response.status = HttpStatus.SC_OK
        render(['message': messageSource.getMessage('show.username.message', [user?.username] as Object[], request.locale), 'username': user.username] as JSON)
    }

    def save(AttorneyCommand attorneyCommand) {
        User user = new User()
        String validUserName = attorneyCommand.username.toLowerCase()
        user.with {
            username = validUserName
            language = attorneyCommand.language
            password = attorneyCommand.password
            devices.add(new UserDevice(user: it, userAgent: request.getHeader('User-Agent')))
        }
        Profile profile = new Profile(user: user)
        profile.with {
            lastName = attorneyCommand.lastName
            firstName = attorneyCommand.firstName
            middleName = attorneyCommand.middleName
            email = attorneyCommand.email
        }
        LegalRepresentative attorney = new LegalRepresentative(profile: profile)
        attorney.with {
            mobilePhone = attorneyCommand.mobilePhone
            practiceAreas = attorneyCommand.practiceAreas
            faxNumber = attorneyCommand.faxNumber
            it.profile.address = attorneyCommand.officeAddress
            officePhone = attorneyCommand.officePhone
            twitterUrl = attorneyCommand.twitterUrl
            youtubeUrl = attorneyCommand.youtubeUrl
            linkedinUrl = attorneyCommand.linkedinUrl
            websiteUrl = attorneyCommand.websiteUrl
            facebookUrl = attorneyCommand.facebookUrl
        }
        try {
            attorney = attorneyService.updateAttorneyOfficeAddress(attorney, attorneyCommand?.officeAddress)
            attorney = attorneyService.registerAttorney(attorney)

            render(template: '/user/attorney', model: [legalRepresentative: attorney, canIncludeUserName: true], status: HttpStatus.SC_CREATED)
        }
        catch (ValidationException e) {
            log.debug(e.stackTrace)
            respond e.errors, [status: HttpStatus.SC_UNPROCESSABLE_ENTITY]
        }
    }

    def checkUsername() {
        String username = request.JSON?.username
        if (!username) {
            render status: HttpStatus.SC_UNPROCESSABLE_ENTITY
        }
        String validUserName = username.toLowerCase();
        if (User.countByUsername(validUserName) > 0) {
            render(['valid': false, 'message': messageSource.getMessage('user.username.unique', null, request.locale)] as JSON)
        } else {
            User user = new User(username: validUserName)
            if (user.validate(['username'])) {
                render(['valid': true] as JSON)
            } else {
                render(['valid': false, 'message': messageSource.getMessage(user.errors.allErrors.first(), request.locale)] as JSON)
            }

        }
    }

    def validateEmail() {
        String email = request.JSON.email
        if (email) {
            boolean valid = true
            String message = ''
            if (StringUtils.cleanEmail(email) == email) {
                Profile profile = new Profile(email: email)
                valid = profile.validate(['email'])
                if (!valid) {
                    message = messageSource.getMessage(profile.errors.allErrors.first(), request.locale)
                }
            } else {
                valid = false
                message = messageSource.getMessage('profile.email.email.invalid', null, request.locale)
            }
            if (valid && profileService.findProfileByEmail(email)) {
                valid = false
                message = messageSource.getMessage('profile.email.unique', null, request.locale)
            }
            def resp = valid ? ['valid': true] : ['valid': false, 'message': message]
            render(resp as JSON)
        } else {
            render status: HttpStatus.SC_UNPROCESSABLE_ENTITY
        }
    }

    def forgotPassword() {
        JSONObject jsonObject = (JSONObject) request.JSON
        String email = jsonObject.email
        if (!email) {
            renderError(HttpStatus.SC_UNPROCESSABLE_ENTITY, 'forgot.password.email.required')
            return
        }
        Profile profile = profileService.findProfileByEmail(email)
        userService.createForgotPasswordTokenAndSendEmail(profile)
        response.status = HttpStatus.SC_OK
        render(['message': messageSource.getMessage('forgot.password.email.sent', null, request.locale)] as JSON)
    }

    def attorneySearchParams() {
        render attorneyService.attorneySearchConfig as JSON
    }

    def find(FindRepresentativeCommand findCommand) {
        PaginationResponseDto responseDto = attorneyService.findRepsForMarketingSite(findCommand)
        response.setIntHeader('X-total-count', responseDto.totalCount)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render(template: '/attorney/searchResults', model: [results: responseDto.result], status: HttpStatus.SC_OK)
    }

    def contactInfo(final Long id) {
        final LegalRepresentative currentAttorney = LegalRepresentative.get(id)
        if (currentAttorney) {
            render(template: '/attorney/contactInfo', model: [representative: currentAttorney], status: HttpStatus.SC_OK)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    def ratings(final Long id) {
        final LegalRepresentative currentAttorney = LegalRepresentative.get(id)
        if (currentAttorney) {
            List<Review> allReviews = attorneyService.getAttorneyReviews(currentAttorney)
            render(getRatingInfo(allReviews) as JSON)
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    def reviews(final Long id, final AttorneyReviewsCommand attorneyReviewsCommand) {
        final LegalRepresentative currentAttorney = LegalRepresentative.get(id)
        if (currentAttorney) {
            List<Review> reviews = attorneyService.getAttorneyReviews(currentAttorney, attorneyReviewsCommand)
            Long count = attorneyService.getAttorneyReviewsCount(currentAttorney, attorneyReviewsCommand)

            response.setIntHeader('X-total-count', count as int)
            response.setHeader('Access-Control-Expose-Headers', 'X-total-count')

            render(view: '/attorney/publicReviews', model: [reviews: reviews])
        } else {
            render status: HttpStatus.SC_NOT_FOUND
        }
    }

    def userProfilePicture(Long id) {
        Profile profile = Profile.findByUser(User.get(id))
        EasyVisaFile profilePhoto = profile?.profilePhoto
        if (profilePhoto) {
            try {
                render(file: fileService.getFile(profilePhoto), fileName: profilePhoto.originalName, contentType: "image/${profilePhoto.fileType}")
            }
            catch (EasyVisaException e) {
                renderError(e.errorCode, e.errorMessageCode)
            }
            catch (IOException ioe) {
                log.warn("IO Error when retriving file for EasyVisaFile ID : ${profilePhoto.id}")
                response.status = HttpStatus.SC_NOT_FOUND
            }
            catch (ControllerExecutionException cee) {
                handleFileDownloadError(cee);
            }
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
        }
    }

    def orgProfilePicture(Long id) {
        Organization organization = Organization.get(id)
        EasyVisaFile profilePhoto = organization?.logoFile
        if (profilePhoto) {
            try {
                render(file: fileService.getFile(profilePhoto), fileName: profilePhoto.originalName, contentType: "image/${profilePhoto.fileType}")
            }
            catch (EasyVisaException e) {
                renderError(e.errorCode, e.errorMessageCode)
            }
            catch (IOException ioe) {
                log.warn("IO Error when retriving file for EasyVisaFile ID : ${profilePhoto.id}")
                response.status = HttpStatus.SC_NOT_FOUND
            }
            catch (ControllerExecutionException cee) {
                handleFileDownloadError(cee);
            }
        } else {
            response.status = HttpStatus.SC_NOT_FOUND
        }
    }

    def organization(Long id) {
        try {
            Organization organization = Organization.get(id)
            if (organization) {
                render(view: '/organization/organization', model: [organization: organization])
            } else {
                renderError(HttpStatus.SC_NOT_FOUND, 'organization.not.found', [id])
            }
        }
        catch (EasyVisaException e) {
            response.status = e.errorCode
            render(['errors': [['code': e.errorCode, 'message': messageSource.getMessage(e.errorMessageCode, null, request.locale)]]] as JSON)
        }
    }

    def attorney(Long id) {
        AttorneySearchResponseDto result = attorneyService.getAttorneyPublicly(id)
        render(view: '/attorney/public', model: [result: result])
    }

    def articles(Long id, FindArticleCommand findArticleCommand) {
        def articles = articleService.getAttorneyArticles(findArticleCommand)
        response.setIntHeader('X-total-count', articles.totalCount ?: 0)
        response.setHeader('Access-Control-Expose-Headers', 'X-total-count')
        render(view: '/articles', model: [articles: articles], status: HttpStatus.SC_OK)
    }

    def article(Long id) {
        try {
            Article article = Article.get(id)
            if (article) {
                render(template: '/article/article', model: [article: article])
            } else {
                renderError(HttpStatus.SC_NOT_FOUND, 'article.not.found', [id])
            }
        }
        catch (EasyVisaException e) {
            response.status = e.errorCode
            render(['errors': [['code': e.errorCode, 'message': messageSource.getMessage(e.errorMessageCode, null, request.locale)]]] as JSON)
        }
    }


    private static Map getRatingInfo(List<Review> reviews) {
        Map result = ['total': reviews.size()]
        Map reviewsGroupedbyRating = reviews.groupBy { Math.floor(it.rating) }
        Map ratings = reviewsGroupedbyRating.collectEntries { k, v ->
            [(k as int): v.size()]
        }
        result['ratings'] = (5..1).collect { rating ->
            Map.Entry ratingEntry = ratings.find { it.key == rating }
            Map countMap = [value: rating, count: 0]

            if (ratingEntry) {
                countMap = [value: ratingEntry.key, count: ratingEntry.value]
            }
            countMap
        }
        result
    }

    def registerRedir(String token) {
        try {
            Applicant applicant = applicantService.findApplicantFromRegToken(token)
            // The applicant should not be allowed to register if they are not in the system.
            // This situation can happen, if the attorney invited an applicant, then deleted them.
            // So if user clicked on an invitation to register that is no longer valid, we could give an error message
            if(!applicant) {
                return redirect(url: "$frontEndAppURL$nonLegalRegistrationLink?invitation_status=rescinded")
            }
            String email = applicant.profile.email
            Profile profile = profileService.findProfileWithUserByEmail(email)
            //if profile having user exists associate this user's profile to applicant
            if (profile) {
                applicant.profile = profile
                applicant.save()
                redirect(url: frontEndAppURL + loginUrl)
            } else {
                redirect(url: "$frontEndAppURL$nonLegalRegistrationLink?token=${token}")
            }
        }
        catch (EasyVisaException e) {
            redirect(url: frontEndAppURL + loginUrl)
        }
    }

    def generateXSRF() {
        CsrfToken csrfToken = request.getAttribute('_csrf') as CsrfToken;
        Cookie csrfCookie = new Cookie('CSRF-TOKEN', csrfToken?.token);
        csrfCookie.setHttpOnly(false);
        csrfCookie.setPath("/");
        if (Environment.current != Environment.DEVELOPMENT) {
            csrfCookie.setDomain("easyvisa.com");
        }
        response.addCookie(csrfCookie);
        render([message: "CSRF Token Generated!"] as JSON);
    }
}
