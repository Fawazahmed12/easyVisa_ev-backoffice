package com.easyvisa.questionnaire.answering.rule

interface IMilestoneReminderRule {
    void register();
    void executeMilestoneReminder(MilestoneReminderEvaluationContext ruleEvaluationContext);
}
