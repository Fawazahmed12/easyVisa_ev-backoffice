package com.easyvisa.questionnaire.answering.rule

interface IPdfFieldRelationshipRule {
    void register();
    String evaluateRelationshipType(NodeRuleEvaluationContext ruleEvaluationContext);
}
