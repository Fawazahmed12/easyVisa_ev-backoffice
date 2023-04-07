package com.easyvisa.questionnaire.answering

class ContinuationSheetHeaderInfo {

    private String alienNumber;
    private String firstName
    private String middleName
    private String lastName

    String getAlienNumber() {
        return alienNumber
    }

    void setAlienNumber(String alienNumber) {
        this.alienNumber = alienNumber
    }

    String getFirstName() {
        return firstName
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    String getMiddleName() {
        return middleName
    }

    void setMiddleName(String middleName) {
        this.middleName = middleName
    }

    String getLastName() {
        return lastName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }
}
