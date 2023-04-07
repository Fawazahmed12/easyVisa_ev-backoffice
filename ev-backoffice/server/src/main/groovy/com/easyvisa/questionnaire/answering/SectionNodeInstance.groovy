package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.QuestionnaireDisplayNodeType
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Section
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class SectionNodeInstance extends EasyVisaNodeInstance {
    private String applicantType
    private String shortName
    private String sectionCompletionRule
    private String sectionCompletionRuleParam
    private String sectionVisibilityRule
    private String sectionVisibilityRuleParam
    private String completionPercentageRule
    private String completionPercentageRuleParam

    @JsonIgnore
    private Section section

    SectionNodeInstance(Section section,
                        DisplayTextLanguage displayTextLanguage,
                        LocalDate currentDate,
                        QuestionnaireTranslationService questionnaireTranslationService) {
        super(section, displayTextLanguage, currentDate)
        this.section = section
        this.applicantType = section.getApplicantType()
        this.sectionCompletionRule = section.getSectionCompletionRule()
        this.sectionCompletionRuleParam = section.getSectionCompletionRuleParam()
        this.sectionVisibilityRule = section.getSectionVisibilityRule()
        this.sectionVisibilityRuleParam = section.getSectionVisibilityRuleParam()
        this.completionPercentageRule = section.getCompletionPercentageRule()
        this.completionPercentageRuleParam = section.getCompletionPercentageRuleParam()
        this.populateDisplayText(questionnaireTranslationService)
    }

    void populateDisplayText(QuestionnaireTranslationService questionnaireTranslationService) {
        String defaultShortName = this.section.getShortName()
        this.shortName = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.SECTION_MENU,
                this.section.id, this.displayTextLanguage) ?: defaultShortName

        String defaultDisplayText = this.section.getDisplayText()
        this.displayText = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.SECTION,
                this.section.id, this.displayTextLanguage) ?: defaultDisplayText
    }

    String getApplicantType() {
        return applicantType
    }

    void setApplicantType(String applicantType) {
        this.applicantType = applicantType
    }

    String getShortName() {
        return shortName
    }

    void setShortName(String shortName) {
        this.shortName = shortName
    }

    String getSectionCompletionRule() {
        return sectionCompletionRule
    }

    void setSectionCompletionRule(String sectionCompletionRule) {
        this.sectionCompletionRule = sectionCompletionRule
    }

    String getSectionCompletionRuleParam() {
        return sectionCompletionRuleParam
    }

    void setSectionCompletionRuleParam(String sectionCompletionRuleParam) {
        this.sectionCompletionRuleParam = sectionCompletionRuleParam
    }

    String getSectionVisibilityRule() {
        return sectionVisibilityRule
    }

    void setSectionVisibilityRule(String sectionVisibilityRule) {
        this.sectionVisibilityRule = sectionVisibilityRule
    }

    String getSectionVisibilityRuleParam() {
        return sectionVisibilityRuleParam
    }

    void setSectionVisibilityRuleParam(String sectionVisibilityRuleParam) {
        this.sectionVisibilityRuleParam = sectionVisibilityRuleParam
    }

    String getCompletionPercentageRule() {
        return completionPercentageRule
    }

    void setCompletionPercentageRule(String completionPercentageRule) {
        this.completionPercentageRule = completionPercentageRule
    }

    String getCompletionPercentageRuleParam() {
        return completionPercentageRuleParam
    }

    void setCompletionPercentageRuleParam(String completionPercentageRuleParam) {
        this.completionPercentageRuleParam = completionPercentageRuleParam
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {
        nodeInstanceVisitor.visit(this)
    }

    @Override
    EasyVisaNode getDefinitionNode() {
        this.section
    }
}
