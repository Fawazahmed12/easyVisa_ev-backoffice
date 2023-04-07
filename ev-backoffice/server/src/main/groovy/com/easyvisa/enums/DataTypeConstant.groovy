package com.easyvisa.enums

enum DataTypeConstant {

    DATE('date'),
    STRING('string'),
    NUMBER('number'),
    BOOLEAN('boolean')

    private final String value;

    DataTypeConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
