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
 * EvTextInputComponent(ev-textinput) component in frontend, will accept text input
 *
 *  Section: Inadmissibility and Other Legal  Issues
 *  SubSection: Immigration History General
 *  Question: (Q_3032) City or Town
 *  Notes: 50 Character limit
 *
 */
@Component
class TextInputAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'TextInputAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String inputCharacterLength = questionNodeInstance.getAttributeRuleParam();
        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.INPUTCHARACTERLENGTH.getValue()] = inputCharacterLength;
    }
}
