package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
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
 * ApplicantType: Beneficiary
 * Section: Family Information
 * SubSection: Marital Status
 * Question: Q_2781: What is your current marital status?
 * Notes:  This rule will remove the radio-button options ('Legally Separated' and 'Marriage Annulled') for the above question only if the
 *         selected immigration category is either EAD(765) or REMOVECOND(751)
 */
@Component
class BeneficiaryMaritalStatusInputSourceRule implements IDynamicInputDatasourceRule {

    private static String RULE_NAME = 'BeneficiaryMaritalStatusInputSourceRule'

    @Autowired
    private QuestionnaireService questionnaireService

    PackageService packageService

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this);
    }


    @Override
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance() as QuestionNodeInstance;
        InputSourceType inputSourceType = questionnaireService.getInputSourceType(questionNodeInstance.inputTypeSource, questionNodeInstance.questVersion,
                questionNodeInstance.displayTextLanguage)

        List<ImmigrationBenefitCategory> ead_removeCondn_Categories = [ImmigrationBenefitCategory.EAD, ImmigrationBenefitCategory.REMOVECOND];
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(ruleEvaluationContext.packageId)
        if (ead_removeCondn_Categories.contains(directBenefitCategory) == false) {
            return inputSourceType
        }

        List<String> maritalStatusRelationshipValues = ['legallyseparated', 'marriage_annulled'];
        List<InputSourceType.ValueMap> maritalStatusInputSourceValues = inputSourceType.getValues().findAll {!maritalStatusRelationshipValues.contains(it.value)}
        InputSourceType maritalStatusInputSourceType = new InputSourceType(RULE_NAME, maritalStatusInputSourceValues);
        return maritalStatusInputSourceType;
    }
}
