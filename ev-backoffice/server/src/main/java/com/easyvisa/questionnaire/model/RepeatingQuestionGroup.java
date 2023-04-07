package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class RepeatingQuestionGroup extends EasyVisaNode {

    @Property
    private String addButtonTitle;

    @Property
    private String attributeRule;

    @Property
    private String attributeRuleParam;

    @Property
    private String displayTextRule;

    @Property
    private String displayTextRuleParam;

    @Property
    private String wrapperName;

    @Property
    private String styleClassName;

    @Property
    private String resetRuleName;

    @Property
    private String resetRuleParam;

    @Property
    private String lifeCycleRule;

    @Property
    private String lifeCycleRuleParam;


    public RepeatingQuestionGroup() {
    }


    public String getAddButtonTitle() {
        return addButtonTitle;
    }

    public void setAddButtonTitle(String addButtonTitle) {
        this.addButtonTitle = addButtonTitle;
    }

    public String getAttributeRule() {
        return attributeRule;
    }

    public void setAttributeRule(String attributeRule) {
        this.attributeRule = attributeRule;
    }

    public String getAttributeRuleParam() {
        return attributeRuleParam;
    }

    public void setAttributeRuleParam(String attributeRuleParam) {
        this.attributeRuleParam = attributeRuleParam;
    }

    public String getDisplayTextRule() {
        return displayTextRule;
    }

    public void setDisplayTextRule(String displayTextRule) {
        this.displayTextRule = displayTextRule;
    }

    public String getDisplayTextRuleParam() {
        return displayTextRuleParam;
    }

    public void setDisplayTextRuleParam(String displayTextRuleParam) {
        this.displayTextRuleParam = displayTextRuleParam;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName;
    }

    public String getStyleClassName() {
        return styleClassName;
    }

    public void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public String getResetRuleName() {
        return resetRuleName;
    }

    public void setResetRuleName(String resetRuleName) {
        this.resetRuleName = resetRuleName;
    }

    public String getResetRuleParam() {
        return resetRuleParam;
    }

    public void setResetRuleParam(String resetRuleParam) {
        this.resetRuleParam = resetRuleParam;
    }

    public String getLifeCycleRule() {
        return lifeCycleRule;
    }

    public void setLifeCycleRule(String lifeCycleRule) {
        this.lifeCycleRule = lifeCycleRule;
    }

    public String getLifeCycleRuleParam() {
        return lifeCycleRuleParam;
    }

    public void setLifeCycleRuleParam(String lifeCycleRuleParam) {
        this.lifeCycleRuleParam = lifeCycleRuleParam;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }

    @Override
    public EasyVisaNode copy() {
        RepeatingQuestionGroup repeatingQuestionGroup = new RepeatingQuestionGroup();
        this.copyBaseProps(repeatingQuestionGroup);
        return repeatingQuestionGroup;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        RepeatingQuestionGroup targetQuestion = (RepeatingQuestionGroup) target;
        targetQuestion.addButtonTitle = this.addButtonTitle;
        targetQuestion.attributeRule = this.attributeRule;
        targetQuestion.attributeRuleParam = this.attributeRuleParam;
        targetQuestion.displayTextRule = this.displayTextRule;
        targetQuestion.displayTextRuleParam = this.displayTextRuleParam;
        targetQuestion.wrapperName = this.wrapperName;
        targetQuestion.styleClassName = this.styleClassName;
        targetQuestion.resetRuleName = this.resetRuleName;
        targetQuestion.resetRuleParam = this.resetRuleParam;
        targetQuestion.lifeCycleRule = this.lifeCycleRule;
        targetQuestion.lifeCycleRuleParam = this.lifeCycleRuleParam;
    }
}
