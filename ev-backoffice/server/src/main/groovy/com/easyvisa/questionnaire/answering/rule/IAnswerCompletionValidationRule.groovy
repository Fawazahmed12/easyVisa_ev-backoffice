package com.easyvisa.questionnaire.answering.rule

interface IAnswerCompletionValidationRule {
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext);
}