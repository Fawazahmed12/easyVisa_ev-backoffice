package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.repositories.QuestionDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   Aapply this rule to 'Question' called 'What is your current marital status?'.
 *   If user selected 'Married' OR 'Legally Separated' option and the Immigration Benefit category is K-1/K-3,
 *   then a Warning message goes to the attorney's Warnings tab in the Task Queue.
 *
 *   This rule will check the  answers to the following question from 'Marital Status' subsection
 *    a.   What is your current marital status? -> 'Married' OR 'Legally Separated'
 * This rule will generate two labels 'yes' and 'no'. If the above condition are true then the result is 'yes'.
 */

/**
 * Section: Sec_familyInformation
 * SubSection: SubSec_maritalStatus
 * Question: 1. (Q_1204) What is your current marital status?
 * RuleParam: Sec_familyInformation/SubSec_maritalStatus/Q_1204,RQG_priorSpouses,SubSec_priorSpouses
 *
 * Section: Sec_familyInformationForBeneficiary
 * SubSection: SubSec_maritalStatusForBeneficiary
 * Question: 1. (Q_2781) What is your current marital status?
 * RuleParam: Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781,RQG_priorSpousesForBeneficiary,SubSec_priorSpouses
 */

@Component
class MaritalStatusAttorneyActionRule extends BaseComputeRule {

    private static String RULE_NAME = "MaritalStatusAttorneyActionRule"
    private static String CLIENTNAME1_PLACEHOLDER = 'clientName'
    private String marriedWarningTemplate = '/email/internal/packageWarningMarriageAll'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    @Autowired
    QuestionDAO questionDAO
    AsyncService asyncService
    AnswerService answerService
    AlertService alertService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String repeatingGroupId = ruleParams[1]
        Answer maritalStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        if (Answer.isValidAnswer(maritalStatusAnswer)) {
            return new Outcome(maritalStatusAnswer.getValue(), true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.evaluateMaritalStatusAttorneyActionRule(nodeRuleEvaluationContext)) {
            asyncService.runAsync({
                createPackageWarning(nodeRuleEvaluationContext)
            }, "Send Package [${nodeRuleEvaluationContext.packageId}] warning (MaritalStatusAttorneyActionRule) for Answer(s) ${nodeRuleEvaluationContext.answerList*.id} and previous Answer [${previousAnswer?.id}] of Applicant [${nodeRuleEvaluationContext.applicantId}]")
        }

        if (this.evaluatePriorSpousesDataInsertRule(nodeRuleEvaluationContext)) {
            this.addDependentDefaultRepeatingGroupIfRequired(nodeRuleEvaluationContext)
        }
    }


    private Boolean evaluateMaritalStatusAttorneyActionRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String repeatingGroupId = ruleParams[1]
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        if (maritalStatusAnswerList.isEmpty()) {
            return false
        }

        Answer maritalStatusAnswer = maritalStatusAnswerList[0]
        String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
        if (maritalStatusAnswerValue == RelationshipTypeConstants.MARRIED.value || maritalStatusAnswerValue == RelationshipTypeConstants.LEGALLY_SEPERATED.value) {
            return true
        }
        return false
    }


    protected Boolean evaluatePriorSpousesDataInsertRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String repeatingGroupId = ruleParams[1]
        List<Answer> maritalStatusAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), maritalStatusFieldPath)
        if (!maritalStatusAnswerList.isEmpty()) {
            Answer maritalStatusAnswer = maritalStatusAnswerList[0]
            String maritalStatusAnswerValue = EasyVisaNode.normalizeAnswer(maritalStatusAnswer.getValue())
            if (maritalStatusAnswerValue == RelationshipTypeConstants.DIVORCED.value ||
                    maritalStatusAnswerValue == RelationshipTypeConstants.WIDOWED.value ||
                    maritalStatusAnswerValue == RelationshipTypeConstants.MARRIAGE_ANULLED.value) {
                return true
            }
        }
        return false
    }


    private void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String warningMessage = constructWarningMessage(packageObj.client)
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                warningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }


    private addDependentDefaultRepeatingGroupIfRequired(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = ruleParam.split(",")
        String maritalStatusFieldPath = ruleParams[0]
        String repeatingGroupId = ruleParams[1]
        String subsectionId = ruleParams[2]
        String questVersion = nodeRuleEvaluationContext.getQuestionnaireVersion()

        List<Question> questionList = questionDAO.findQuestionsOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId)
        String questionId = questionList[0].id
        String sectionId = maritalStatusFieldPath.split("/")[0]

        // Check if it has a instance of repeating group question
        Answer existingAnswer = Answer.findByPackageIdAndApplicantIdAndSectionIdAndSubsectionIdAndQuestionIdAndIndex(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, sectionId, subsectionId, questionId, 0)
        if (existingAnswer) {
            return
        }
        answerService.addRepeatingGroupInstance(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, sectionId, subsectionId,
                repeatingGroupId, nodeRuleEvaluationContext.currentDate)
    }


    private String constructWarningMessage(Applicant applicant) {
        return alertService.renderTemplate(marriedWarningTemplate, [(CLIENTNAME1_PLACEHOLDER):applicant.getName()])
    }

}
