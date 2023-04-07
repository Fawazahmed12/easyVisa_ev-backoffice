package com.easyvisa.questionnaire.answering.rule

class Outcome {

    private String relationshipType
    private boolean successfulMatch

    Outcome(String relationshipType, boolean successfulMatch) {
        this.relationshipType = relationshipType
        this.successfulMatch = successfulMatch
    }

    String getRelationshipType() {
        return relationshipType
    }

    void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType
    }

    boolean isSuccessfulMatch() {
        return successfulMatch
    }

    void setSuccessfulMatch(boolean successfulMatch) {
        this.successfulMatch = successfulMatch
    }
}
