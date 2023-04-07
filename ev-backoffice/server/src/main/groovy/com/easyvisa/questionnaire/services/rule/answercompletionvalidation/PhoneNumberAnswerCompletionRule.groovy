package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerCompletionValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Contact Information
 * SubSection: Mobile Phone Number / Mobile Phone Number / Daytime/Home Phone Number
 * Question: (Q_81) Phone Number
 *           (Q_83) Phone Number
 *           (Q_85) Phone Number
 *
 * Here, This field is a numeric field, but we also need to allow hyphens (-), Space (), Parentheses (()), plus (+)
 * If the selected Country is the United States, then the numeric field MUST have EXACTLY 10 digits.
 * However there is no limit to the digits allowed in any other country
 */

@Component
class PhoneNumberAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'PhoneNumberAnswerCompletionRule'
    private static int MAX_US_PHONE_NUMERIC_LENGTH = 10
    private static int MIN_NONUS_PHONE_NUMERIC_LENGTH = 5
    private static int MAX_NONUS_PHONE_NUMERIC_LENGTH = 15

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerCompletionValidationRule(this.getRuleName(), this);
    }

    String getRuleName() {
        return RULE_NAME;
    }

    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        if(!Answer.isValidAnswer(questionNodeInstance.answer)){
            return false;
        }
        String dependentCountryPath = questionNodeInstance.getAnswerCompletionValidationRuleParam()
        Answer selectedCountryAnswer = ruleEvaluationContext.findAnswerByPath(dependentCountryPath);
        String selectedCountryValue = selectedCountryAnswer?.value ?: Country.UNITED_STATES.getDisplayName()
        return this.validatePhoneNumberAnswerCompletion(ruleEvaluationContext, selectedCountryValue);
    }

    Boolean validatePhoneNumberAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext, String selectedCountryValue) {
        if (selectedCountryValue == Country.UNITED_STATES.getDisplayName()) {
            return this.validateUSPhoneNumber(ruleEvaluationContext);
        }
        return this.validateNonUSPhoneNumber(ruleEvaluationContext);
    }

    //If the selected Country is the United States, then the numeric field MUST have EXACTLY 10 digits
    Boolean validateUSPhoneNumber(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer phoneNumberAnswer = questionNodeInstance.getAnswer();
        int phoneNumberNumericCount = this.getNumericCharacterCount(phoneNumberAnswer);
        return phoneNumberNumericCount == this.MAX_US_PHONE_NUMERIC_LENGTH;
    }

    // However there is no limit to the digits allowed in any other country
    Boolean validateNonUSPhoneNumber(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer phoneNumberAnswer = questionNodeInstance.getAnswer();
        int phoneNumberNumericCount = this.getNumericCharacterCount(phoneNumberAnswer);
        return phoneNumberNumericCount >= this.MIN_NONUS_PHONE_NUMERIC_LENGTH && phoneNumberNumericCount <= this.MAX_NONUS_PHONE_NUMERIC_LENGTH;
    }

    // https://stackoverflow.com/questions/12216065/how-to-extract-numeric-values-from-input-string-in-java
    int getNumericCharacterCount(Answer phoneNumberAnswer) {
        String currentPhoneNumberValue = phoneNumberAnswer.getValue();
        String[] matchedValues = currentPhoneNumberValue.replaceAll("[^0-9 ]", "").trim().split(" +");
        int currentPhoneNumberLength = 0;
        int phoneNumberNumericFieldLength = matchedValues.inject(currentPhoneNumberLength) { count, item ->
            count + item.size()
        }
        return phoneNumberNumericFieldLength;
    }
}