package com.easyvisa.questionnaire.services.rule.attribute


import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 *  EvNgDatePickerComponent(ev-ngdatepicker) component in frontend, max-date attribute validation
 *
 *  This rule is common for the  following questions
 *
 *  1. When does your passport expire?  (Notes: No max-date - need future dates)
 *          ( Sec_legalStatusInUS/SubSec_usCitizens/Q_119)
 *
 *
 */

@Component
class DatePickerMaxDateAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'DatePickerMaxDateAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String maxDateValue = questionNodeInstance.getAttributeRuleParam();
        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.MAXIMUMDATE.getValue()] = maxDateValue;
    }
}
