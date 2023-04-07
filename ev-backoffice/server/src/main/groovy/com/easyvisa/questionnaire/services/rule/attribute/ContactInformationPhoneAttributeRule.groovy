package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import org.springframework.stereotype.Component

@Component
class ContactInformationPhoneAttributeRule extends PhoneNumberAttributeRule {

    private static String RULE_NAME = 'ContactInformationPhoneAttributeRule'

    @Override
    String getRuleName() {
        return RULE_NAME;
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        String attributeRuleParam = questionNodeInstance.getAttributeRuleParam();
        String[] attributeRuleParams = attributeRuleParam.split(',');
        String contactInformationAvailabilityFieldPath = attributeRuleParams[0];
        Answer contactInformationAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(contactInformationAvailabilityFieldPath);
        if (Answer.isValidAnswer(contactInformationAvailabilityAnswer) && contactInformationAvailabilityAnswer.value == 'true') {
            Map attributes = questionNodeInstance.getAttributes();
            attributes.put(TemplateOptionAttributes.DISABLED.getValue(), true);
        } else {
            String dependentCountryPath = (attributeRuleParams.length > 1) ? attributeRuleParams[1] : '';
            Answer selectedCountryAnswer = ruleEvaluationContext.findAnswerByPath(dependentCountryPath);
            String selectedCountryValue = selectedCountryAnswer?.value ?: Country.UNITED_STATES.getDisplayName()
            if (attributeRuleParams.length == 1) {
                // If the param size is one the, its from subsection 'Intended Daytime Phone Number in United States'
                selectedCountryValue = Country.UNITED_STATES.getDisplayName();
            }
            this.generatePhoneNumberAttributes(questionNodeInstance, selectedCountryValue);
        }
    }
}
