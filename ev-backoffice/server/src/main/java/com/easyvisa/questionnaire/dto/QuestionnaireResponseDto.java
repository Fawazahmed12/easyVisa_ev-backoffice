package com.easyvisa.questionnaire.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;

public class QuestionnaireResponseDto {

    @JsonRawValue
    private String activePackage;

    private List packageSections;

    private FieldItemDto sectionQuestions;

    private AnswerItemDto sectionAnswer;

    private String sourceFieldId;

    public QuestionnaireResponseDto() {
    }

    public QuestionnaireResponseDto(String activePackage, List packageSections,
                                    FieldItemDto sectionQuestions, AnswerItemDto sectionAnswer) {
        this.activePackage = activePackage;
        this.packageSections = packageSections;
        this.sectionQuestions = sectionQuestions;
        this.sectionAnswer = sectionAnswer;
    }

    public String getActivePackage() {
        return activePackage;
    }

    public void setActivePackage(String activePackage) {
        this.activePackage = activePackage;
    }

    public FieldItemDto getSectionQuestions() {
        return sectionQuestions;
    }

    public void setSectionQuestions(FieldItemDto sectionQuestions) {
        this.sectionQuestions = sectionQuestions;
    }

    public AnswerItemDto getSectionAnswer() {
        return sectionAnswer;
    }

    public void setSectionAnswer(AnswerItemDto sectionAnswer) {
        this.sectionAnswer = sectionAnswer;
    }

    public List getPackageSections() {
        return packageSections;
    }

    public void setPackageSections(List packageSections) {
        this.packageSections = packageSections;
    }

    public String getSourceFieldId() {
        return sourceFieldId;
    }

    public void setSourceFieldId(String sourceFieldId) {
        this.sourceFieldId = sourceFieldId;
    }
}
