package com.easyvisa.questionnaire.dto;

import com.easyvisa.questionnaire.meta.UIStyleMeta;
import com.easyvisa.questionnaire.model.EasyVisaNodeRelationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldItemDto implements IFieldGroup {

    @JsonIgnore
    private IFieldGroup parentField;

    private String key;
    private String id;
    private Boolean hide;

    @JsonProperty("fieldGroup")
    private List<IFieldGroup> fieldGroups = new ArrayList<>();

    public FieldItemDto() {
    }

    public FieldItemDto(IFieldGroup parentField, String name, String id) {
        this.parentField = parentField;
        this.key = name;
        this.id = id;
        this.hide = false;
    }

    public FieldItemDto(IFieldGroup parentField, String name, String id, String displayText, UIStyleMeta uiStyleMeta, String styleClassName) {
        this(parentField, name, id);
        HeaderTextDto headerTextDto = this.buildHeaderText(uiStyleMeta, displayText, styleClassName);
        this.fieldGroups.add(headerTextDto);
    }

    private HeaderTextDto buildHeaderText(UIStyleMeta uiStyleMeta, String displayText, String styleClassName) {
        HeaderTextDto headerTextDto = new HeaderTextDto(this.parentField, this.id);
        String headerTextStyleClassName = uiStyleMeta.getStyle().getStyleClassName();
        String subSectionClassName = StringUtils.isNotEmpty(styleClassName) ? headerTextStyleClassName+" "+styleClassName : headerTextStyleClassName;
        headerTextDto.setStyleClassName(subSectionClassName);

        Object[] params = new Object[]{uiStyleMeta.getStyle().getHeader(), displayText};
        String template = MessageFormat.format("<{0}>{1}</{0}>", params);
        headerTextDto.setTemplate(template);
        return headerTextDto;
    }

    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public void addFieldGroup(IFieldGroup iFieldGroup) {
        this.fieldGroups.add(iFieldGroup);
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
        this.fieldGroups = fieldGroups;
    }

    @Override
    public List<IFieldGroup> getFieldGroups() {
        return this.fieldGroups;
    }

    public String getFullyQualifiedKey() {
        if(this.parentField==null) {
            return this.getKey();
        }
        return this.parentField.getFullyQualifiedKey() +"."+ this.getKey();
    }
}
