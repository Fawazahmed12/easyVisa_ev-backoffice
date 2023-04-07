package com.easyvisa

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.ErrorMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.dto.QuestionnaireResponseDto
import com.easyvisa.questionnaire.services.AnswerVisibilityEvaluatorService
import com.easyvisa.questionnaire.services.JacksonJsonHelper
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource

import java.time.LocalDate
import java.util.stream.Collectors

@SuppressWarnings('FactoryMethodName')
@GrailsCompileStatic
class AnswerController implements IErrorHandler {
    AnswerService answerService
    MessageSource messageSource

    @Autowired
    SectionCompletionStatusService sectionCompletionStatusService
    @Autowired
    PackageQuestionnaireService packageQuestionnaireService
    @Autowired
    AnswerVisibilityEvaluatorService answerVisibilityEvaluatorService

    SpringSecurityService springSecurityService

    @Secured([Role.EMPLOYEE, Role.USER])
    def validateAnswer(ValidateAnswerCommand validateAnswerCommand) {
        Answer answerToSave = validateAnswerCommand.toAnswer()
        answerToSave.validate()
        if (answerToSave.hasErrors()) {
            render status: HttpStatus.SC_UNPROCESSABLE_ENTITY
            return
        }
        this.validatePackageQuestionnaireAccess(answerToSave.packageId, answerToSave.questionId)
        LocalDate currentDate = this.getCurrentDate()
        AnswerValidationDto answerValidationDto = answerService.validateAnswer(answerToSave, currentDate)
        answerValidationDto.setHasAnswerCompleted(validateAnswerCommand.hasAnswerCompleted)
        render(answerValidationDto as JSON)
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def save(Answer answer) {
        answer.validate()
        if (answer.hasErrors()) {
            render status: HttpStatus.SC_UNPROCESSABLE_ENTITY
            return
        }
        this.validatePackageQuestionnaireAccess(answer.packageId, answer.questionId)
        LocalDate currentDate = this.getCurrentDate()
        Boolean evaluateRule = true
        Answer savedAnswer = answerService.saveAnswer(answer, evaluateRule, currentDate)

        render answerSaveCompletionHandler(savedAnswer.packageId, savedAnswer.applicantId, savedAnswer.sectionId, savedAnswer.questionId)
    }


    @Secured([Role.EMPLOYEE, Role.USER])
    def createRepeatingGroupInstance(RepeatingQuestionGroupCommand repeatingQuestionGroupCommand) {
        this.validatePackageQuestionnaireAccess(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.repeatingGroupId)
        LocalDate currentDate = this.getCurrentDate()
        answerService.addRepeatingGroupInstance(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.applicantId,
                repeatingQuestionGroupCommand.sectionId, repeatingQuestionGroupCommand.subsectionId,
                repeatingQuestionGroupCommand.repeatingGroupId, currentDate)
        render answerSaveCompletionHandler(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.applicantId,
                repeatingQuestionGroupCommand.sectionId, repeatingQuestionGroupCommand.repeatingGroupId)
    }

    @Secured([Role.EMPLOYEE, Role.USER])
    def removeRepeatingGroupInstance(RepeatingQuestionGroupCommand repeatingQuestionGroupCommand) {
        this.validatePackageQuestionnaireAccess(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.repeatingGroupId)
        LocalDate currentDate = this.getCurrentDate()
        answerService.removeRepeatingGroupInstance(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.applicantId,
                repeatingQuestionGroupCommand.sectionId, repeatingQuestionGroupCommand.subsectionId,
                repeatingQuestionGroupCommand.repeatingGroupId, repeatingQuestionGroupCommand.index, currentDate)
        // Call a seperate service method with a new transaction to reset the triggering questions answer
        answerService.resetTriggeringQuestionsOfRepeatingGroup(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.applicantId,
                repeatingQuestionGroupCommand.sectionId, repeatingQuestionGroupCommand.subsectionId,
                repeatingQuestionGroupCommand.repeatingGroupId, currentDate)
        response.status = HttpStatus.SC_OK
        render answerSaveCompletionHandler(repeatingQuestionGroupCommand.packageId, repeatingQuestionGroupCommand.applicantId,
                repeatingQuestionGroupCommand.sectionId, repeatingQuestionGroupCommand.repeatingGroupId)
    }

    private String answerSaveCompletionHandler(Long packageId, Long applicantId, String sectionId, String sourceFieldId) {
        LocalDate currentDate = this.getCurrentDate()
        DisplayTextLanguage displayTextLanguage = this.getDisplayTextLanguage()

        List<Answer> allAnswerList = this.answerService.fetchAnswers(packageId, applicantId)

        SectionNodeInstance answerPopulatedSection = this.packageQuestionnaireService
                .questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, allAnswerList, displayTextLanguage, currentDate)

        List<String> excludedPercentageCalculationQuestions = this.packageQuestionnaireService.
                getExcludedPercentageCalculationQuestions(answerPopulatedSection).collect {it.id};
        List<Answer> sectionAnswerList = allAnswerList.stream()
                .filter({ answerObj -> (answerObj.getSectionId() == sectionId) &&
                        !excludedPercentageCalculationQuestions.contains(answerObj.questionId)})
                .collect(Collectors.toList())

        AnswerEvaluationContext answerEvaluationContext = new AnswerEvaluationContext(packageId: packageId, applicantId: applicantId,
                answerList: allAnswerList, excludedPercentageCalculationQuestions: excludedPercentageCalculationQuestions)
        List<Answer> validSectionAnswerList = answerPopulatedSection.collectAllValidAnswers(answerEvaluationContext, this.answerVisibilityEvaluatorService)

        this.sectionCompletionStatusService.saveSectionCompletionStatus(packageId, applicantId, answerPopulatedSection, validSectionAnswerList, allAnswerList)
        this.sectionCompletionStatusService.updatedDependentSectionCompletion(packageId, applicantId, answerPopulatedSection, allAnswerList)
        this.sectionCompletionStatusService.removeUnusedQuestionAnswers(sectionAnswerList, validSectionAnswerList)

        Map updatedSectionInstanceData = [
                'allAnswerList'      : allAnswerList,
                'sectionNodeInstance': answerPopulatedSection
        ];

        this.packageQuestionnaireService.answerSaveCompletionHandler(updatedSectionInstanceData, packageId, currentDate, displayTextLanguage)
        QuestionnaireResponseDto questionnaireResponseDto = this.packageQuestionnaireService.buildQuestionnaireResponseDto(packageId, applicantId, sourceFieldId, updatedSectionInstanceData)
        return JacksonJsonHelper.toJson(questionnaireResponseDto)
    }

    private void validatePackageQuestionnaireAccess(Long packageId, String sourceFieldId) {
        Package packageInstance = Package.get(packageId)
        if (packageInstance == null) {
            throw ExceptionUtils.createNotFoundException('package.not.found.with.id')
        }

        User user = springSecurityService.currentUser as User
        Map questionnaireAccessState = this.packageQuestionnaireService.fetchQuestionnaireAccessState(packageInstance, user)
        Boolean hasQuestionnaireAccess = questionnaireAccessState.access as Boolean
        Boolean hasReadOnlyQuestionnaire = questionnaireAccessState.readOnly as Boolean
        if (!hasQuestionnaireAccess || hasReadOnlyQuestionnaire) {
            throw ExceptionUtils.createAccessDeniedException('user.not.allowed.to.access.questionnaire', null, [sourceFieldId], ErrorMessageType.INVALID_QUESTIONNAIRE_ACCESS)
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
