package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer

interface INodeComputeRule {
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext);

    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext ruleEvaluationContext);

    void register();

    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext);

    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext, Answer previousAnswer);

    String determineAnswer(NodeRuleEvaluationContext ruleEvaluationContext, Answer answer, Outcome outcome);
}
