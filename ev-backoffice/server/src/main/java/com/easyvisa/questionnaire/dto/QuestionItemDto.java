package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionItemDto implements IFieldGroup {

    @JsonIgnore
    private IFieldGroup parentField;

    private String key;
    private String type;
    private String name;

    private String defaultValue;
    private List<String> wrappers = new ArrayList<>();

    @JsonProperty("className")
    private String styleClassName;

    @JsonProperty("templateOptions")
    private TemplateOption templateOption;

    @JsonProperty("modelOptions")
    private ModelOption modelOption;

    private String hideExpression;

    public QuestionItemDto(IFieldGroup parentField, String id, String key, String type, TemplateOption templateOption) {
        this.parentField = parentField;
        this.key = key;
        this.type = type;
        this.name = templateOption.getName();
        this.templateOption = templateOption;
        this.modelOption = new ModelOption(this.type);
        this.addAttribute(TemplateOptionAttributes.QUESTIONID.getValue(), id);
        this.addAttribute(TemplateOptionAttributes.FIELDPATH.getValue(), this.getFullyQualifiedKey());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStyleClassName() {
        return styleClassName;
    }

    public void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getWrappers() {
        return wrappers;
    }

    public void setWrappers(List<String> wrappers) {
        this.wrappers = wrappers;
    }

    public TemplateOption getTemplateOption() {
        return templateOption;
    }

    public void setTemplateOption(TemplateOption templateOption) {
        this.templateOption = templateOption;
    }

    @Override
    public void addFieldGroup(IFieldGroup iFieldGroup) {

    }

    @Override
    public void setAnswerIndex(Integer answerIndex) {
        this.addAttribute(TemplateOptionAttributes.ANSWERINDEX.getValue(), answerIndex);
    }


    @Override
    public String getFieldId() {
        Map attributes = this.templateOption.getAttributes();
        String questionId = (String) attributes.get(TemplateOptionAttributes.QUESTIONID.getValue());
        return questionId;
    }

    @Override
    public Integer getOrderIndex() {
        return 0;
    }


    @Override
    public void setFieldGroups(List<IFieldGroup> fieldGroups) {

    }

    @Override
    public List<IFieldGroup> getFieldGroups() {
        return null;
    }


    public String getHideExpression() {
        return hideExpression;
    }

    public void setHideExpression(String hideExpression) {
        this.hideExpression = hideExpression;
    }

    public ModelOption getModelOption() {
        return modelOption;
    }

    public void setModelOption(ModelOption modelOption) {
        this.modelOption = modelOption;
    }

    public void addSubSectionAttribute(String subsectionId){
        this.addAttribute(TemplateOptionAttributes.SUBSECTION.getValue(), subsectionId);
    }

    public void addDisabledAttribute(){
        Map attributes = this.templateOption.getAttributes();
        attributes.put(TemplateOptionAttributes.DISABLED.getValue(), true);
        this.templateOption.setDisabled(true);
    }


    private void addAttribute(String key, String value){
        Map attributes = this.templateOption.getAttributes();
        attributes.put(key, value);
    }

    private void addAttribute(String key, Integer value){
        Map attributes = this.templateOption.getAttributes();
        attributes.put(key, value);
    }

    public String getFullyQualifiedKey() {
        return this.parentField.getFullyQualifiedKey() +"."+ this.getKey();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
