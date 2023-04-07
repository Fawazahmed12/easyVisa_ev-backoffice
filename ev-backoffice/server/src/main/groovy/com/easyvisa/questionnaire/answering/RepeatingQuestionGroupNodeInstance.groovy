package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RepeatingQuestionGroup
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class RepeatingQuestionGroupNodeInstance extends EasyVisaNodeInstance {

    @JsonIgnore
    RepeatingQuestionGroup repeatingQuestionGroup

    private Integer answerIndex;
    private Integer totalRepeatCount;
    private String addButtonTitle
    private String attributeRule
    private String attributeRuleParam
    private String displayTextRule
    private String displayTextRuleParam
    private String wrapperName;
    private String styleClassName;
    private String resetRuleName;
    private String resetRuleParam;
    private String lifeCycleRule;
    private String lifeCycleRuleParam;

    private Map attributes = new HashMap();

    private static String DELETE_WARNING_TEXT = 'Are you sure that you want to delete this <strong>#repeatingGroupLabel</strong>?'
    private static String DELETE_WARNING_PLACEHOLDER = '#repeatingGroupLabel'

    RepeatingQuestionGroupNodeInstance(RepeatingQuestionGroup repeatingQuestionGroup,
                                       Integer answerIndex, Integer totalRepeatCount,
                                       DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        super(repeatingQuestionGroup, displayTextLanguage, currentDate)
        this.repeatingQuestionGroup = repeatingQuestionGroup
        this.answerIndex = answerIndex;
        this.totalRepeatCount = totalRepeatCount;
        this.copyRepeatingQuestionGroupProperties(repeatingQuestionGroup);
        this.addRepeatingGroupWarningText();
    }

    private copyRepeatingQuestionGroupProperties(RepeatingQuestionGroup repeatingQuestionGroup) {
        this.addButtonTitle = repeatingQuestionGroup.getAddButtonTitle()
        this.attributeRule = repeatingQuestionGroup.getAttributeRule()
        this.attributeRuleParam = repeatingQuestionGroup.getAttributeRuleParam()
        this.displayTextRule = repeatingQuestionGroup.getDisplayTextRule()
        this.displayTextRuleParam = repeatingQuestionGroup.getDisplayTextRuleParam()
        this.wrapperName = repeatingQuestionGroup.getWrapperName()
        this.styleClassName = repeatingQuestionGroup.getStyleClassName()
        this.resetRuleName = repeatingQuestionGroup.getResetRuleName()
        this.resetRuleParam = repeatingQuestionGroup.getResetRuleParam()
        this.lifeCycleRule = repeatingQuestionGroup.getLifeCycleRule()
        this.lifeCycleRuleParam = repeatingQuestionGroup.getLifeCycleRuleParam()
        this.setOrder(repeatingQuestionGroup.getOrder())
    }


    private void addRepeatingGroupWarningText() {
        String updatedRepeatingGroupWarningText = DELETE_WARNING_TEXT.replaceAll(DELETE_WARNING_PLACEHOLDER, "${this.displayText.toLowerCase()}")
        this.attributes[TemplateOptionAttributes.REPEATINGGROUP_DELETE_TEXT.getValue()] = updatedRepeatingGroupWarningText;
    }


    Integer getAnswerIndex() {
        return answerIndex
    }

    void setAnswerIndex(Integer answerIndex) {
        this.answerIndex = answerIndex
    }

    Integer getTotalRepeatCount() {
        return totalRepeatCount
    }

    void setTotalRepeatCount(Integer totalRepeatCount) {
        this.totalRepeatCount = totalRepeatCount
    }

    String getAddButtonTitle() {
        return addButtonTitle
    }

    void setAddButtonTitle(String addButtonTitle) {
        this.addButtonTitle = addButtonTitle
    }

    String getAttributeRule() {
        return attributeRule
    }

    void setAttributeRule(String attributeRule) {
        this.attributeRule = attributeRule
    }

    String getAttributeRuleParam() {
        return attributeRuleParam
    }

    void setAttributeRuleParam(String attributeRuleParam) {
        this.attributeRuleParam = attributeRuleParam
    }

    String getDisplayTextRule() {
        return displayTextRule
    }

    void setDisplayTextRule(String displayTextRule) {
        this.displayTextRule = displayTextRule
    }

    String getDisplayTextRuleParam() {
        return displayTextRuleParam
    }

    void setDisplayTextRuleParam(String displayTextRuleParam) {
        this.displayTextRuleParam = displayTextRuleParam
    }

    String getWrapperName() {
        return wrapperName
    }

    void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName
    }

    String getStyleClassName() {
        return styleClassName
    }

    void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName
    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.repeatingQuestionGroup
    }

    @Override
    void accept(INodeInstanceVisitor nodeInstanceVisitor) {
        nodeInstanceVisitor.visit(this)
    }

    String getAnswerKey() {
        return this.name + "-" + this.answerIndex;
    }

    Map getAttributes() {
        return attributes
    }

    void setAttributes(Map attributes) {
        this.attributes = attributes
    }

    String getResetRuleName() {
        return resetRuleName
    }

    void setResetRuleName(String resetRuleName) {
        this.resetRuleName = resetRuleName
    }

    String getResetRuleParam() {
        return resetRuleParam
    }

    void setResetRuleParam(String resetRuleParam) {
        this.resetRuleParam = resetRuleParam
    }

    String getLifeCycleRule() {
        return lifeCycleRule
    }

    void setLifeCycleRule(String lifeCycleRule) {
        this.lifeCycleRule = lifeCycleRule
    }

    String getLifeCycleRuleParam() {
        return lifeCycleRuleParam
    }

    void setLifeCycleRuleParam(String lifeCycleRuleParam) {
        this.lifeCycleRuleParam = lifeCycleRuleParam
    }

    List<String> getWrappers() {
        List<String> wrappers = [];
        if(wrapperName!=null){
            wrappers = wrapperName.split(",");
        }
        return wrappers;
    }
}
