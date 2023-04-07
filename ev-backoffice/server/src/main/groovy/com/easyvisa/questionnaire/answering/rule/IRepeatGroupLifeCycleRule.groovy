package com.easyvisa.questionnaire.answering.rule

interface IRepeatGroupLifeCycleRule {

    void register();

    /**
     * Gets called when user adds the new repeating-question-group instance*
     */
    void onEntry(NodeRuleEvaluationContext ruleEvaluationContext);


    /**
     * Gets called before user removes the repeating-question-group instance
     */
    void onPreExit(NodeRuleEvaluationContext ruleEvaluationContext);


    /**
     * Gets called after user removes the repeating-question-group instance
     */
    void onPostExit(NodeRuleEvaluationContext ruleEvaluationContext);
}
