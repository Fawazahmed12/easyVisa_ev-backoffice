package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.meta.InputSourceType

interface IDynamicInputDatasourceRule {
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext)
}