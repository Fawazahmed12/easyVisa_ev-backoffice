package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Notes:  This rule will generate the list of countries(execpt united states) for dropdown options.
 */

@Component
class CountriesExcludingUSDropdownGenerator implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'CountriesExcludingUSDropdownGenerator'
    private static String US_VALUE = "United States";
    private static String COUNTRYLIST_INPUTTYPE_SOURCE = "countryListDropdown";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    private QuestionnaireService questionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        InputSourceType countryListInputSourceType = questionnaireService.getInputSourceType(COUNTRYLIST_INPUTTYPE_SOURCE, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage);
        List<InputSourceType.ValueMap> values = []
        countryListInputSourceType.getValues().each { countryValueMap ->
            if (countryValueMap.value != US_VALUE) {
                values.add(countryValueMap.clone());
            }
        }
        InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values);
        return inputSourceType;
    }
}
