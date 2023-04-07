package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public class AnswerItemDto {
    private Map<String, Object> childItems; // The value could be primitive, anotherAnswerItemDto or a List

    public AnswerItemDto() {
        childItems = new HashMap<>();
    }

    public void addChildItem(String prop, Object value) {
        this.childItems.put(prop, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getChildItems() {
        return childItems;
    }

    public void setChildItems(Map<String, Object> childItems) {
        this.childItems = childItems;
    }
}
