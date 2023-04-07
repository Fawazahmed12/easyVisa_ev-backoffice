package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Section: Address History
 * SubSection: Current Mailing Address
 * Question: Q_76
 *
 * Notes:  EV-3331 - This rule filters the list of states to the ones in Poverty guidelines IF
 *         Benefit Category includes Form 864
 *
 */

@CompileStatic
@Component
class StatesInUSPovertyGuidelinesInputSourceRule implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'StatesInUSPovertyGuidelinesInputSourceRule'


    private static String STATELIST_INPUTTYPE_SOURCE = "stateListDropdown";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    private QuestionnaireService questionnaireService


    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {


        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        InputSourceType stateListInputSourceType = questionnaireService.getInputSourceType(STATELIST_INPUTTYPE_SOURCE, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage)
        //EV-3331 Fix Country to US only if Package contains Form 864
        Boolean isIncludedInForm864 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I864)

        if (isIncludedInForm864) {
            // Filter States for Poverty Guidelines
            List<State> filterStates = [State.AMERICAN_SAMOA,
                                        State.FEDERATED_STATES_OF_MICRONESIA,
                                        State.MARSHALL_ISLANDS,
                                        State.PALAU,
                                        State.ARMED_FORCES_AFRICA,
                                        State.ARMED_FORCES_AMERICAS,
                                        State.ARMED_FORCES_CANADA,
                                        State.ARMED_FORCES_EUROPE,
                                        State.ARMED_FORCES_MIDDLE_EAST,
                                        State.ARMED_FORCES_PACIFIC]

            List usStateValue = stateListInputSourceType.getValues().findAll { stateVal ->
                return !(stateVal.value in filterStates*.displayName)
            }

            List<InputSourceType.ValueMap> values = usStateValue
            InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values)
            return inputSourceType
        } else {

            return stateListInputSourceType
        }


    }
}
