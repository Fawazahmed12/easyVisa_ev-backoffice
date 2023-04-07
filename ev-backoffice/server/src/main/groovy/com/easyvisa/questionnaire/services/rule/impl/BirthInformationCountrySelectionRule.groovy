package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.stereotype.Component

/**
 * This rule will check whether the question 'Country of Birth' comes under Inital Family-Prefernces Category
 * If yes, then it will not populate any derived questions irrespective of its answer
 * If no, then will follow the same rule as 'EmployerCountrySelectionRule'
 */
@Component
class BirthInformationCountrySelectionRule extends EmployerCountrySelectionRule {

    private static String RULE_NAME = 'BirthInformatiopnCountrySelectionRule'

    PackageService packageService

    @Override
    protected String getRuleName() {
        return BirthInformationCountrySelectionRule.RULE_NAME;
    }

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        Boolean isInitialFamilyPreferenceCategory = [
                ImmigrationBenefitCategory.F1_A,
                ImmigrationBenefitCategory.F2_A,
                ImmigrationBenefitCategory.F3_A,
                ImmigrationBenefitCategory.F4_A
        ].contains(directBenefitCategory)

        if (isInitialFamilyPreferenceCategory) {
            return new Outcome(RelationshipTypeConstants.NO.value, false)
        }
        return super.evaluateOutcome(nodeRuleEvaluationContext);
    }
}
