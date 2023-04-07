package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class AttorneyWarningRule extends BaseComputeRule {

    private static String RULE_NAME = 'AttorneyWarningRule'

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private static String QUESTION_PLACEHOLDER = 'question'
    private static String DAYTIME_PHONENUMBER_PLACEHOLDER = 'daytimePhone'
    private static String MOBILE_PHONENUMBER_PLACEHOLDER = 'mobilePhone'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    AsyncService asyncService
    AlertService alertService
    private String warningTemplate = '/email/internal/packageWarningYesAnswer'

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateAttorneyWarningRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }


    private Boolean evaluateAttorneyWarningRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer questionNodeInstanceAnswer = questionNodeInstance.getAnswer()
        if (!Answer.isValidAnswer(questionNodeInstanceAnswer)) {
            return false
        }
        String attorneyWarningExpectedAnswer = questionNodeInstance.getDefinitionNode().getRuleParam()
        String attorneyWarningFieldAnswerValue = EasyVisaNode.normalizeAnswer(questionNodeInstanceAnswer.getValue())
        return (attorneyWarningFieldAnswerValue == attorneyWarningExpectedAnswer)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        asyncService.runAsync({
            createPackageWarning(answerContext)
        }, "Send Package [${answerContext.packageId}] warning (AttorneyWarningRule) for Answer(s) ${answerContext.answerList*.id} and previous Answer [${previousAnswer?.id}] of Applicant [${answerContext.applicantId}]")
    }

    /**
     *
     * Template placeholders
     * [Insert Client Name]
     * [Insert daytime phone number]
     * [Insert mobile phone number]
     */
    private void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        if (!packageObj) {
            return
        }

        String attorneyWarningMessage = constructWarningMessage(packageObj.client, questionNodeInstance.displayText)
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                attorneyWarningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }


    private String constructWarningMessage(Applicant applicant, String question) {
        return alertService.renderTemplate(warningTemplate, [(CLIENTNAME_PLACEHOLDER):applicant.getName(),
                                                             (QUESTION_PLACEHOLDER):question,
                                                             (DAYTIME_PHONENUMBER_PLACEHOLDER):applicant.homeNumber,
                                                             (MOBILE_PHONENUMBER_PLACEHOLDER):applicant.mobileNumber])
    }

}
