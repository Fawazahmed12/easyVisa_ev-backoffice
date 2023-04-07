package com.easyvisa.questionnaire.model;

import java.util.Arrays;
import java.util.List;

public enum ApplicantType {

    Petitioner("Petitioner", "PETITIONER"),
    Beneficiary("Beneficiary", "BENEFICIARY"),
    //next type is used for the UI convenience only for detecting different description of Principle Beneficiary
    Principal_Beneficiary("Principle Beneficiary", "PRINCIPAL_BENEFICIARY"),
    Derivative_Beneficiary("Derivative Beneficiary", "DERIVATIVE_BENEFICIARY");

    private final String value;
    private final String uiValue;

    ApplicantType(String value, String uiValue) {
        this.value = value;
        this.uiValue = uiValue;
    }

    public String getValue() {
        return value;
    }

    public String getUiValue() {
        return uiValue;
    }

    public static ApplicantType getByUiValue(String uiValue) {
        for (ApplicantType value : values()) {
            if (value.uiValue.equals(uiValue)) {
                return value;
            }
        }
        return null;
    }

    public static List<ApplicantType> getBeneficiaryTypes() {
        return Arrays.asList(Beneficiary, Principal_Beneficiary);
    }

}
