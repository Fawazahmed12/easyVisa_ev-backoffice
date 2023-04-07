package com.easyvisa

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.SectionCompletionState
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.SectionCompletionStatus
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.SectionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.CompletionPercentageRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.services.AnswerVisibilityEvaluatorService
import com.easyvisa.questionnaire.services.RuleActionHandler
import com.easyvisa.questionnaire.util.DateUtil
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovy.transform.TypeCheckingMode
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.util.function.Function
import java.util.stream.Collectors

@Service
@GrailsCompileStatic
class SectionCompletionStatusService {

    @Autowired
    AnswerService answerService

    @Autowired
    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    AnswerVisibilityEvaluatorService answerVisibilityEvaluatorService

    @Autowired
    RuleActionHandler ruleActionHandler

    LocalDate defaultCurrentDate = DateUtil.today()
    DisplayTextLanguage defaultDisplayTextLanguage = DisplayTextLanguage.defaultLanguage;

    @Transactional
    Map updateSectionCompletionStatus(Long packageId, Long applicantId, String sectionId,
                                      DisplayTextLanguage displayTextLanguage = this.defaultDisplayTextLanguage,
                                      LocalDate currentDate = this.defaultCurrentDate) {
        List<Answer> allAnswerList = this.answerService.fetchAnswers(packageId, applicantId)
        SectionNodeInstance answerPopulatedSection = this.updateApplicantSectionCompletionStatusAndRemoveZombieAnswers(packageId, applicantId, sectionId, allAnswerList,
                displayTextLanguage, currentDate)
        return [
                'allAnswerList'      : allAnswerList,
                'sectionNodeInstance': answerPopulatedSection
        ]
    }


    @Transactional
    private SectionNodeInstance updateApplicantSectionCompletionStatusAndRemoveZombieAnswers(Long packageId, Long applicantId, String sectionId,
                                                                                             List<Answer> allAnswerList, DisplayTextLanguage displayTextLanguage,
                                                                                             LocalDate currentDate) {
        SectionNodeInstance answerPopulatedSection = this.packageQuestionnaireService
                .questionGraphByBenefitCategoryAndSection(packageId, applicantId, sectionId, allAnswerList, displayTextLanguage, currentDate)

        List<EasyVisaNodeInstance> excludedPercentageCalculationQuestions = this.packageQuestionnaireService.
                getExcludedPercentageCalculationQuestions(answerPopulatedSection);
        List<String> excludedPercentageCalculationQuestionIdList = excludedPercentageCalculationQuestions.collect {it.id};
        List<Answer> sectionAnswerList = allAnswerList.stream()
                .filter({ answerObj -> (answerObj.getSectionId() == sectionId) &&
                        !excludedPercentageCalculationQuestionIdList.contains(answerObj.id)})
                .collect(Collectors.toList())

        AnswerEvaluationContext answerEvaluationContext = new AnswerEvaluationContext(packageId: packageId, applicantId: applicantId,
                answerList: allAnswerList, excludedPercentageCalculationQuestions: excludedPercentageCalculationQuestionIdList)
        List<Answer> validSectionAnswerList = answerPopulatedSection.collectAllValidAnswers(answerEvaluationContext, this.answerVisibilityEvaluatorService)

        this.saveSectionCompletionStatus(packageId, applicantId, answerPopulatedSection, validSectionAnswerList, allAnswerList)
        this.updatedDependentSectionCompletion(packageId, applicantId, answerPopulatedSection, allAnswerList)
        this.removeUnusedQuestionAnswers(sectionAnswerList, validSectionAnswerList)

        return answerPopulatedSection
    }


    @Transactional
    void saveSectionCompletionStatus(Long packageId, Long applicantId, SectionNodeInstance sectionNodeInstance,
                                             List<Answer> sectionAnswerList, List<Answer> allAnswerList) {
        Double completedPercentage = this.findSectionCompletionPercentage(packageId, applicantId, sectionNodeInstance, allAnswerList)
        SectionCompletionState completionState = this.findSectionCompletionState(packageId, applicantId, sectionNodeInstance, sectionAnswerList, completedPercentage)
        String sectionId = sectionNodeInstance.getId()
        SectionCompletionStatus sectionCompletionStatus = SectionCompletionStatus.findByPackageIdAndApplicantIdAndSectionId(packageId, applicantId, sectionId)
        if (sectionCompletionStatus == null) {
            sectionCompletionStatus = new SectionCompletionStatus(packageId: packageId, applicantId: applicantId, sectionId: sectionId)
        }
        sectionCompletionStatus.setCompletedPercentage(completedPercentage)
        sectionCompletionStatus.setCompletionState(completionState)
        sectionCompletionStatus.save(failOnError: true)
    }


    private Double findSectionCompletionPercentage(Long packageId, Long applicantId,
                                                   SectionNodeInstance sectionNodeInstance, List<Answer> allAnswerList) {
        def questionNodeFilter = { EasyVisaNodeInstance easyVisaNodeInstance ->
            if (easyVisaNodeInstance instanceof QuestionNodeInstance) {
                QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance
                return (questionNodeInstance.inputType != InputTypeConstant.LABEL.value && !questionNodeInstance.hasExcludeFromPercentageCalculation())
            }
            return false
        }
        List<EasyVisaNodeInstance> flattenedVisibleNodeList = sectionNodeInstance.flattenCollect({ EasyVisaNodeInstance easyVisaNodeInstance -> easyVisaNodeInstance.isVisibility() })
        List<EasyVisaNodeInstance> questionNodeList = flattenedVisibleNodeList.findAll { questionNodeFilter(it) }
        int validAnswersCount = questionNodeList.count {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) it
            return evaluateAnswerCompletionValidation(packageId, applicantId, questionNodeInstance, allAnswerList)
        } as int
        BigDecimal currentCompletedPercentage = (questionNodeList.size() != 0) ? ((validAnswersCount / questionNodeList.size()) * 100) : 100
        CompletionPercentageRuleEvaluationContext ruleEvaluationContext = new CompletionPercentageRuleEvaluationContext(allAnswerList, sectionNodeInstance,
                packageId, applicantId, Math.round(currentCompletedPercentage.toDouble()), validAnswersCount, questionNodeList)
        double completedPercentage = ruleActionHandler.calculateCompletionPercentage(sectionNodeInstance.getCompletionPercentageRule(), ruleEvaluationContext)
        return completedPercentage
    }


    private Boolean evaluateAnswerCompletionValidation(Long packageId, Long applicantId, QuestionNodeInstance questionNodeInstance, List<Answer> answerList) {
        Answer answer = questionNodeInstance.getAnswer()
        boolean hasQuestionAnswered = Answer.isValidAnswer(answer)
        if (StringUtils.isNotEmpty(questionNodeInstance.answerCompletionValidationRule)) {
            NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(answerList, questionNodeInstance, packageId, applicantId)
            hasQuestionAnswered = ruleActionHandler.validateAnswerCompletion(questionNodeInstance.answerCompletionValidationRule, ruleEvaluationContext)
        }
        return hasQuestionAnswered
    }


    private SectionCompletionState findSectionCompletionState(Long packageId, Long applicantId,
                                                              SectionNodeInstance sectionNodeInstance,
                                                              List<Answer> sectionAnswerList, Double sectionCompletedPercentage) {
        boolean hasAllSectionQuestionsAnswered = sectionAnswerList.stream().allMatch { answer -> Answer.isValidAnswer(answer) }
        if (Math.round(sectionCompletedPercentage)!=100 || hasAllSectionQuestionsAnswered == false) {
            return SectionCompletionState.PENDING
        }
        NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(sectionAnswerList, sectionNodeInstance, packageId, applicantId)
        boolean hasSectionCompleted = ruleActionHandler.validateCompletionStatus(sectionNodeInstance.getSectionCompletionRule(), ruleEvaluationContext)
        SectionCompletionState completionState = (hasSectionCompleted == true) ? SectionCompletionState.COMPLETED : SectionCompletionState.PENDING
        return completionState
    }


    @Transactional
    void updatedDependentSectionCompletion(Long packageId, Long applicantId, SectionNodeInstance sectionNodeInstance, List<Answer> allAnswerList) {
        NodeRuleEvaluationContext ruleEvaluationContext = new NodeRuleEvaluationContext(allAnswerList, sectionNodeInstance, packageId, applicantId)
        ruleActionHandler.updatedDependentSectionCompletion(sectionNodeInstance.getSectionCompletionRule(), ruleEvaluationContext)
    }

    @Transactional
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    void removeUnusedQuestionAnswers(List<Answer> allSectionAnswerList, List<Answer> validSectionAnswerList) {
        Map<String, Answer> validAnswerByPath = validSectionAnswerList.stream().collect(Collectors.toMap({ Answer answer -> answer.path }, Function.identity()))
        for (Answer answer : allSectionAnswerList) {
            if (!validAnswerByPath.containsKey(answer.path)) {
                Warning.findAllByAnswer(answer).each {
                    Warning warning = it as Warning
                    warning.delete()
                }
                answer.delete()
            }
        }
    }


    @Transactional
    void updateSectionCompletionStatusAndRemoveZombieAnswers(Package aPackage, def oldVerPackageSections) {
        def packageSections = this.packageQuestionnaireService.fetchPackageSections(aPackage.id)
        this.removeDataFromOldVersionPackage(aPackage, packageSections, oldVerPackageSections)
        packageSections.each { packageSectionData ->
            Long applicantId = packageSectionData['applicantId'] as Long
            List<Answer> allAnswerList = this.answerService.fetchAnswers(aPackage.id, applicantId)
            List applicantSections = packageSectionData['sections'] as List
            applicantSections.each { sectionData ->
                this.updateApplicantSectionCompletionStatusAndRemoveZombieAnswers(aPackage.id, applicantId, sectionData['id'] as String, allAnswerList,
                        this.defaultDisplayTextLanguage, this.defaultCurrentDate)
            }
        }
    }

    private void removeDataFromOldVersionPackage(Package aPackage, def currentVerPackageSections, def oldVerPackageSections) {
        oldVerPackageSections.each { oldVerPackageSectionData ->
            Long applicantId = oldVerPackageSectionData['applicantId'] as Long
            def currentVerPackageSectionData = currentVerPackageSections.find {
                it['applicantId'] == applicantId
            } ?: [sections: []]
            List currentVerPackageApplicantSections = currentVerPackageSectionData['sections'] as List
            List oldVerPackageApplicantSections = oldVerPackageSectionData['sections'] as List
            oldVerPackageApplicantSections.each { oldVerSectionData ->
                String sectionId = oldVerSectionData['id'] as String
                def currentVerSectionData = currentVerPackageApplicantSections.find { it['id'] == sectionId }
                if (!currentVerSectionData) {
                    this.answerService.removeApplicantSectionAnswers(aPackage.id, applicantId, sectionId)
                    SectionCompletionStatus sectionCompletionStatus = SectionCompletionStatus.findByPackageIdAndApplicantIdAndSectionId(aPackage.id, applicantId, sectionId)
                    sectionCompletionStatus?.delete(failOnError: true)
                }
            }
        }
    }
}
