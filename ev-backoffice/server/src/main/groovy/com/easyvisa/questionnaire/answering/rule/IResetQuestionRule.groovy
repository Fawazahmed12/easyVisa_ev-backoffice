package com.easyvisa.questionnaire.answering.rule

interface IResetQuestionRule {

    void register();

    void resetTriggeringQuestions(NodeRuleEvaluationContext ruleEvaluationContext);
}
