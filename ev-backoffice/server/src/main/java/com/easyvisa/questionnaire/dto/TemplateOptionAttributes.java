package com.easyvisa.questionnaire.dto;

public enum TemplateOptionAttributes {

    LABEL("label"),
    QUESTIONID("questionId"),
    SUBSECTION("subsectionId"),
    REPEATINGGROUP("repeatingGroupId"),
    ANSWERINDEX("answerIndex"),
    TOTALREPEATCOUNT("totalRepeatCount"),
    HASQUESTIONANSWERED("hasQuestionAnswered"),
    DISABLED("disabled"),
    ERRORMESSAGE("errorMessage"),
    TOOLTIPCLASS("tooltipClass"),
    TOOLTIPCLOSEDELAY("tooltipCloseDelay"),
    NUMERICCHARACTERLENGTH("numericCharacterLength"),
    INPUTCHARACTERLENGTH("inputCharacterLength"),
    HASIMAGEURL("hasImageUrl"),
    ADDREPEATINGBUTTONTITLE("addButtonTitle"),
    REPEATINGGROUP_DELETE_TEXT("repeatingGroupDeleteText"),
    REMOVEREPEATINGBUTTON("showRemoveButton"),
    ADDREPEATINGBUTTON("showAddButton"),
    MAXIMUMNUMERICVALUE("maximumNumericValue"),
    HASVALIDATIONREQUIRED("hasValidationRequired"),
    MINIMUMDATE("minDate"),
    MAXIMUMDATE("maxDate"),
    FIELDPATH("fieldPath"),
    MAXLENGTH("maxLength");

    private final String value;

    TemplateOptionAttributes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

