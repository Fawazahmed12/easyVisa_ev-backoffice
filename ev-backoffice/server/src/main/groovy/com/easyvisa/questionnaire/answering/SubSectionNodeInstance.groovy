package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.QuestionnaireDisplayNodeType
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.SubSection
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class SubSectionNodeInstance extends EasyVisaNodeInstance {
    private String wrapperName;
    private String styleClassName;

    @JsonIgnore
    private SubSection subSection

    SubSectionNodeInstance(SubSection subSection,
                           DisplayTextLanguage displayTextLanguage,
                           LocalDate currentDate,
                           QuestionnaireTranslationService questionnaireTranslationService) {
        super(subSection, displayTextLanguage, currentDate)
        this.subSection = subSection
        this.copySubSectionProperties(subSection);
        this.populateDisplayText(questionnaireTranslationService);
    }

    private copySubSectionProperties(SubSection subSection) {
        this.wrapperName = subSection.getWrapperName()
        this.styleClassName = subSection.getStyleClassName()
    }

    void populateDisplayText(QuestionnaireTranslationService questionnaireTranslationService) {
        String defaultDisplayText = this.subSection.getDisplayText()
        this.displayText = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.SUBSECTION,
                this.subSection.id, this.displayTextLanguage) ?: defaultDisplayText
    }

    String getWrapperName() {
        return wrapperName
    }

    void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName
    }

    String getStyleClassName() {
        return styleClassName
    }

    void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {
        nodeInstanceVisitor.visit(this)
    }

    EasyVisaNode getDefinitionNode() {
        return this.subSection
    }
}
