package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer

class BaseComputeRule implements INodeComputeRule {
    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        return new Outcome('has', true)
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext ruleEvaluationContext) {
        return true
    }

    @Override
    void register() {

    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer) {

    }

    String determineAnswer(NodeRuleEvaluationContext ruleEvaluationContext, Answer answer, Outcome outcome) {
        return answer?.value
    }
}
