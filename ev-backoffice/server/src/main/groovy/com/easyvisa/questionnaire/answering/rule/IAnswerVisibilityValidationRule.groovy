package com.easyvisa.questionnaire.answering.rule

interface IAnswerVisibilityValidationRule {
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext);
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext);
}