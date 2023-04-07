package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Income History
 * SubSection: Income History
 * Question: 1. Select the most recent year that you filed federal income taxes
 *           2. Select the second most recent year that you filed federal income taxes
 *           3. Select the third most recent year that you filed federal income taxes
 * Notes:  This rule will generate the dropdown options for the above questions
 *         Drop down list goes from currentYear to previous 15 years(approx.)
 */


@Component
class YearListDropdownGenerator implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'YearListDropdownGenerator'
    private static Integer YEAR_LIST_SIZE = 15;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }

    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<InputSourceType.ValueMap> values = []
        Integer startYear = ruleEvaluationContext.currentDate.year;
        Integer endYear = startYear - YEAR_LIST_SIZE;
        (startYear..endYear).each { value ->
            values.add(new InputSourceType.ValueMap(Integer.toString(value)));
        }
        InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values);
        return inputSourceType;
    }
}
