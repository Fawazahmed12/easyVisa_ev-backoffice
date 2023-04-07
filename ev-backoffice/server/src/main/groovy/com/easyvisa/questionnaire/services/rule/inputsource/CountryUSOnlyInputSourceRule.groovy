package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.AnswerService
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.model.EasyVisaNode
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
 * Question: Q_70
 *
 * Notes: EV-3331 - If Benefit Category has Form 864, then Country in Current Mailing Address is fixed to US
 *
 */

@CompileStatic
@Component
class CountryUSOnlyInputSourceRule implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'CountryUSOnlyInputSourceRule'
    private static String US_VALUE = "United States"
    private static String COUNTRYLIST_INPUTTYPE_SOURCE = "countryListDropdown"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    private QuestionnaireService questionnaireService

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this)
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        InputSourceType countryListInputSourceType = questionnaireService.getInputSourceType(COUNTRYLIST_INPUTTYPE_SOURCE, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage)

        //EV-3331 Fix Country to US only if Package contains Form 864
        Boolean isIncludedInForm864 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I864)


        if (isIncludedInForm864) {
            List<InputSourceType.ValueMap> values = []

            def usValue = countryListInputSourceType.getValues().find { countryValueMap ->
                countryValueMap.value == US_VALUE
            }
            // There will always be a value (US) so not checking for existence
            values.add(usValue.clone())
            InputSourceType inputSourceType = new InputSourceType(RULE_NAME, values)

            return inputSourceType

        } else {

            return countryListInputSourceType
        }

    }
}
