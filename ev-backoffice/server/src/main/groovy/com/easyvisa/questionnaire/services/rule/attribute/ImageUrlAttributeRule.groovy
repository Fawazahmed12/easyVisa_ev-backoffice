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
 * Section: Legal Status in U.S.
 * SubSection: LPR (Lawful Permanent Resident), also commonly referred to as a Green Card holder
 * Question: (Q_128) Class (Category) of Admission Help
 * Notes: Show sample Green Card graphic (file name: 'Location of Classification on Green Card') in this pop up showing the location of this class on the Green Card. The file is located in DropBox in the Questionnaire folder
 *
 *
 * This rule will set the 'hasImageUrl' attribute value as true..
 * Based on this value FormlyHorizontalWrapperComponent(in front-end) will display
 * image inside toolTip box
 *
 *
 */
@Component
class ImageUrlAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'ImageUrlAttributeRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.HASIMAGEURL.getValue()] = true;
    }
}
