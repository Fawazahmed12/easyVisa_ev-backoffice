package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
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
 * Section: Legal Status in U.S.
 * ApplicantType: Petitioner
 * SubSection: U.S. Citizens
 * Question: (Q_120) How was your Citizenship acquired?
 * Notes:  1. This rule will generate the radio-button options (Another Method) for the above question only if the
 *            selected immigration category has a Form-134
 *         2. The question, Q_120 connected with the Forms - 129F, 130 and 134
 *         3. One of the radio-button option for the above question called 'Another Method' triggers the
 *            visibility of the following question (Q_6032)
 *
 * The question, Q_6032 'Explain how you acquired your citizenship:', which has a below P.Notes
 * P.Notes: If user answered 'Another Method' to the question 'How was your citizenship acquired?',
 *          then put this response into continuation sheet named: '134 Continuation Sheet - Page 2, Part 1, Item 11c (Citizenship or Residency or Status) 2016-11-30'
 * Notes: So bases on the P.Notes, the question (Q_6032) is connected to the Form-134
 */
@CompileStatic
@Component
class CitizenshipAcquiredInputSourceRule implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'CitizenshipAcquiredInputSourceRule'

    @Autowired
    private QuestionnaireService questionnaireService

    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this)
    }


    // We should list the 'Another Method' option to packages only when it has Form-134.
    // If the package does not have Form-134 then remove 'Another Method' option
    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        InputSourceType inputSourceType = questionnaireService.getInputSourceType(questionNodeInstance.inputTypeSource, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage)
        Boolean isIncludedInForm134 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I134)
        if (isIncludedInForm134) {
            return inputSourceType
        }

        List<String> citizenshipAcquiredExcludedValues = ['another_method']
        List<InputSourceType.ValueMap> filteredInputSourceValues = inputSourceType.getValues().findAll { !citizenshipAcquiredExcludedValues.contains(it.value) }
        InputSourceType newInputSourceType = new InputSourceType(RULE_NAME, filteredInputSourceValues)
        return newInputSourceType
    }
}
