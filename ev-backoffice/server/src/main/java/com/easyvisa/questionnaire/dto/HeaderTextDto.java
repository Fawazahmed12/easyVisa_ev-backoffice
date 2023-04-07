package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HeaderTextDto implements IFieldGroup {

    @JsonIgnore
    private IFieldGroup parentField;

    @JsonProperty("className")
    private String styleClassName;
    private String template;
    private String id;

    public HeaderTextDto(IFieldGroup parentField, String id){
        this.parentField = parentField;
        this.id = id;
    }

    public String getStyleClassName() {
        return styleClassName;
    }

    public void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public void addFieldGroup(IFieldGroup iFieldGroup) {

    }

    @Override
    public void setAnswerIndex(Integer answerIndex) {

    }

    @Override
    public String getFieldId() {
        return this.id;
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


    public String getFullyQualifiedKey() {
        return this.parentField.getFullyQualifiedKey() +"."+ this.id;
    }
}
