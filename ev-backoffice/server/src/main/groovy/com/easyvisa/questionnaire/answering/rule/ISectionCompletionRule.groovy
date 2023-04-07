package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.dto.CompletionWarningDto

interface ISectionCompletionRule {
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext);

    CompletionWarningDto generateCompletionWarning(NodeRuleEvaluationContext ruleEvaluationContext)

    void updatedDependentSectionCompletion(NodeRuleEvaluationContext ruleEvaluationContext)
}