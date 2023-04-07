package com.easyvisa.questionnaire.services.rule.attribute

import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicAttributeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.dto.TemplateOptionAttributes
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class BMINumberInputAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'BMINumberInputAttributeRule'
    private static String ATTRIBUTE_NAME = "unit";
    private static String NUMERIC_CHARCTER_LENGTH = "3";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicAttributeRule(RULE_NAME, this);
    }

    @Override
    void generateDynamicAttribute(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String attributeRuleParam = questionNodeInstance.getAttributeRuleParam();
        String[] ruleParams = attributeRuleParam.split(",");
        String unitName = ruleParams[0];

        Map attributes = questionNodeInstance.getAttributes();
        attributes[TemplateOptionAttributes.NUMERICCHARACTERLENGTH.getValue()] = NUMERIC_CHARCTER_LENGTH;
        attributes[ATTRIBUTE_NAME] = unitName;

        if(ruleParams.size()==2){
            String MAX_INPUT_VALUE = ruleParams[1];
            attributes[TemplateOptionAttributes.MAXIMUMNUMERICVALUE.getValue()] = MAX_INPUT_VALUE;
        }
    }
}
