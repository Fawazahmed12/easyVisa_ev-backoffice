package com.easyvisa.questionnaire.dto;

import com.easyvisa.questionnaire.meta.InputSourceType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateOption {
    private String label;
    private String type;
    private Boolean required;
    private String placeholder;
    private String toolTip;
    private Boolean disabled;
    private Integer rows; // This is available only for the inputType:'textarea'
    private Integer maxLength;
    private Map attributes = new HashMap();
    List<InputSourceType.ValueMap> options;
    private String name;

    public TemplateOption() {
        this.disabled = false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public List<InputSourceType.ValueMap> getOptions() {
        return options;
    }

    public void setOptions(List<InputSourceType.ValueMap> options) {
        this.options = options;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public Map getAttributes() {
        return attributes;
    }

    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
