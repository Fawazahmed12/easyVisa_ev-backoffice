package com.easyvisa.questionnaire.dto;

public enum InputTypeConstant {
    INPUT("input"), RADIO("radio"), LABEL("label"), TEXTAREA("textarea");

    private final String value;

    InputTypeConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

