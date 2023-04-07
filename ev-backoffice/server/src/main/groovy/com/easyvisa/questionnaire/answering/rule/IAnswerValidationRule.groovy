package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.dto.AnswerValidationDto

interface IAnswerValidationRule {
    AnswerValidationDto validateAnswer(AnswerValidationRuleEvaluationContext ruleEvaluationContext);
}