package com.easyvisa.questionnaire.model;

public enum RelationshipTypeConstants {
    YES("yes"), NO("no"), UNITED_STATES("united_states"), OTHER("other"),
    EMPLOYED("employed"), RETIRED("retired"), UNEMPLOYED("unemployed"),
    MARRIED("married"), LEGALLY_SEPERATED("legallyseparated"), DIVORCED("divorced"), WIDOWED("widowed"), MARRIAGE_ANULLED("marriage_annulled"),
    IMPERIAL("imperial"), METRIC("metric"),
    US_CITIZEN("united_states_citizen"), LPR("lawful_permanent_resident"),US_NATIONAL("us_national"),ALIEN("alien");

    private final String value;

    RelationshipTypeConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
