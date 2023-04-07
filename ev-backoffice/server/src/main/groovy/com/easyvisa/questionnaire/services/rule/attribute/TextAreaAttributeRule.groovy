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
 *  This Rule will constrain the length of the characters in textarea
 *  based on the attribute param value
 *
 */
@Component
class TextAreaAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'TextAreaAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Integer maxLength = questionNodeInstance.getAttributeRuleParam().toInteger();
        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.MAXLENGTH.getValue()] = maxLength;
    }
}
