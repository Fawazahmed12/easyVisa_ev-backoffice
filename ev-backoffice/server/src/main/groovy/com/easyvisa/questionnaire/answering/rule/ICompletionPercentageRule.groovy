package com.easyvisa.questionnaire.answering.rule

interface ICompletionPercentageRule {

    void register();

    Double calculateCompletionPercentage(CompletionPercentageRuleEvaluationContext ruleEvaluationContext);
}