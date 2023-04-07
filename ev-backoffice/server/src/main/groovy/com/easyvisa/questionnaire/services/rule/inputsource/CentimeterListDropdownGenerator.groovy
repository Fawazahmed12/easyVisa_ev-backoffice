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
 * Question: Centimeters
 * Notes:  This rule will generate the dropdown options for the question 'centimeters'
 *         Drop down list goes from 61-300 (EV-1282)
 */
@Component
class CentimeterListDropdownGenerator implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'CentimeterListDropdownGenerator'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }

    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<InputSourceType.ValueMap> values = []
        (61..300).each { value ->
            values.add(new InputSourceType.ValueMap(Integer.toString(value)));
        }
        InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values);
        return inputSourceType;
    }
}
