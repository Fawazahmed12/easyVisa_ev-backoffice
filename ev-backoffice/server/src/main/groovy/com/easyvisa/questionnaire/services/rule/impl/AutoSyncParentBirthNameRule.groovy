package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Family Information
 * SubSection: Parent 1 and Parent 2
 * Notes:  This rule will copy the current question answer to the ruleParam path question,
 *         only if the questions(Q_2882 and Q_2900)
 *         "Is this parent's current name the same as their birth name?" is 'yes'... ,
 *         (i.e) If the auto-populate is true then here we are syncing
 *         the current question-answer from (Current Name),  to
 *         its corresponding question-answer (Birth Name).
 */
@Component
class AutoSyncParentBirthNameRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncParentBirthNameRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AnswerService answerService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return new Outcome(answer.getValue(), true)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String questionnaireRuleParam = questionNodeInstance.getDefinitionNode().getRuleParam()
        String[] ruleParams = questionnaireRuleParam.split("\\|")
        ruleParams.each { ruleParam ->
            this.evaluateAutoSyncParentBirthNameAnswer(ruleParam, nodeRuleEvaluationContext)
        }
    }


    private void evaluateAutoSyncParentBirthNameAnswer(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateAutoPopulateParentCurrentNameRule(ruleParam, nodeRuleEvaluationContext)) {
            this.autoPopulateParentBirthNameFields(ruleParam, nodeRuleEvaluationContext)
        }
    }


    Boolean evaluateAutoPopulateParentCurrentNameRule(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String[] ruleParams = ruleParam.split(";")
        String CURRENT_NAME_POPULATE_PATH = ruleParams[0]
        Answer parentCurrentNamePopulateAnswer = nodeRuleEvaluationContext.findAnswerByPath(CURRENT_NAME_POPULATE_PATH)
        if (!Answer.isValidAnswer(parentCurrentNamePopulateAnswer)) {
            return false
        }
        String isCurrentNameSameAsBirthName = EasyVisaNode.normalizeAnswer(parentCurrentNamePopulateAnswer.getValue())
        if (isCurrentNameSameAsBirthName == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }


    private void autoPopulateParentBirthNameFields(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        List answerPathParts = [answer.sectionId, answer.subsectionId, answer.questionId]
        String[] ruleParams = ruleParam.split(";")
        Map<String, String> fieldsToTransfer = new HashMap<>()
        fieldsToTransfer[answerPathParts.join('/')] = ruleParams[1]
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer)
    }
}
