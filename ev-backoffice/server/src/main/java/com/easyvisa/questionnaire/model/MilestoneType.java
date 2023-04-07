package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class MilestoneType extends EasyVisaNode {

    @Property
    private String dateLabel;

    @Property
    private String reminderRule;

    @Property
    private String reminderRuleParam;

    public MilestoneType() {
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public String getReminderRule() {
        return reminderRule;
    }

    public void setReminderRule(String reminderRule) {
        this.reminderRule = reminderRule;
    }

    public String getReminderRuleParam() {
        return reminderRuleParam;
    }

    public void setReminderRuleParam(String reminderRuleParam) {
        this.reminderRuleParam = reminderRuleParam;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        MilestoneType milestoneType = new MilestoneType();
        this.copyBaseProps(milestoneType);
        return milestoneType;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        MilestoneType targetSection = (MilestoneType) target;
        targetSection.dateLabel = this.dateLabel;
        targetSection.reminderRule = this.reminderRule;
        targetSection.reminderRuleParam = this.reminderRuleParam;
    }
}
