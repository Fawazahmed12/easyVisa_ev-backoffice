package com.easyvisa.questionnaire.answering.rule

interface ISectionVisibilityRule {

    void register();

    void updateVisibilityOnSuccessfulMatch(SectionVisibilityRuleEvaluationContext ruleEvaluationContext);
}
