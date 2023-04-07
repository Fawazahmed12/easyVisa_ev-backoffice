package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.questionnaire.answering.rule.*
import org.springframework.stereotype.Component

@Component
class RuleComponentRegistry {
    private Map<String, INodeComputeRule> nodeComputeRuleMap = new HashMap<>()
    private Map<String, IDynamicInputDatasourceRule> dynamicInputDatasourceRuleHashMap = new HashMap<>()
    private Map<String, IDynamicToolTipRule> dynamicToolTipRuleHashMap = new HashMap<>()
    private Map<String, IDisplayTextRule> displayTextRuleMap = new HashMap<>()
    private Map<String, IDynamicAttributeRule> dynamicAttributeRuleHashMap = new HashMap<>()
    private Map<String, IAnswerCompletionValidationRule> answerCompletionValidationRuleHashMap = new HashMap<>()
    private Map<String, IAnswerValidationRule> answerVisibilityValidationRuleHashMap = new HashMap<>()
    private Map<String, IAnswerValidationRule> answerValidationRuleHashMap = new HashMap<>()
    private Map<String, ISectionCompletionRule> sectionCompletionRuleHashMap = new HashMap<>()
    private Map<String, ISectionVisibilityRule> sectionVisibilityRuleHashMap = new HashMap<>()
    private Map<String, ICompletionPercentageRule> completionPercentageRuleHashMap = new HashMap<>()
    private Map<String, IResetQuestionRule> resetQuestionRuleHashMap = new HashMap<>()
    private Map<String, IRepeatGroupLifeCycleRule> repeatGroupLifeCycleRuleHashMap = new HashMap<>()
    private Map<String, IMilestoneReminderRule> milestoneReminderRuleMap = new HashMap<>()
    private Map<String, IPdfFieldRelationshipRule> pdfFieldRelationshipRuleMap = new HashMap<>()
    private Map<String, IVisibilityComputeRule> visibilityComputeRuleMap = new HashMap<>()

    void registerDynamicInputRule(String ruleName, IDynamicInputDatasourceRule ruleComponent) {
        dynamicInputDatasourceRuleHashMap[ruleName] = ruleComponent;
    }

    void registerDynamicAttributeRule(String ruleName, IDynamicAttributeRule ruleComponent) {
        dynamicAttributeRuleHashMap[ruleName] = ruleComponent;
    }

    void registerDisplayTextRule(String ruleName, IDisplayTextRule ruleComponent) {
        displayTextRuleMap[ruleName] = ruleComponent;
    }

    void registerMilestoneReminderRule(String ruleName, IMilestoneReminderRule ruleComponent) {
        milestoneReminderRuleMap[ruleName] = ruleComponent;
    }

    void registerAnswerCompletionValidationRule(String ruleName, IAnswerCompletionValidationRule ruleComponent) {
        answerCompletionValidationRuleHashMap[ruleName] = ruleComponent;
    }

    void registerAnswerVisibilityValidationRule(String ruleName, IAnswerVisibilityValidationRule ruleComponent) {
        answerVisibilityValidationRuleHashMap[ruleName] = ruleComponent;
    }

    void registerAnswerValidationRule(String ruleName, IAnswerValidationRule ruleComponent) {
        answerValidationRuleHashMap[ruleName] = ruleComponent;
    }

    void registerNodeRules(String ruleName, INodeComputeRule ruleComponent) {
        nodeComputeRuleMap[ruleName] = ruleComponent
    }

    void registerScetionCompletionRules(String ruleName, ISectionCompletionRule ruleComponent) {
        sectionCompletionRuleHashMap[ruleName] = ruleComponent
    }

    void registerSectionVisibilityRules(String ruleName, ISectionVisibilityRule ruleComponent) {
        sectionVisibilityRuleHashMap[ruleName] = ruleComponent
    }

    void registerCompletionPercentageRules(String ruleName, ICompletionPercentageRule ruleComponent) {
        completionPercentageRuleHashMap[ruleName] = ruleComponent
    }

    void registerQuestionResetRule(String ruleName, IResetQuestionRule ruleComponent) {
        resetQuestionRuleHashMap[ruleName] = ruleComponent;
    }

    void registerRepeatGroupLifeCycleRule(String ruleName, IRepeatGroupLifeCycleRule ruleComponent) {
        repeatGroupLifeCycleRuleHashMap[ruleName] = ruleComponent;
    }

    void registerPdfFieldRelationshipRule(String ruleName, IPdfFieldRelationshipRule ruleComponent) {
        pdfFieldRelationshipRuleMap[ruleName] = ruleComponent;
    }

    void registerVisibilityComputeRules(String ruleName, IVisibilityComputeRule ruleComponent) {
        visibilityComputeRuleMap[ruleName] = ruleComponent
    }

    INodeComputeRule getNodeComputeRule(String ruleName) {
        return nodeComputeRuleMap[ruleName]
    }

    IDynamicInputDatasourceRule getDynamicInputDataSourceRule(String ruleName) {
        return dynamicInputDatasourceRuleHashMap[ruleName]
    }

    IDynamicAttributeRule getDynamicAttributeRule(String ruleName) {
        return dynamicAttributeRuleHashMap[ruleName]
    }

    void registerDynamicTooltiptRule(String ruleName, IDynamicToolTipRule ruleComponent) {
        dynamicToolTipRuleHashMap[ruleName] = ruleComponent;
    }

    IDynamicToolTipRule getDynamicTooltipRule(String ruleName) {
        return dynamicToolTipRuleHashMap[ruleName]
    }

    IDisplayTextRule getDisplayTextRule(String ruleName) {
        return displayTextRuleMap[ruleName]
    }

    IMilestoneReminderRule getMilestoneReminderRule(String ruleName) {
        return milestoneReminderRuleMap[ruleName]
    }

    IAnswerCompletionValidationRule getAnswerCompletionValidationRule(String ruleName) {
        return answerCompletionValidationRuleHashMap[ruleName]
    }

    IAnswerVisibilityValidationRule getAnswerVisibilityValidationRule(String ruleName) {
        return answerVisibilityValidationRuleHashMap[ruleName]
    }

    IAnswerValidationRule getAnswerValidationRule(String ruleName) {
        return answerValidationRuleHashMap[ruleName]
    }

    ISectionCompletionRule getSectionCompletionRule(String ruleName) {
        return sectionCompletionRuleHashMap[ruleName]
    }

    ISectionVisibilityRule getSectionVisibilityRule(String ruleName) {
        return sectionVisibilityRuleHashMap[ruleName]
    }

    ICompletionPercentageRule getCompletionPercentageRule(String ruleName) {
        return completionPercentageRuleHashMap[ruleName]
    }

    IResetQuestionRule getResetQuestionRule(String ruleName) {
        return resetQuestionRuleHashMap[ruleName]
    }

    IRepeatGroupLifeCycleRule getRepeatGroupLifeCycleRule(String ruleName) {
        return repeatGroupLifeCycleRuleHashMap[ruleName]
    }

    IPdfFieldRelationshipRule getPdfFieldRelationshipRule(String ruleName) {
        return pdfFieldRelationshipRuleMap[ruleName]
    }

    IVisibilityComputeRule getVisibilityComputeRule(String ruleName) {
        return visibilityComputeRuleMap[ruleName]
    }
}
