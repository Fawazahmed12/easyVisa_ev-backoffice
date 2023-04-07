package com.easyvisa.questionnaire.dto;

public class CompletionWarningDto {
    private Boolean hasSectionWarning;
    private String headerText = "Warning";
    private String warningMessage;
    private String leftButtonText;
    private String rightButtonText;

    public CompletionWarningDto() {
        this.hasSectionWarning = false;
    }

    public CompletionWarningDto(String warningMessage, String leftButtonText, String rightButtonText) {
        this.hasSectionWarning = true;
        this.warningMessage = warningMessage;
        this.leftButtonText = leftButtonText;
        this.rightButtonText = rightButtonText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public Boolean getHasSectionWarning() {
        return hasSectionWarning;
    }

    public void setHasSectionWarning(Boolean hasSectionWarning) {
        this.hasSectionWarning = hasSectionWarning;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public String getLeftButtonText() {
        return leftButtonText;
    }

    public void setLeftButtonText(String leftButtonText) {
        this.leftButtonText = leftButtonText;
    }

    public String getRightButtonText() {
        return rightButtonText;
    }

    public void setRightButtonText(String rightButtonText) {
        this.rightButtonText = rightButtonText;
    }
}
