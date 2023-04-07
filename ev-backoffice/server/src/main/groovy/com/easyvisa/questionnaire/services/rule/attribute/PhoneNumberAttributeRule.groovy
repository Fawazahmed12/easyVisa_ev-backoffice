package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
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
 * Here , If the selected Country is the United States, then the numeric field MUST have
 * EXACTLY 10 digits.
 * However there is no limit to the digits allowed in any other country.
 * Also, NO exponent letter ‘E’
 *
 * So here for the above validation, we need to know the selected-country for the above phoneNumber question.
 * This rule will assign the selectedCountry value to the attribute 'selectedCountry' inside templateOptions.
 */
@Component
class PhoneNumberAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'PhoneNumberAttributeRule'

    private static String COUNTRY_ATTRIBUTE_NAME = "selectedCountry";
    private static String CSS_ATTRIBUTE_VALUE = "phonenumber-tooltip";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(this.getRuleName(), this);
    }

    String getRuleName() {
        return RULE_NAME;
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String dependentCountryPath = questionNodeInstance.getAttributeRuleParam();
        Answer selectedCountryAnswer = ruleEvaluationContext.findAnswerByPath(dependentCountryPath);
        String selectedCountryValue = selectedCountryAnswer?.value ?: Country.UNITED_STATES.getDisplayName();
        this.generatePhoneNumberAttributes(questionNodeInstance, selectedCountryValue);
    }


    void generatePhoneNumberAttributes(QuestionNodeInstance questionNodeInstance, String selectedCountryValue) {
        Map attributes = questionNodeInstance.getAttributes();
        if (selectedCountryValue == Country.UNITED_STATES.getDisplayName()) {
            attributes[COUNTRY_ATTRIBUTE_NAME] = RelationshipTypeConstants.UNITED_STATES.value;
        } else {
            attributes[COUNTRY_ATTRIBUTE_NAME] = RelationshipTypeConstants.OTHER.value;
        }
        attributes[TemplateOptionAttributes.TOOLTIPCLASS.getValue()] = CSS_ATTRIBUTE_VALUE;
    }
}
