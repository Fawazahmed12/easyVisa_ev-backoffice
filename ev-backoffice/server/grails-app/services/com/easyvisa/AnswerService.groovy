package com.easyvisa

import com.easyvisa.enums.DataTypeConstant
import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.RepeatingQuestionGroupNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerValidationRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.RepeatingQuestionGroup
import com.easyvisa.questionnaire.repositories.QuestionDAO
import com.easyvisa.questionnaire.repositories.RepeatingQuestionGroupDAO
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.easyvisa.questionnaire.services.RuleActionHandler
import com.easyvisa.questionnaire.services.RuleEvaluator
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.TypeCheckingMode
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate
import java.util.stream.Collectors

@GrailsCompileStatic
class AnswerService {

    QuestionnaireService questionnaireService
    QuestionnaireTranslationService questionnaireTranslationService
    QuestionDAO questionDAO
    RepeatingQuestionGroupDAO repeatingQuestionGroupDAO
    RuleEvaluator ruleEvaluator
    RuleActionHandler ruleActionHandler
    SpringSecurityService springSecurityService
    LocalDate defaultCurrentDate = DateUtil.today()

    @Transactional
    AnswerValidationDto validateAnswer(Answer answerToSave, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(answerToSave.packageId)
        Question question = questionDAO.findByEasyVisaId(questionnaireVersion.questVersion, answerToSave.questionId)
        List<Answer> answerList = this.fetchAnswers(answerToSave.packageId, answerToSave.applicantId)
        Integer repeatingIndex = answerToSave.index
        QuestionNodeInstance questionNodeInstance = new QuestionNodeInstance(question, answerToSave, repeatingIndex,
                DisplayTextLanguage.defaultLanguage, currentDate, this.questionnaireTranslationService)
        AnswerValidationRuleEvaluationContext ruleEvaluationContext = new AnswerValidationRuleEvaluationContext(answerToSave, answerList, questionNodeInstance,
                answerToSave.packageId, answerToSave.applicantId)
        return ruleActionHandler.validateAnswer(questionNodeInstance.answerValidationRule, ruleEvaluationContext)
    }

    // 'evaluateRule': This will prevent rule from cyclic invokation.. Example: 'CentimeterToFeetConversionRule' and 'FeetToCentimeterConversionRule'
    @SuppressWarnings('ParameterReassignment')
    @Transactional
    Answer saveAnswer(Answer answer, Boolean evaluateRule = true, LocalDate currentDate = this.defaultCurrentDate) {
        Answer previousAnswer = Answer.findByPackageIdAndApplicantIdAndQuestionIdAndIndex(answer.packageId, answer.applicantId, answer.questionId, answer.index)?.clone()
        Answer savedAnswer = saveOrUpdate(answer)
        //Now take any action that this answer triggers
        String questionId = savedAnswer.questionId
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(savedAnswer.packageId)
        Question question = questionDAO.findByEasyVisaId(questionnaireVersion.questVersion, questionId)

        addDependentDefaultRepeatingGroupIfRequired(questionnaireVersion.questVersion, question, savedAnswer, currentDate)

        if (question.actionable && evaluateRule) {
            List<Answer> answerList = this.fetchAnswers(savedAnswer.packageId, savedAnswer.applicantId)
            Integer repeatingIndex = savedAnswer.index
            QuestionNodeInstance questionNodeInstance = new QuestionNodeInstance(question, savedAnswer, repeatingIndex,
                    DisplayTextLanguage.defaultLanguage, currentDate, this.questionnaireTranslationService)
            Outcome nodeRuleOutCome = ruleEvaluator.evaluateQuestion(answerList, questionNodeInstance)
            if (nodeRuleOutCome.isSuccessfulMatch()) {
                NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance, savedAnswer.packageId,
                        savedAnswer.applicantId)
                ruleActionHandler.triggerFormActionOnSuccessfulNodeRule(ruleEvaluationContext, previousAnswer)
            }
        }
        savedAnswer
    }

    @Transactional
    List<Answer> addRepeatingGroupInstance(Long packageId, Long applicantId, String sectionId, String subsectionId,
                                           String repeatingGroupId, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Question> questionList = this.findQuestionsOfRepeatingGroupByEasyVisaId(questionnaireVersion.questVersion, repeatingGroupId)
        // Repeating Group will have atleast one question so safe to assume the existence
        def maxIndex = currentMaxIndex(packageId, applicantId, sectionId, subsectionId, questionList)
        if (maxIndex == null) {
            maxIndex = 0
        } else {
            maxIndex += 1
        }
        List<Answer> answerList = questionList.collect { question ->
            List answerPathParts = [sectionId, subsectionId, question.id, maxIndex]
            String answerPath = answerPathParts.join('/')
            Answer answer = new Answer(packageId: packageId, applicantId: applicantId, index: maxIndex,
                    sectionId: sectionId, subsectionId: subsectionId, questionId: question.id, path: answerPath)
            saveOrUpdate(answer)
        }

        this.executeRepeatGroupLifeCycleRule(questionnaireVersion.questVersion, packageId, applicantId, repeatingGroupId,
                maxIndex, maxIndex, currentDate, ruleActionHandler.&executeRepeatGroupOnEntryRule)
        answerList
    }

    @Transactional
    def removeRepeatingGroupInstance(Long packageId, Long applicantId, String sectionId, String subsectionId,
                                     String repeatingGroupId, Integer index, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Question> questionList = this.findLinkedQuestionsOfRepeatingGroupByEasyVisaId(questionnaireVersion.questVersion, repeatingGroupId)
        // Repeating Group will have atleast one question so safe to assume the existence
        def preExitMaxIndex = currentMaxIndex(packageId, applicantId, sectionId, subsectionId, questionList)
        this.executeRepeatGroupLifeCycleRule(questionnaireVersion.questVersion, packageId, applicantId, repeatingGroupId,
                index, preExitMaxIndex, currentDate, ruleActionHandler.&executeRepeatGroupOnPreExitRule)

        questionList.each {
            question ->
                Answer answer = Answer.findByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionIdAndIndex(packageId, applicantId, sectionId,
                        subsectionId, question.id, index)
                if (answer) {
                    answer.delete(flush: true)
                }
        }

        // decrement the answer index greater than the removed one
        questionList.each {
            question ->
                List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionIdAndIndexGreaterThan(packageId, applicantId,
                        sectionId, subsectionId, question.id, index)
                answerList.each {
                    answer ->
                        answer.setIndex(answer.index - 1)
                        List answerPathParts = [answer.sectionId, answer.subsectionId, answer.questionId, answer.getIndex()]
                        answer.setPath(answerPathParts.join('/'))
                        saveOrUpdate(answer)
                }
        }

        // Repeating Group will have atleast one question so safe to assume the existence
        def postExitMaxIndex = currentMaxIndex(packageId, applicantId, sectionId, subsectionId, questionList)
        this.executeRepeatGroupLifeCycleRule(questionnaireVersion.questVersion, packageId, applicantId, repeatingGroupId,
                index, postExitMaxIndex, currentDate, ruleActionHandler.&executeRepeatGroupOnPostExitRule)
    }


    def executeRepeatGroupLifeCycleRule(String questVersion, Long packageId, Long applicantId, String repeatingGroupId,
                                        Integer answerIndex, Integer totalRepeatCount, LocalDate currentDate, Closure lifeCycleHandler) {
        RepeatingQuestionGroup repeatingQuestionGroup = repeatingQuestionGroupDAO.findByEasyVisaId(questVersion, repeatingGroupId)
        if (repeatingQuestionGroup && StringUtils.isNotEmpty(repeatingQuestionGroup.lifeCycleRule)) {
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance = new RepeatingQuestionGroupNodeInstance(repeatingQuestionGroup, answerIndex, totalRepeatCount,
                    DisplayTextLanguage.defaultLanguage, currentDate)
            List<Answer> answerList = this.fetchAnswers(packageId, applicantId)
            NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, repeatingQuestionGroupInstance, packageId, applicantId)
            lifeCycleHandler(repeatingQuestionGroup.lifeCycleRule, ruleEvaluationContext)
        }
    }


    @Transactional
    def resetTriggeringQuestionsOfRepeatingGroup(Long packageId, Long applicantId, String sectionId,
                                                 String subsectionId, String repeatingGroupId,
                                                 LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Question> questionList = this.findQuestionsOfRepeatingGroupByEasyVisaId(questionnaireVersion.questVersion, repeatingGroupId)
        // Repeating Group will have atleast one question so safe to assume the existence
        def maxIndex = currentMaxIndex(packageId, applicantId, sectionId, subsectionId, questionList)
        RepeatingQuestionGroup repeatingQuestionGroup = repeatingQuestionGroupDAO.findByEasyVisaId(questionnaireVersion.questVersion, repeatingGroupId)
        if (repeatingQuestionGroup && StringUtils.isNotEmpty(repeatingQuestionGroup.resetRuleName) && maxIndex == null) {
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance = new RepeatingQuestionGroupNodeInstance(repeatingQuestionGroup, 0, 0,
                    DisplayTextLanguage.defaultLanguage, currentDate)
            List<Answer> answerList = this.fetchAnswers(packageId, applicantId)
            NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, repeatingQuestionGroupInstance, packageId, applicantId)
            ruleActionHandler.resetTriggeringQuestionsNodeRule(repeatingQuestionGroup.resetRuleName, ruleEvaluationContext)
        }
    }

    /** Exclude Labels from being counted as Question.
     *  Labels wont have any answer or repeating group children **/
    private List<Question> findQuestionsOfRepeatingGroupByEasyVisaId(String questVersion, String repeatingGroupId) {
        List<Question> resultItems = questionDAO.findQuestionsOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId)
        List<Question> questionNodeList = resultItems.stream()
                .filter({ childNode -> (childNode instanceof Question) && (((Question) childNode).inputType != InputTypeConstant.LABEL.value) })
                .map({ childNode -> (Question) childNode })
                .collect(Collectors.toList())
        return questionNodeList
    }

    /** Exclude Labels from being counted as Question.
     *  Labels wont have any answer or repeating group children **/
    private List<Question> findLinkedQuestionsOfRepeatingGroupByEasyVisaId(String questVersion, String repeatingGroupId) {
        List<Question> resultItems = questionDAO.findLinkedQuestionsOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId)
        List<Question> questionNodeList = resultItems.stream()
                .filter({ childNode -> (childNode instanceof Question) && (((Question) childNode).inputType != InputTypeConstant.LABEL.value) })
                .map({ childNode -> (Question) childNode })
                .collect(Collectors.toList())
        return questionNodeList
    }


    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    private Integer currentMaxIndex(Long packageId, Long applicantId, String sectionId, String subsectionId, List<Question> questionList) {
        def answerCriteria = Answer.createCriteria()
        Integer result = answerCriteria.get {
            and {
                eq('packageId', packageId)
                eq('applicantId', applicantId)
                or {
                    questionList.collect {
                        ilike('path', "${[sectionId, subsectionId, it.id].join('/')}%")
                    }
                }
            }

            projections {
                max('index')
            }
        }
        result
    }


    /*
        This method is used to get all the questionnaire answers for the given packageId
        A Package may have derivative beneficiaries too in its applicant-list.
        But Only we can add a answer for 'Petitioner' and 'Principal-Beneficiary' applicants
        To apply this filter we have added 'applicantIdList' in the Answer query
     */

    @Transactional
    List<Answer> fetchPackageAnswers(Long packageId) {
        Package aPackage = Package.get(packageId)
        List<Long> applicantIdList = [aPackage.petitioner?.applicant?.id, aPackage.principalBeneficiary?.id].stream()
                .filter({ applicantId -> applicantId != null })
                .collect(Collectors.toList())

        List<Answer> answerList = Answer.createCriteria().list() {
            eq('packageId', packageId)
            'in'("applicantId", applicantIdList)
        } as List<Answer>
        answerList
    }


    @Transactional
    List<Answer> fetchAnswers(Long packageId, Long applicantId, List<String> sectionIdList) {
        List<Answer> answerList = Answer.createCriteria().list() {
            eq('packageId', packageId)
            eq('applicantId', applicantId)
            'in'("sectionId", sectionIdList)
        } as List<Answer>
        answerList
    }


    @Transactional
    List<Answer> fetchAnswers(Long packageId, Long applicantId) {
        List<Answer> answerList = Answer.createCriteria().list() {
            eq('packageId', packageId)
            eq('applicantId', applicantId)
        } as List<Answer>
        answerList
    }


    @Transactional
    def populateAutoFields(Long packageId, Long applicantId, Map<String, String> fieldsToTransfer) {
        fieldsToTransfer.each { String sourcePath, String destinationPath ->
            Answer sourceAnswer = Answer.findByPackageIdAndApplicantIdAndPath(packageId, applicantId, sourcePath)
            String sourceFieldValue = sourceAnswer?.value ?: ''

            Answer destinationAnswer = this.getAnswerInstance(packageId, applicantId, destinationPath, sourceFieldValue)
            saveOrUpdate(destinationAnswer)
        }
    }

    /**
     * EV-3330 Method to add empty values, in case --None-- is selected.
     * @param packageId
     * @param applicantId
     * @param fieldsToTransfer
     * @return
     */
    @Transactional
    def populateAutoFieldsWithEmptyValues(Long packageId, Long applicantId, Map<String, String> fieldsToTransfer) {
        fieldsToTransfer.each { String sourcePath, String _ ->
            Answer destinationAnswer = this.getAnswerInstance(packageId, applicantId, sourcePath, "")
            saveOrUpdate(destinationAnswer)


        }
    }


    @Transactional
    def removeAutoFillFields(Long packageId, Long applicantId, List<String> removableQuestionFields) {
        removableQuestionFields.each { fieldPath ->
            List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(packageId, applicantId, "${fieldPath}%")
            this.removeAnswers(answerList)
        }
    }


    @Transactional
    def removeApplicantSectionAnswers(Long packageId, Long applicantId, String sectionId) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
        this.removeAnswers(answerList)
    }


    @Transactional
    def removeAnswers(List<Answer> answerList) {
        answerList.each { answer ->
            Warning.findAllByAnswer(answer).each {
                Warning warning = it as Warning
                warning.delete(failOnError: true)
            }
            answer.delete(failOnError: true)
        }
    }


    private addDependentDefaultRepeatingGroupIfRequired(String questVersion, Question question, Answer answer, LocalDate currentDate) {
        String answerValue = EasyVisaNode.normalizeAnswer(answer.getValue())
        String relationshipType = EasyVisaNode.hasValidRelationshipType(answerValue) ? "has|${answerValue}" : "has"
        List<RepeatingQuestionGroup> repeatingQuestionGroupList = questionDAO.findDependentRepeatingGroups(questVersion, question.id, relationshipType)
        if (repeatingQuestionGroupList.isEmpty()) {
            return
        }

        RepeatingQuestionGroup repeatingQuestionGroup = repeatingQuestionGroupList[0]
        String repeatingGroupId = repeatingQuestionGroup.id
        this.addDefaultRepeatingGroupIfRequired(answer.packageId, answer.applicantId,
                answer.sectionId, answer.subsectionId, repeatingGroupId, currentDate)
    }


    @Transactional
    def addDefaultRepeatingGroupIfRequired(Long packageId, Long applicantId, String sectionId, String subsectionId,
                                           String repeatingGroupId, LocalDate currentDate) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(packageId)
        List<Question> questionList = this.findQuestionsOfRepeatingGroupByEasyVisaId(questionnaireVersion.questVersion, repeatingGroupId)
        String questionId = questionList[0].id

        // Check if it has a instance of repeatting group question
        Answer existingAnswer = Answer.findByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionIdAndIndex(packageId, applicantId, sectionId, subsectionId, questionId, 0)
        if (existingAnswer) {
            return
        }

        // Create a new Default Set of answers
        this.addRepeatingGroupInstance(packageId, applicantId, sectionId, subsectionId, repeatingGroupId, currentDate)
    }

    private static Answer getAnswerInstance(Long packageId, Long applicantId, String path, String value) {
        String[] pathInfoList = path.split("/")
        Answer answer = new Answer(packageId: packageId, applicantId: applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: value, path: path)
        if (pathInfoList.size() == 4) { // will hold repeating answers index
            Integer index = pathInfoList[3] as int
            answer.setIndex(index)
        }
        return answer
    }

    private Answer saveOrUpdate(Answer answer) {
        User user = springSecurityService.currentUser as User
        this.normalizeAnswerValue(answer)
        Answer savedAnswer = Answer.findByPackageIdAndApplicantIdAndQuestionIdAndIndex(answer.packageId, answer.applicantId, answer.questionId, answer.index)
        if (savedAnswer) {
            savedAnswer.value = answer.value
            savedAnswer.updatedBy = user
            savedAnswer.save(failOnError: true, flush: true)
            return savedAnswer
        }

        List answerPathParts = (answer.index >= 0) ? [answer.sectionId, answer.subsectionId, answer.questionId, answer.index] :
                [answer.sectionId, answer.subsectionId, answer.questionId]
        String answerPath = answerPathParts.join('/')
        answer.setPath(answerPath)
        answer.createdBy = user
        answer.updatedBy = user
        answer.save(failOnError: true, flush: true)
        return answer
    }


    private void normalizeAnswerValue(Answer answer) {
        if (!Answer.isValidAnswer(answer)) {
            return
        }

        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(answer.packageId)
        Question question = questionDAO.findByEasyVisaId(questionnaireVersion.questVersion, answer.questionId)
        if (StringUtils.equals(question.dataType, DataTypeConstant.DATE.value)) {
            answer.value = DateUtil.normalizeEasyVisaDateFormat(answer.value)
        }
    }


    Map<String, List<Answer>> fetchAnswersListGroupedBySection(Long packageId, Long applicantId) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantId(packageId, applicantId)
        def answersBySection = answerList.groupBy({ answer -> answer.sectionId })
        return answersBySection
    }


    @Transactional
    List<Answer> fetchRecentAnswers(List<Package> packages, Applicant applicant) {
        List<Answer> answerList = Answer.createCriteria().list() {
            'in'('packageId', packages.collect { it.id })
            'eq'('applicantId', applicant.id)
        } as List<Answer> ?: []

        def answersGroupedByPath = answerList.groupBy { it.path }
        List<Answer> recentAnswers = []
        answersGroupedByPath.each { path, groupedAnswers ->
            groupedAnswers.sort { it.lastUpdated }
            recentAnswers.push(groupedAnswers.first())
        }
        return recentAnswers
    }


    @Transactional
    List<Answer> copyApplicantAnswers(List<Answer> answerList, Package aPackage) {
        List<Answer> copiedAnswers = []
        Boolean evaluateRule = false
        answerList.each { answer ->
            Answer copyAnswer = answer.clone()
            copyAnswer.packageId = aPackage.id
            Answer savedAnswer = this.saveAnswer(copyAnswer, evaluateRule)
            copiedAnswers.push(savedAnswer)
        }
        return copiedAnswers
    }


    /**
     * Sets null to answers that user answered to other applicants.
     * @param user user
     */
    void nullifyUserInAnswers(User user) {
        Answer.executeUpdate('update Answer set createdBy = null where createdBy = :user', [user: user])
        Answer.executeUpdate('update Answer set updatedBy = null where updatedBy = :user', [user: user])
    }

}
