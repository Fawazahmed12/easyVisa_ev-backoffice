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
 * Section: Address History
 * SubSection: Current Physical Address
 * Question : (Q_53) Country of Domicile
 *
 *
 * Section: Statement from Applicant
 * SubSection: Extreme Hardship Statement
 * Question : (Q_3901) Explain in detail the 'extreme hardship' your spouse and/or parent(s) would experience if you are refused admission to the United States, and state why the USCIS should approve your application for a provisional unlawful presence waiver as a matter of discretion.
 */
@Component
class BigTooltipAttributeRule implements IDynamicAttributeRule {

    private static String RULE_NAME = 'BigTooltipAttributeRule'
    private static String CSS_ATTRIBUTE_VALUE = "big-tooltip";
    private static String TOOLTIP_CLOSE_DELAY_VALUE = 12000;

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
        attributes[TemplateOptionAttributes.TOOLTIPCLASS.getValue()] = CSS_ATTRIBUTE_VALUE;
        attributes[TemplateOptionAttributes.TOOLTIPCLOSEDELAY.getValue()] = TOOLTIP_CLOSE_DELAY_VALUE;
    }
}
