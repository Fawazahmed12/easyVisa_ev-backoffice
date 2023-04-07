package com.easyvisa.questionnaire.answering.rule

/**
 * These rules are generally configured at  "link" level. These rules might need information across nodes instances. For example to execute a rule that
 * depends on the field of multiple instances, we can configure this rule at the parentNode. These rules are checked after visiting the child instances. See
 * VisibilityAssignmentVisitor
 */
interface IRelationTypeRule {
    boolean canProceed(NodeRuleEvaluationContext answerContext);

    void register();

    void updateVisibilityOnSuccessful(NodeRuleEvaluationContext answerContext);

    void updateVisibilityOnNonSuccessful(NodeRuleEvaluationContext answerContext);

    void triggerFormActionOnSuccessfulRelationshipRule(NodeRuleEvaluationContext answerContext);
}
