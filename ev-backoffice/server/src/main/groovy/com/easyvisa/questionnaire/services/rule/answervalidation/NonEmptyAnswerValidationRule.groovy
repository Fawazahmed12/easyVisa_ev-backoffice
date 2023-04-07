package com.easyvisa.questionnaire.services.rule.answervalidation


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerValidationRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerValidationRule
import com.easyvisa.questionnaire.dto.AnswerValidationDto
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Section: Name
 * SubSection: Current Legal Name
 * Question: Given Name (First name)
 *           Family Name/Last Name/Surname
 * Applicant Type: Petitioner & Beneficiary
 *
 * Notes:  We are syncing both 'firstName' and 'lastName' answers to Profile model and also in postgres there is a validation for these 2 fields
 *         that is which should be empty. So if user eneterd any empy values, then we should validate its answers
 */

@Component
class NonEmptyAnswerValidationRule implements IAnswerValidationRule {

    private static String RULE_NAME = 'NonEmptyAnswerValidationRule'
    private static String NON_EMPTY_ERROR_MESSAGE = "'#fieldName' of Profile cannot be empty, Please enter a valid value"
    private static String FIELD_NAME_PLACEHOLDER = '#fieldName'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerValidationRule(RULE_NAME, this);
    }

    @Override
    AnswerValidationDto validateAnswer(AnswerValidationRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.questionNodeInstance;

        Answer currentAnswer = questionNodeInstance.getAnswer()
        String currentAnswerValue = currentAnswer?.value ?: ''

        AnswerValidationDto answerValidationDto = ruleEvaluationContext.constructAnswerValidationDto();
        if (!StringUtils.isNotEmpty(currentAnswerValue)) {
            String validationRuleParam = questionNodeInstance.getAnswerValidationRuleParam();
            String updatedWarningText = NON_EMPTY_ERROR_MESSAGE.replaceAll(FIELD_NAME_PLACEHOLDER, "${validationRuleParam}")
            answerValidationDto.setErrorMessage(updatedWarningText);
            String answerPath = ruleEvaluationContext.getAnswerToSavePath();
            Answer previousAnswer = ruleEvaluationContext.findAnswerByPath(answerPath);
            answerValidationDto.setResetValue(previousAnswer?.value);
        }
        return answerValidationDto;
    }
}
