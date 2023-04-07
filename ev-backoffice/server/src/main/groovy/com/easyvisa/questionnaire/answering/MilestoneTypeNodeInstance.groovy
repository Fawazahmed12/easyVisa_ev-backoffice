package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.MilestoneType
import com.fasterxml.jackson.annotation.JsonIgnore

class MilestoneTypeNodeInstance extends EasyVisaNodeInstance {
    private String dateLabel
    private String reminderRule
    private String reminderRuleParam

    @JsonIgnore
    private MilestoneType milestoneType;

    MilestoneTypeNodeInstance(MilestoneType milestoneType, DisplayTextLanguage displayTextLanguage) {
        super(milestoneType, displayTextLanguage)
        this.milestoneType = milestoneType
        this.dateLabel = milestoneType.getDateLabel()
        this.reminderRule = milestoneType.getReminderRule()
        this.reminderRuleParam = milestoneType.getReminderRuleParam()
    }

    String getDateLabel() {
        return dateLabel
    }

    void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel
    }

    String getReminderRule() {
        return reminderRule
    }

    void setReminderRule(String reminderRule) {
        this.reminderRule = reminderRule
    }

    String getReminderRuleParam() {
        return reminderRuleParam
    }

    void setReminderRuleParam(String reminderRuleParam) {
        this.reminderRuleParam = reminderRuleParam
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {

    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.milestoneType
    }
}
