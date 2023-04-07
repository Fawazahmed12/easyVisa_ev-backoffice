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
 * Section: Intro Questions
 * SubSection: Sponsorship Relationship
 * Question: How is the Principle Beneficiary related to you?
 * Notes:  This rule will generate the radio-button options (Fiancé or Spouse) for the above question only if the
 *         selected immigration category is K-1
 */
@CompileStatic
@Component
class SponsorshipRelationshipInputSourceRule implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'SponsorshipRelationshipInputSourceRule'

    @Autowired
    private QuestionnaireService questionnaireService

    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this)
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance() as QuestionNodeInstance
        InputSourceType inputSourceType = questionnaireService.getInputSourceType(questionNodeInstance.inputTypeSource, questionNodeInstance.questVersion, questionNodeInstance.displayTextLanguage)
        Boolean isIncludedInForm129Or134 = packageQuestionnaireService.isQuestionIncludedInAnyForm(ruleEvaluationContext, [PdfForm.I129F, PdfForm.I134])
        if (isIncludedInForm129Or134) {
            List<String> fianceK1K3CategoryRelationshipValues = ['fiance', 'spouse']
            return filterInputSourceTypeValues(fianceK1K3CategoryRelationshipValues, inputSourceType)
        }

        Boolean isIncludedInForm864 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I864)
        if (isIncludedInForm864) {
            List<String> form864RelationshipValues = ['spouse', 'parent', 'sibling', 'child', 'substitutesponsor']
            return filterInputSourceTypeValues(form864RelationshipValues, inputSourceType)
        }

        Boolean isIncludedInForm130 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I130)
        if (isIncludedInForm130) {
            List<String> form130RelationshipValues = ['spouse', 'parent', 'sibling', 'child']
            return filterInputSourceTypeValues(form130RelationshipValues, inputSourceType)
        }
        return inputSourceType
    }

    protected InputSourceType filterInputSourceTypeValues(List<String> inputSourceValues, InputSourceType inputSourceType) {
        List<InputSourceType.ValueMap> filteredInputSourceValues = inputSourceType.getValues().findAll { inputSourceValues.contains(it.value) }
        InputSourceType fianceInputSourceType = new InputSourceType(RULE_NAME, filteredInputSourceValues)
        return fianceInputSourceType
    }
}
