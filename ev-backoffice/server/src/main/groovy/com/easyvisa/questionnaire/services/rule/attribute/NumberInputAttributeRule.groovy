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
 * EvNumberinputComponent(ev-numberinput) component in frontend, will accept only numeric
 * values.  It cannot allow special characters and it doesnot not allow decimals too...
 * Also, NO exponent letter ‘E’
 *
 * This is the generic inputtype component, we are using this for the following questions
 *  1. Zip Code   (EXACTLY 5 numbers)
 *  2. What is your USCIS ELIS Account Number?  (EXACTLY 12 numbers)
 *  ( Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115)
 *
 * Here only thing which is variable is  numeric characters length..
 * So this rule is passing numeric-chars length (from rule-param) as attribute...
 * By using this atrtribute 'EvNumberinputComponent' will handle the input length..
 *
 */
@Component
class NumberInputAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'NumberInputAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String numericCharacterLength = questionNodeInstance.getAttributeRuleParam();
        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.NUMERICCHARACTERLENGTH.getValue()] = numericCharacterLength;
    }
}
