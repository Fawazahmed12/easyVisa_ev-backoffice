package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepeatingQuestionGroupDto implements IFieldGroup {

    @JsonIgnore
    private IFieldGroup parentField;

    private String key;
    private String type;
    private List<String> wrappers = new ArrayList<>();
    @JsonProperty("className")
    private String styleClassName;
    @JsonProperty("fieldArray")
    private RepeatingQuestionInfo repeatingQuestionInfo;
    private Boolean hide;
    private static String REPEATING_TYPE = "repeat";
    private Integer answerIndex;

    public RepeatingQuestionGroupDto(IFieldGroup parentField, String repeatingGroupId, Integer answerIndex,
                                     Integer totalRepeatCount, String answerKey) {
        this.parentField = parentField;
        this.key = answerKey;
        this.type = REPEATING_TYPE;
        this.repeatingQuestionInfo = new RepeatingQuestionInfo(repeatingGroupId, totalRepeatCount);
        this.setAnswerIndex(answerIndex);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyleClassName() {
        return styleClassName;
    }

    public void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName;
    }

    public RepeatingQuestionInfo getRepeatingQuestionInfo() {
        return repeatingQuestionInfo;
    }

    public void setRepeatingQuestionInfo(RepeatingQuestionInfo repeatingQuestionInfo) {
        this.repeatingQuestionInfo = repeatingQuestionInfo;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public List<String> getWrappers() {
        return wrappers;
    }

    public void setWrappers(List<String> wrappers) {
        this.wrappers = wrappers;
    }

    @Override
    public void addFieldGroup(IFieldGroup iFieldGroup) {
        List<IFieldGroup> fieldGroups = this.repeatingQuestionInfo.getFieldGroups();
        fieldGroups.add(iFieldGroup);
        iFieldGroup.setAnswerIndex(this.answerIndex);
    }

    @Override
    public void setAnswerIndex(Integer answerIndex) {
        this.answerIndex = answerIndex;
        this.repeatingQuestionInfo.addTemplateOptionAttribute(TemplateOptionAttributes.ANSWERINDEX.getValue(), answerIndex);
    }

    @Override
    public String getFieldId() {
        return this.repeatingQuestionInfo.getFieldId();
    }

    @Override
    public Integer getOrderIndex() {
        return this.answerIndex;
    }

    @Override
    public void setFieldGroups(List<IFieldGroup> fieldGroups) {
        this.repeatingQuestionInfo.setFieldGroups(fieldGroups);
    }

    @Override
    public List<IFieldGroup> getFieldGroups() {
        return this.repeatingQuestionInfo.getFieldGroups();
    }

    public void setFieldGroupClassName(String fieldGroupClassName) {
        this.repeatingQuestionInfo.setFieldGroupClassName(fieldGroupClassName);
    }


    public void buildTemplateOptionAttributes(Map attributes, String repeatingGroupLabel,
                                              String addButtonTitle, String subsectionId) {
        this.repeatingQuestionInfo.buildTemplateOptionAttributes(attributes, repeatingGroupLabel, addButtonTitle, subsectionId);
    }

    public String getFullyQualifiedKey() {
        return this.parentField.getFullyQualifiedKey() +"."+ this.getKey()+"[0]";
    }


    public class RepeatingQuestionInfo {
        private String fieldGroupClassName;
        @JsonProperty("fieldGroup")
        private List<IFieldGroup> fieldGroups;

        @JsonProperty("templateOptions")
        private TemplateOption templateOption;

        public RepeatingQuestionInfo(String repeatingGroupId, Integer totalRepeatCount) {
            this.templateOption = new TemplateOption();
            this.fieldGroups = new ArrayList<>();
            this.addTemplateOptionAttributes(repeatingGroupId, totalRepeatCount);
        }


        public void addTemplateOptionAttributes(String repeatingGroupId, Integer totalRepeatCount) {
            Map attributes = this.templateOption.getAttributes();
            attributes.put(TemplateOptionAttributes.REPEATINGGROUP.getValue(), repeatingGroupId);
            attributes.put(TemplateOptionAttributes.TOTALREPEATCOUNT.getValue(), totalRepeatCount);
        }


        public String getFieldId() {
            Map attributes = this.templateOption.getAttributes();
            String repeatingGroupId = (String) attributes.get(TemplateOptionAttributes.REPEATINGGROUP.getValue());
            return repeatingGroupId;
        }


        public String getFieldGroupClassName() {
            return fieldGroupClassName;
        }

        public void setFieldGroupClassName(String fieldGroupClassName) {
            this.fieldGroupClassName = fieldGroupClassName;
        }

        public List<IFieldGroup> getFieldGroups() {
            return fieldGroups;
        }

        public void setFieldGroups(List<IFieldGroup> fieldGroups) {
            this.fieldGroups = fieldGroups;
        }

        public TemplateOption getTemplateOption() {
            return templateOption;
        }

        public void setTemplateOption(TemplateOption templateOption) {
            this.templateOption = templateOption;
        }


        public void addTemplateOptionAttribute(String attributeName, Integer attributeValue) {
            Map attributes = this.templateOption.getAttributes();
            attributes.put(attributeName, attributeValue);
        }

        public void buildTemplateOptionAttributes(Map attributes, String repeatingGroupLabel,
                                                  String addButtonTitle, String subsectionId) {
            Map templateOptionAttributes = this.templateOption.getAttributes();
            templateOptionAttributes.put(TemplateOptionAttributes.LABEL.getValue(), repeatingGroupLabel);
            templateOptionAttributes.put(TemplateOptionAttributes.SUBSECTION.getValue(), subsectionId);
            templateOptionAttributes.put(TemplateOptionAttributes.ADDREPEATINGBUTTONTITLE.getValue(), addButtonTitle);
            // looping over keys
            for (Object attribute : attributes.keySet()) {
                String attributeName = (String) attribute;
                templateOptionAttributes.put(attributeName, attributes.get(attributeName));
            }
        }


        public void sortChildren(List<String> displayOrderIdList) {

        }
    }
}
