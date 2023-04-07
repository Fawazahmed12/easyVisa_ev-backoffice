package com.easyvisa.questionnaire.model;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.List;

@NodeEntity
public class Question extends EasyVisaNode {

    @Property
    private String dataType;

    @Property
    private String inputType; // can be txtfield, calendar, radio etc

    @Property
    private String styleClassName;

    @Property
    private String wrapperName;

    @Property
    private String actionable;

    @Property
    private String excludeFromPercentageCalculation;

    @Property
    private List<String> values;

    @Property
    private String inputSourceType;

    @Property
    private String inputTypeSourceRule;

    @Property
    private String inputTypeSourceRuleParam;

    @Property
    private String displayTextRule;

    @Property
    private String displayTextRuleParam;

    @Property
    private String tooltipRule;

    @Property
    private String attributeRule;

    @Property
    private String attributeRuleParam;

    @Property
    private String answerCompletionValidationRule;

    @Property
    private String answerCompletionValidationRuleParam;

    @Property
    private String answerVisibilityValidationRule;

    @Property
    private String answerVisibilityValidationRuleParam;

    @Property
    private String answerValidationRule;

    @Property
    private String answerValidationRuleParam;

    @Property
    private String pdfFieldRelationshipRule;

    @Property
    private String pdfFieldRelationshipRuleParam;

    @Property
    private String defaultValue;

    @Property
    private Boolean required;

    @Property
    private String refresh;

    @Property
    private String readonly;

    @Property
    private String tooltip;

    @Property
    private String errorMessage;

    @Property
    private String contextualClue;

    public Question() {
    }

    public String getActionable() {
        return actionable;
    }

    public void setActionable(String actionable) {
        this.actionable = actionable;
    }

    public String getExcludeFromPercentageCalculation() {
        return this.excludeFromPercentageCalculation;
    }

    public void setExcludeFromPercentageCalculation(String excludeFromPercentageCalculation) {
        this.excludeFromPercentageCalculation = excludeFromPercentageCalculation;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }


    public String getInputSourceType() {
        return inputSourceType;
    }

    public void setInputSourceType(String inputSourceType) {
        this.inputSourceType = inputSourceType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getReadonly() {
        return readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }


    public String getInputTypeSourceRule() {
        return inputTypeSourceRule;
    }

    public void setInputTypeSourceRule(String inputTypeSourceRule) {
        this.inputTypeSourceRule = inputTypeSourceRule;
    }

    public String getInputTypeSourceRuleParam() {
        return inputTypeSourceRuleParam;
    }

    public void setInputTypeSourceRuleParam(String inputTypeSourceRuleParam) {
        this.inputTypeSourceRuleParam = inputTypeSourceRuleParam;
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

    public String getTooltipRule() {
        return tooltipRule;
    }

    public void setTooltipRule(String tooltipRule) {
        this.tooltipRule = tooltipRule;
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

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getStyleClassName() {
        return styleClassName;
    }

    public void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName;
    }

    public String getAnswerCompletionValidationRule() {
        return answerCompletionValidationRule;
    }

    public void setAnswerCompletionValidationRule(String answerCompletionValidationRule) {
        this.answerCompletionValidationRule = answerCompletionValidationRule;
    }

    public String getAnswerCompletionValidationRuleParam() {
        return answerCompletionValidationRuleParam;
    }

    public void setAnswerCompletionValidationRuleParam(String answerCompletionValidationRuleParam) {
        this.answerCompletionValidationRuleParam = answerCompletionValidationRuleParam;
    }

    public String getAnswerVisibilityValidationRule() {
        return answerVisibilityValidationRule;
    }

    public void setAnswerVisibilityValidationRule(String answerVisibilityValidationRule) {
        this.answerVisibilityValidationRule = answerVisibilityValidationRule;
    }

    public String getAnswerVisibilityValidationRuleParam() {
        return answerVisibilityValidationRuleParam;
    }

    public void setAnswerVisibilityValidationRuleParam(String answerVisibilityValidationRuleParam) {
        this.answerVisibilityValidationRuleParam = answerVisibilityValidationRuleParam;
    }

    public String getAnswerValidationRule() {
        return answerValidationRule;
    }

    public void setAnswerValidationRule(String answerValidationRule) {
        this.answerValidationRule = answerValidationRule;
    }

    public String getAnswerValidationRuleParam() {
        return answerValidationRuleParam;
    }

    public void setAnswerValidationRuleParam(String answerValidationRuleParam) {
        this.answerValidationRuleParam = answerValidationRuleParam;
    }

    public String getPdfFieldRelationshipRule() {
        return pdfFieldRelationshipRule;
    }

    public void setPdfFieldRelationshipRule(String pdfFieldRelationshipRule) {
        this.pdfFieldRelationshipRule = pdfFieldRelationshipRule;
    }

    public String getPdfFieldRelationshipRuleParam() {
        return pdfFieldRelationshipRuleParam;
    }

    public void setPdfFieldRelationshipRuleParam(String pdfFieldRelationshipRuleParam) {
        this.pdfFieldRelationshipRuleParam = pdfFieldRelationshipRuleParam;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getContextualClue() {
        return contextualClue;
    }

    public void setContextualClue(String contextualClue) {
        this.contextualClue = contextualClue;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        Question question = new Question();
        this.copyBaseProps(question);
        return question;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        Question targetQuestion = (Question) target;
        targetQuestion.dataType = this.dataType;
        targetQuestion.inputType = this.inputType;
        targetQuestion.styleClassName = this.styleClassName;
        targetQuestion.wrapperName = this.wrapperName;
        targetQuestion.actionable = this.actionable;
        targetQuestion.excludeFromPercentageCalculation = this.excludeFromPercentageCalculation;
        targetQuestion.values = this.values;
        targetQuestion.inputSourceType = this.inputSourceType;
        targetQuestion.defaultValue = this.defaultValue;
        targetQuestion.required = this.required;
        targetQuestion.refresh = this.refresh;
        targetQuestion.readonly = this.readonly;
        targetQuestion.inputTypeSourceRule = this.inputTypeSourceRule;
        targetQuestion.inputTypeSourceRuleParam = this.inputTypeSourceRuleParam;
        targetQuestion.displayTextRule = this.displayTextRule;
        targetQuestion.displayTextRuleParam = this.displayTextRuleParam;
        targetQuestion.tooltipRule = this.tooltipRule;
        targetQuestion.attributeRule = this.attributeRule;
        targetQuestion.attributeRuleParam = this.attributeRuleParam;
        targetQuestion.answerCompletionValidationRule = this.answerCompletionValidationRule;
        targetQuestion.answerCompletionValidationRuleParam = this.answerCompletionValidationRuleParam;
        targetQuestion.answerVisibilityValidationRule = this.answerVisibilityValidationRule;
        targetQuestion.answerVisibilityValidationRuleParam = this.answerVisibilityValidationRuleParam;
        targetQuestion.answerValidationRule = this.answerValidationRule;
        targetQuestion.answerValidationRuleParam = this.answerValidationRuleParam;
        targetQuestion.pdfFieldRelationshipRule = this.pdfFieldRelationshipRule;
        targetQuestion.pdfFieldRelationshipRuleParam = this.pdfFieldRelationshipRuleParam;
        targetQuestion.tooltip = this.tooltip;
        targetQuestion.errorMessage = this.errorMessage;
        targetQuestion.contextualClue = this.contextualClue;
    }
}
