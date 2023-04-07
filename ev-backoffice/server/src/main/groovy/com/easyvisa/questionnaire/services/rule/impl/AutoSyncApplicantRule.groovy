package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.ApplicantService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class AutoSyncApplicantRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncApplicantRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    ApplicantService applicantService

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
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam()
        Answer answer = questionNodeInstance.getAnswer()
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        applicant[ruleParam] = answer.value ?: ''
        applicantService.saveApplicant(applicant)
    }
}
