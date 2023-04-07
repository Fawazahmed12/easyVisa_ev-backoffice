package com.easyvisa.questionnaire.services.rule.inputsource


import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Biographic Information
 * SubSection: Height
 * Question: Feet
 * Notes:  This rule will generate the dropdown options for the question 'feet'
 *         Drop down list goes from 2-8
 */

@Component
class FeetListDropdownGenerator implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'FeetListDropdownGenerator'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<InputSourceType.ValueMap> values = []
        (0..8).each { value ->
            values.add(new InputSourceType.ValueMap(Integer.toString(value)));
        }
        InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values);
        return inputSourceType;
    }
}
