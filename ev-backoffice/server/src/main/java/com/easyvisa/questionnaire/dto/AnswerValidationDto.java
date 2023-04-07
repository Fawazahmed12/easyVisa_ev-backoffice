package com.easyvisa.questionnaire.dto;


public class AnswerValidationDto {

    private String value;
    private String subsectionId;
    private String questionId;
    private Integer index; // will hold repeating answers index

    private Boolean hasValidAnswer;
    private Boolean hasAnswerCompleted;
    private String errorMessage;
    private String resetValue;

    public AnswerValidationDto() {
        this.hasValidAnswer = true;
        this.hasAnswerCompleted = true;
    }


    public Boolean getHasValidAnswer() {
        return hasValidAnswer;
    }

    public void setHasValidAnswer(Boolean hasValidAnswer) {
        this.hasValidAnswer = hasValidAnswer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.hasValidAnswer = false;
        this.errorMessage = errorMessage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSubsectionId() {
        return subsectionId;
    }

    public void setSubsectionId(String subsectionId) {
        this.subsectionId = subsectionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getResetValue() {
        return resetValue;
    }

    public void setResetValue(String resetValue) {
        this.resetValue = resetValue;
    }

    public Boolean getHasAnswerCompleted() {
        return hasAnswerCompleted;
    }

    public void setHasAnswerCompleted(Boolean hasAnswerCompleted) {
        this.hasAnswerCompleted = hasAnswerCompleted;
    }
}
