package com.easyvisa.questionnaire.answering.rule

interface IVisibilityComputeRule {

    void register();

    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext);
}
