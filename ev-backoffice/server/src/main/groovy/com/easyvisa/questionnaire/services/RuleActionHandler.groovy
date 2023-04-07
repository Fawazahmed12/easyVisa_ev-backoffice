package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.*
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.dto.CompletionWarningDto
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.rule.displayorder.FormlyQuestionnaireDisplayOrderComponent
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

@Component
class RuleActionHandler {

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    FormlyQuestionnaireDisplayOrderComponent formlyQuestionnaireDisplayOrderComponent

    @Autowired
    private MessageSource messageSource

    protected INodeComputeRule getNodeComputeRule(NodeRuleEvaluationContext ruleEvauluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvauluationContext.getEasyVisaNodeInstance()
        String ruleClassName = easyVisaNodeInstance.getRuleClassName()
        INodeComputeRule computeRule = ruleComponentRegistry.getNodeComputeRule(ruleClassName)
        return computeRule
    }

    /**
     * Gets called when we want to determine the visibile of a node (question, repeatingQuestionGroup, subsection etc)
     * @param ruleEvauluationContext
     */
    @SuppressWarnings("GroovyVariableCanBeFinal")
    void updateVisibilityOnSuccessfulNodeRule(NodeRuleEvaluationContext ruleEvauluationContext) {
        INodeComputeRule computeRule = getNodeComputeRule(ruleEvauluationContext)
        if (!Objects.isNull(computeRule)) {
            computeRule.updateVisibilityOnSuccessfulMatch(ruleEvauluationContext)
        }
    }

    /**
     * Gets called after evaluation is done. This method allows us to override or prepopulate the answer
     * @param ruleEvauluationContext
     * @param answer
     * @param outcome
     */
    @SuppressWarnings("GroovyVariableCanBeFinal")
    String determineAnswer(NodeRuleEvaluationContext ruleEvauluationContext, Answer answer, Outcome outcome) {
        INodeComputeRule computeRule = getNodeComputeRule(ruleEvauluationContext)
        if (!Objects.isNull(computeRule)) {
            return computeRule.determineAnswer(ruleEvauluationContext, answer, outcome)
        }
        return answer?.value
    }

    /**
     * Gets called when user saves a answer in form
     *
     * @param nodeRuleEvaluationContext
     */
    void triggerFormActionOnSuccessfulNodeRule(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        INodeComputeRule computeRule = getNodeComputeRule(nodeRuleEvaluationContext)
        if (!Objects.isNull(computeRule)) {
            computeRule.triggerFormActionOnSuccessfulMatch(nodeRuleEvaluationContext, previousAnswer)
        }
    }

    /**
     * Gets called when user removes the entire(zero) repeating-question-group instance
     *
     * @param nodeRuleEvaluationContext
     */
    void resetTriggeringQuestionsNodeRule(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IResetQuestionRule resetQuestionRule = ruleComponentRegistry.getResetQuestionRule(ruleClassName)
        if (!Objects.isNull(resetQuestionRule)) {
            resetQuestionRule.resetTriggeringQuestions(nodeRuleEvaluationContext)
        }
    }


    InputSourceType generateDynamicInputSource(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IDynamicInputDatasourceRule inputDataSourceRule = ruleComponentRegistry.getDynamicInputDataSourceRule(ruleClassName)
        if (!Objects.isNull(inputDataSourceRule)) {
            return inputDataSourceRule.generateInputSourceType(nodeRuleEvaluationContext)
        }
        return null
    }

    String generateToolTip(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IDynamicToolTipRule dynamicToolTipRule = ruleComponentRegistry.getDynamicTooltipRule(ruleClassName)
        if (!Objects.isNull(dynamicToolTipRule)) {
            return dynamicToolTipRule.generateDynamicTooltip(nodeRuleEvaluationContext)
        }
        return null
    }

    String generateDisplayText(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IDisplayTextRule displayTextRule = ruleComponentRegistry.getDisplayTextRule(ruleClassName)
        if (!Objects.isNull(displayTextRule)) {
            return displayTextRule.generateDisplayText(nodeRuleEvaluationContext)
        }
        return null
    }

    void generateDynamicAttribute(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IDynamicAttributeRule dynamicAttributeRule = ruleComponentRegistry.getDynamicAttributeRule(ruleClassName)
        if (!Objects.isNull(dynamicAttributeRule)) {
            dynamicAttributeRule.generateDynamicAttribute(nodeRuleEvaluationContext)
        }
    }

    Boolean validateAnswerCompletion(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IAnswerCompletionValidationRule answerCompletionValidationRule = ruleComponentRegistry.getAnswerCompletionValidationRule(ruleClassName)
        if (!Objects.isNull(answerCompletionValidationRule)) {
            return answerCompletionValidationRule.validateAnswerCompletion(nodeRuleEvaluationContext)
        }
        return false;
    }

    Boolean validateAnswerVisibility(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IAnswerVisibilityValidationRule answerVisibilityValidationRule = ruleComponentRegistry.getAnswerVisibilityValidationRule(ruleClassName)
        if (!Objects.isNull(answerVisibilityValidationRule)) {
            return answerVisibilityValidationRule.validateAnswerVisibility(nodeRuleEvaluationContext)
        }
        return false;
    }

    void populatePdfFieldAnswer(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IAnswerVisibilityValidationRule answerVisibilityValidationRule = ruleComponentRegistry.getAnswerVisibilityValidationRule(ruleClassName)
        if (!Objects.isNull(answerVisibilityValidationRule)) {
            answerVisibilityValidationRule.populatePdfFieldAnswer(nodeRuleEvaluationContext)
        }
    }

    /**
     * Gets called when user tries to save a answer, whose question is having answerValidationRule... Called from AnswerService
     *
     * @param nodeRuleEvaluationContext
     */
    AnswerValidationDto validateAnswer(String ruleClassName, AnswerValidationRuleEvaluationContext ruleEvaluationContext) {
        IAnswerValidationRule answerValidationRule = ruleComponentRegistry.getAnswerValidationRule(ruleClassName)
        if (!Objects.isNull(answerValidationRule)) {
            return answerValidationRule.validateAnswer(ruleEvaluationContext)
        }
        return new AnswerValidationDto();
    }

    void orderDisplayQuestions(FormlyFieldEvaluationContext fieldDtoEvaluationContext) {
        formlyQuestionnaireDisplayOrderComponent.orderDisplayQuestions(fieldDtoEvaluationContext);
    }

    //This method only gets called, if all the visible questions in this section has answered...
    Boolean validateCompletionStatus(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        ISectionCompletionRule sectionCompletionRule = ruleComponentRegistry.getSectionCompletionRule(ruleClassName)
        if (!Objects.isNull(sectionCompletionRule)) {
            return sectionCompletionRule.validateAnswerCompletion(nodeRuleEvaluationContext)
        }
        return true;
    }

    CompletionWarningDto getCompletionWarning(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        ISectionCompletionRule sectionCompletionRule = ruleComponentRegistry.getSectionCompletionRule(ruleClassName)
        if (!Objects.isNull(sectionCompletionRule)) {
            CompletionWarningDto cwdto = sectionCompletionRule.generateCompletionWarning(nodeRuleEvaluationContext)
            cwdto.warningMessage = resolveMessageCode(cwdto.warningMessage)
            return cwdto
        }
        return new CompletionWarningDto();
    }

    void updatedDependentSectionCompletion(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        ISectionCompletionRule sectionCompletionRule = ruleComponentRegistry.getSectionCompletionRule(ruleClassName)
        if (!Objects.isNull(sectionCompletionRule)) {
            sectionCompletionRule.updatedDependentSectionCompletion(nodeRuleEvaluationContext)
        }
    }


    void updateSectionVisibilityOnSuccessfulNodeRule(String visibilityRule, SectionVisibilityRuleEvaluationContext ruleEvaluationContext) {
        ISectionVisibilityRule sectionVisibilityRule = ruleComponentRegistry.getSectionVisibilityRule(visibilityRule)
        if (!Objects.isNull(sectionVisibilityRule)) {
            sectionVisibilityRule.updateVisibilityOnSuccessfulMatch(ruleEvaluationContext)
        }
    }


    void updateVisibilityByComputeRule(NodeRuleEvaluationContext ruleEvauluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvauluationContext.getEasyVisaNodeInstance()
        String ruleClassName = easyVisaNodeInstance.getVisibilityRuleClassName()
        IVisibilityComputeRule computeRule = ruleComponentRegistry.getVisibilityComputeRule(ruleClassName)
        if (!Objects.isNull(computeRule)) {
            computeRule.updateVisibilityOnSuccessfulMatch(ruleEvauluationContext)
        }
    }


    double calculateCompletionPercentage(String ruleClassName, CompletionPercentageRuleEvaluationContext ruleEvaluationContext) {
        ICompletionPercentageRule completionPercentageRule = ruleComponentRegistry.getCompletionPercentageRule(ruleClassName)
        if (!Objects.isNull(completionPercentageRule)) {
            return completionPercentageRule.calculateCompletionPercentage(ruleEvaluationContext)
        }
        return ruleEvaluationContext.completedPercentage;
    }


    void executeMilestoneReminderRule(String ruleClassName, MilestoneReminderEvaluationContext ruleEvaluationContext) {
        IMilestoneReminderRule milestoneReminderRule = ruleComponentRegistry.getMilestoneReminderRule(ruleClassName)
        if (!Objects.isNull(milestoneReminderRule)) {
            milestoneReminderRule.executeMilestoneReminder(ruleEvaluationContext)
        }
    }


    String generatePdfFieldRelationship(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        IPdfFieldRelationshipRule pdfFieldRelationshipRule = ruleComponentRegistry.getPdfFieldRelationshipRule(ruleClassName)
        if (!Objects.isNull(pdfFieldRelationshipRule)) {
            return pdfFieldRelationshipRule.evaluateRelationshipType(nodeRuleEvaluationContext)
        }
        return null
    }


    /**
     * Gets called when user adds the new repeating-question-group instance
     *
     * @param nodeRuleEvaluationContext
     */
    void executeRepeatGroupOnEntryRule(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Optional<IRepeatGroupLifeCycleRule> repeatGroupLifeCycleRuleOptional = Optional.ofNullable(ruleComponentRegistry.getRepeatGroupLifeCycleRule(ruleClassName));
        repeatGroupLifeCycleRuleOptional.ifPresent({ repeatGroupLifeCycleRule -> repeatGroupLifeCycleRule.onEntry(nodeRuleEvaluationContext) })
    }

    /**
     * Gets called before user removes the repeating-question-group instance
     *
     * @param nodeRuleEvaluationContext
     */
    void executeRepeatGroupOnPreExitRule(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Optional<IRepeatGroupLifeCycleRule> repeatGroupLifeCycleRuleOptional = Optional.ofNullable(ruleComponentRegistry.getRepeatGroupLifeCycleRule(ruleClassName));
        repeatGroupLifeCycleRuleOptional.ifPresent({ repeatGroupLifeCycleRule -> repeatGroupLifeCycleRule.onPreExit(nodeRuleEvaluationContext) })
    }

    /**
     * Gets called after user removes the repeating-question-group instance
     *
     * @param nodeRuleEvaluationContext
     */
    void executeRepeatGroupOnPostExitRule(String ruleClassName, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Optional<IRepeatGroupLifeCycleRule> repeatGroupLifeCycleRuleOptional = Optional.ofNullable(ruleComponentRegistry.getRepeatGroupLifeCycleRule(ruleClassName));
        repeatGroupLifeCycleRuleOptional.ifPresent({ repeatGroupLifeCycleRule -> repeatGroupLifeCycleRule.onPostExit(nodeRuleEvaluationContext) })
    }

    private String resolveMessageCode(String messageCode) {
        if (!messageCode) return messageCode
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.locale)
    }
}
