package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.enums.QuestionnaireDisplayNodeType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityEvaluator
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.fasterxml.jackson.annotation.JsonIgnore

import java.time.LocalDate

class QuestionNodeInstance extends EasyVisaNodeInstance {
    private String inputType
    private String dataType;
    private String defaultValue;
    private Boolean required;
    private Boolean readOnly;
    private Boolean excludeFromPercentageCalculation;
    private String displayTextRule;
    private String displayTextRuleParam;
    private String dynamicTooltipRule;
    private String attributeRule;
    private String attributeRuleParam;
    private String answerCompletionValidationRule;
    private String answerCompletionValidationRuleParam;
    private String answerVisibilityValidationRule;
    private String answerVisibilityValidationRuleParam;
    private String answerValidationRule;
    private String answerValidationRuleParam;
    private String pdfFieldRelationshipRule;
    private String pdfFieldRelationshipRuleParam;
    private String tooltip;
    private String inputTypeSource;
    private String inputTypeSourceRule;
    private String inputTypeSourceRuleParam;
    private String styleClassName;
    private String wrapperName;
    private String errorMessage;
    private String contextualClue;


    private InputSourceType inputSourceType
    private Answer answer

    Integer repeatingIndex

    private Map attributes = new HashMap();

    @JsonIgnore
    private Question question

    QuestionNodeInstance(Question question, Answer answer, Integer repeatingIndex,
                         DisplayTextLanguage displayTextLanguage, LocalDate currentDate,
                         QuestionnaireTranslationService questionnaireTranslationService) {
        this(question, displayTextLanguage, currentDate);
        this.answer = answer
        this.repeatingIndex = repeatingIndex
        this.populateDisplayText(questionnaireTranslationService)
    }

    QuestionNodeInstance(Question question, DisplayTextLanguage displayTextLanguage,
                         LocalDate currentDate) {
        super(question, displayTextLanguage, currentDate)
        this.question = question
        this.copyQuestionProperties(question)
        this.setOrder(this.question.getOrder())
    }

    private copyQuestionProperties(Question question) {
        this.inputType = question.getInputType()
        this.dataType = question.getDataType()
        this.wrapperName = question.getWrapperName()
        this.styleClassName = question.getStyleClassName()
        this.defaultValue = question.getDefaultValue()
        this.required = question.getRequired()
        this.displayText = question.getDisplayText()
        this.inputTypeSourceRule = question.getInputTypeSourceRule()
        this.inputTypeSourceRuleParam = question.getInputTypeSourceRuleParam()
        this.displayTextRule = question.getDisplayTextRule()
        this.displayTextRuleParam = question.getDisplayTextRuleParam()
        this.dynamicTooltipRule = question.getTooltipRule()
        this.attributeRule = question.getAttributeRule()
        this.attributeRuleParam = question.getAttributeRuleParam()
        this.answerCompletionValidationRule = question.getAnswerCompletionValidationRule()
        this.answerCompletionValidationRuleParam = question.getAnswerCompletionValidationRuleParam()
        this.answerVisibilityValidationRule = question.getAnswerVisibilityValidationRule()
        this.answerVisibilityValidationRuleParam = question.getAnswerVisibilityValidationRuleParam()
        this.answerValidationRule = question.getAnswerValidationRule()
        this.answerValidationRuleParam = question.getAnswerValidationRuleParam()
        this.pdfFieldRelationshipRule = question.getPdfFieldRelationshipRule()
        this.pdfFieldRelationshipRuleParam = question.getPdfFieldRelationshipRuleParam()
        this.tooltip = question.getTooltip()
        this.inputTypeSource = question.getInputSourceType()
        this.errorMessage = question.getErrorMessage()
        this.contextualClue = question.getContextualClue()
        this.readOnly = question.getReadonly()
        this.excludeFromPercentageCalculation = question.getExcludeFromPercentageCalculation()
    }

    void populateDisplayText(QuestionnaireTranslationService questionnaireTranslationService) {
        String defaultDisplayText = this.question.getDisplayText()
        this.displayText = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.QUESTION,
                this.question.id, this.displayTextLanguage) ?: defaultDisplayText

        String defaultTooltip = this.question.getTooltip()
        this.tooltip = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.TOOLTIP,
                this.question.id, this.displayTextLanguage) ?: defaultTooltip

        String defaultContextualClue = this.question.getContextualClue()
        this.contextualClue = questionnaireTranslationService.getTranslatorValue(this.questVersion, QuestionnaireDisplayNodeType.CONTEXTUAL_CLUE,
                this.question.id, this.displayTextLanguage) ?: defaultContextualClue
    }

    @Override
    EasyVisaNode getDefinitionNode() {
        return this.question
    }

    @Override
    void accept(INodeInstanceVisitor nodeInstanceVisitor) {
        this.setVisibility(true)
        nodeInstanceVisitor.visit(this)
    }

    Answer getAnswer() {
        answer
    }

    void setAnswer(Answer answer) {
        this.answer = answer
    }

    int getAnswerIndex() {
        return answer.getIndex()
    }

    InputSourceType getInputSourceType() {
        return inputSourceType
    }

    void setInputSourceType(InputSourceType inputSourceType) {
        this.inputSourceType = inputSourceType
    }

    String getInputType() {
        return inputType
    }

    void setInputType(String inputType) {
        this.inputType = inputType
    }

    String getDataType() {
        return dataType
    }

    void setDataType(String dataType) {
        this.dataType = dataType
    }

    String getDefaultValue() {
        return defaultValue
    }

    void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue
    }

    Boolean getRequired() {
        return required
    }

    void setRequired(Boolean required) {
        this.required = required
    }

    String getDisplayTextRule() {
        return displayTextRule
    }

    void setDisplayTextRule(String displayTextRule) {
        this.displayTextRule = displayTextRule
    }

    String getDisplayTextRuleParam() {
        return displayTextRuleParam
    }

    void setDisplayTextRuleParam(String displayTextRuleParam) {
        this.displayTextRuleParam = displayTextRuleParam
    }

    String getDynamicTooltipRule() {
        return dynamicTooltipRule
    }

    void setDynamicTooltipRule(String dynamicTooltipRule) {
        this.dynamicTooltipRule = dynamicTooltipRule
    }

    String getAttributeRule() {
        return attributeRule
    }

    void setAttributeRule(String attributeRule) {
        this.attributeRule = attributeRule
    }


    String getAttributeRuleParam() {
        return attributeRuleParam
    }

    void setAttributeRuleParam(String attributeRuleParam) {
        this.attributeRuleParam = attributeRuleParam
    }


    String getAnswerCompletionValidationRule() {
        return answerCompletionValidationRule
    }

    void setAnswerCompletionValidationRule(String answerCompletionValidationRule) {
        this.answerCompletionValidationRule = answerCompletionValidationRule
    }


    String getAnswerCompletionValidationRuleParam() {
        return answerCompletionValidationRuleParam
    }

    void setAnswerCompletionValidationRuleParam(String answerCompletionValidationRuleParam) {
        this.answerCompletionValidationRuleParam = answerCompletionValidationRuleParam
    }

    String getAnswerVisibilityValidationRule() {
        return answerVisibilityValidationRule
    }

    void setAnswerVisibilityValidationRule(String answerVisibilityValidationRule) {
        this.answerVisibilityValidationRule = answerVisibilityValidationRule
    }

    String getAnswerVisibilityValidationRuleParam() {
        return answerVisibilityValidationRuleParam
    }

    void setAnswerVisibilityValidationRuleParam(String answerVisibilityValidationRuleParam) {
        this.answerVisibilityValidationRuleParam = answerVisibilityValidationRuleParam
    }

    String getAnswerValidationRule() {
        return answerValidationRule
    }

    void setAnswerValidationRule(String answerValidationRule) {
        this.answerValidationRule = answerValidationRule
    }

    String getAnswerValidationRuleParam() {
        return answerValidationRuleParam
    }

    void setAnswerValidationRuleParam(String answerValidationRuleParam) {
        this.answerValidationRuleParam = answerValidationRuleParam
    }

    String getPdfFieldRelationshipRule() {
        return pdfFieldRelationshipRule
    }

    void setPdfFieldRelationshipRule(String pdfFieldRelationshipRule) {
        this.pdfFieldRelationshipRule = pdfFieldRelationshipRule
    }

    String getPdfFieldRelationshipRuleParam() {
        return pdfFieldRelationshipRuleParam
    }

    void setPdfFieldRelationshipRuleParam(String pdfFieldRelationshipRuleParam) {
        this.pdfFieldRelationshipRuleParam = pdfFieldRelationshipRuleParam
    }

    String getTooltip() {
        return tooltip
    }

    void setTooltip(String tooltip) {
        this.tooltip = tooltip
    }


    String getInputTypeSource() {
        return inputTypeSource
    }

    void setInputTypeSource(String inputTypeSource) {
        this.inputTypeSource = inputTypeSource
    }

    String getInputTypeSourceRule() {
        return inputTypeSourceRule
    }

    void setInputTypeSourceRule(String inputTypeSourceRule) {
        this.inputTypeSourceRule = inputTypeSourceRule
    }

    String getInputTypeSourceRuleParam() {
        return inputTypeSourceRuleParam
    }

    void setInputTypeSourceRuleParam(String inputTypeSourceRuleParam) {
        this.inputTypeSourceRuleParam = inputTypeSourceRuleParam
    }

    Integer getRepeatingIndex() {
        return repeatingIndex
    }

    void setRepeatingIndex(Integer repeatingIndex) {
        this.repeatingIndex = repeatingIndex
    }

    Question getQuestion() {
        return question
    }

    void setQuestion(Question question) {
        this.question = question
    }

    String getStyleClassName() {
        return styleClassName
    }

    void setStyleClassName(String styleClassName) {
        this.styleClassName = styleClassName
    }

    String getWrapperName() {
        return wrapperName
    }

    void setWrapperName(String wrapperName) {
        this.wrapperName = wrapperName
    }

    Map getAttributes() {
        return attributes
    }

    void setAttributes(Map attributes) {
        this.attributes = attributes
    }

    String getErrorMessage() {
        return errorMessage
    }

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    String getContextualClue() {
        return contextualClue
    }

    void setContextualClue(String contextualClue) {
        this.contextualClue = contextualClue
    }

    Boolean isReadOny() {
        return this.readOnly
    }

    Boolean hasExcludeFromPercentageCalculation() {
        return this.excludeFromPercentageCalculation
    }

    List<String> getWrappers() {
        List<String> wrappers = [];
        if (wrapperName != null) {
            wrappers = wrapperName.split(",");
        }
        return wrappers;
    }


    void collectAnswer(AnswerEvaluationContext answerEvaluationContext, IAnswerVisibilityEvaluator answerVisibilityEvaluator, List<Answer> answerList) {
        if (this.inputType != InputTypeConstant.LABEL.value) {
            answerList.add(this.answer);
        }
        super.collectAnswer(answerEvaluationContext, answerVisibilityEvaluator, answerList);
    }

    Boolean hasValidEasyVisaInstance(AnswerEvaluationContext answerEvaluationContext, IAnswerVisibilityEvaluator answerVisibilityEvaluator) {
        return answerVisibilityEvaluator.evaluate(answerEvaluationContext, this) && !answerEvaluationContext.hasExcludedPercentageCalculationQuestion(this);
    }
};
