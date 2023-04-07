package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.springframework.stereotype.Component

@Component
class ContactInformationPhoneNumberAnswerCompletionRule extends PhoneNumberAnswerCompletionRule {

    private static String RULE_NAME = 'ContactInformationPhoneNumberAnswerCompletionRule'

    @Override
    String getRuleName() {
        return RULE_NAME;
    }


    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        if(!Answer.isValidAnswer(questionNodeInstance.answer)){
            return false;
        }

        String attributeRuleParam = questionNodeInstance.getAttributeRuleParam();
        String[] attributeRuleParams = attributeRuleParam.split(',');
        String contactInformationAvailabilityFieldPath = attributeRuleParams[0];
        Answer contactInformationAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(contactInformationAvailabilityFieldPath);
        if (Answer.isValidAnswer(contactInformationAvailabilityAnswer) && contactInformationAvailabilityAnswer.value == 'true') {
            return true;
        }

        String dependentCountryPath = (attributeRuleParams.length > 1) ? attributeRuleParams[1] : '';
        Answer selectedCountryAnswer = ruleEvaluationContext.findAnswerByPath(dependentCountryPath);
        String selectedCountryValue = selectedCountryAnswer?.value ?: ''
        if (attributeRuleParams.length == 1) {
            // If the param size is one the, its from subsection 'Intended Daytime Phone Number in United States'
            selectedCountryValue = Country.UNITED_STATES.getDisplayName();
        }
        return this.validatePhoneNumberAnswerCompletion(ruleEvaluationContext, selectedCountryValue);
    }
}
