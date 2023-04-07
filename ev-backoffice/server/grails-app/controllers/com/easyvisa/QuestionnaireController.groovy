package com.easyvisa

import com.easyvisa.dto.PackageApplicantProgressDto
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.dto.AnswerItemDto
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.dto.FieldItemDto
import com.easyvisa.questionnaire.services.JacksonJsonHelper
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.grails.web.json.JSONObject
import org.springframework.context.MessageSource

import java.time.LocalDate

@Secured([Role.EV])
@GrailsCompileStatic
class QuestionnaireController implements IErrorHandler {

    MessageSource messageSource
    PackageQuestionnaireService packageQuestionnaireService
    PackageService packageService
    PermissionsService permissionsService
    SpringSecurityService springSecurityService
    ApplicantService applicantService

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchQuestionnaire() {
        def questionnaireJSON = request.JSON as JSONObject
        String sectionId = questionnaireJSON.sectionId
        Long packageId = questionnaireJSON.packageId as int
        Long applicantId = questionnaireJSON.applicantId as int
        this.validatePackageReadAccess(packageId);
        this.validateSectionAvailability(packageId, sectionId);
        DisplayTextLanguage displayTextLanguage = this.getDisplayTextLanguage()
        LocalDate currentDate = this.getCurrentDate()
        FieldItemDto fieldItemDto = packageQuestionnaireService.fetchFormlyQuestionnaire(packageId, applicantId, sectionId,
                currentDate, displayTextLanguage)
        String jsonResponse = JacksonJsonHelper.toJson(fieldItemDto)
        render jsonResponse
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchQuestionnaireAnswers(Long packageId, Long applicantId, String sectionId) {
        this.validatePackageReadAccess(packageId);
        this.validateSectionAvailability(packageId, sectionId);
        DisplayTextLanguage displayTextLanguage = this.getDisplayTextLanguage()
        LocalDate currentDate = this.getCurrentDate()
        AnswerItemDto answerItemDto = packageQuestionnaireService.fetchFormlyAnswers(packageId, applicantId, sectionId,
                currentDate, displayTextLanguage)
        String jsonResponse = JacksonJsonHelper.toJson(answerItemDto)
        render jsonResponse
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchPackageSections(Long packageId) {
        this.validatePackageReadAccess(packageId);
        DisplayTextLanguage displayTextLanguage = this.getDisplayTextLanguage()
        LocalDate currentDate = this.getCurrentDate()
        def packageSections = packageQuestionnaireService.fetchPackageSections(packageId, currentDate, displayTextLanguage)
        render packageSections as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchQuestionnaireForms(Long packageId){
        this.validatePackageReadAccess(packageId);
        render packageQuestionnaireService.fetchQuestionnaireForms(packageId) as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchQuestionnaireCompletionWarning() {
        def questionnaireJSON = request.JSON as JSONObject
        String sectionId = questionnaireJSON.sectionId
        Long packageId = questionnaireJSON.packageId as int
        Long applicantId = questionnaireJSON.applicantId as int
        this.validatePackageReadAccess(packageId);
        this.validateSectionAvailability(packageId, sectionId);
        LocalDate currentDate = this.getCurrentDate()
        CompletionWarningDto completionWarningDto = packageQuestionnaireService.fetchQuestionnaireCompletionWarning(packageId, applicantId, sectionId, currentDate)
        String jsonResponse = JacksonJsonHelper.toJson(completionWarningDto)
        render jsonResponse
    }

    @Secured(value = [Role.USER], httpMethod = 'GET')
    def progress(Long id) {
        Package aPackage = Package.get(id)
        if (aPackage) {
            Applicant applicant = applicantService.findApplicantByUser(springSecurityService.currentUserId as Long)
            if (!(applicant && aPackage.doesUserBelongToPackage(applicant))) {
                throw ExceptionUtils.createAccessDeniedException('user.not.allowed.to.access.package')
            }
            LocalDate currentDate = this.getCurrentDate()
            List<PackageApplicantProgressDto> result = packageQuestionnaireService.calculateProgress(aPackage, currentDate)
            render result as JSON
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'package.not.found.with.id')
        }
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def fetchQuestionnaireAccessState(Long id) {
        this.validatePackageReadAccess(id)
        Package aPackage = Package.get(id)
        User user = springSecurityService.currentUser as User
        def questionnaireAccessState = packageQuestionnaireService.fetchQuestionnaireAccessState(aPackage, user)
        render questionnaireAccessState as JSON
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def syncAndCopyQuestionnaireAnswers() {
        def questionnaireJSON = request.JSON as JSONObject
        Long packageId = questionnaireJSON.packageId as int
        this.validatePackageReadAccess(packageId);
        LocalDate currentDate = this.getCurrentDate()
        packageService.generateAsyncQuestionnaireData(packageId, currentDate)
        render(status: HttpStatus.SC_OK)
    }

    private validateSectionAvailability(Long packageId, String sectionId) {
        if (packageQuestionnaireService.getSectionNode(packageId, sectionId) == null) {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND,
                    errorMessageCode: 'section.not.found.with.id',
                    params: [sectionId])
        }
    }

    private validatePackageReadAccess(Long packageId) {
        Package aPackage = Package.get(packageId)
        if (aPackage) {
            User user = springSecurityService.currentUser as User
            permissionsService.validatePackageReadAccess(user, aPackage)
        } else {
            throw new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: 'package.not.found.with.id')
        }
    }

    private DisplayTextLanguage getDisplayTextLanguage() {
        String languageCode = request.getHeader("Accept-Language")
        DisplayTextLanguage displayTextLanguage = DisplayTextLanguage.findByLanguageCode(languageCode) ?: DisplayTextLanguage.defaultLanguage;
        return displayTextLanguage
    }

    private LocalDate getCurrentDate() {
        String calenderDay = request.getHeader("Current-Date")
        LocalDate calendar = (calenderDay != null) ? DateUtil.localDate(calenderDay) : DateUtil.today()
        return calendar
    }
}
