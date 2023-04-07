package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * This rule is applicable to the following question
 * This question appears ONLY if selected immigraion-catgeory is EAD.
 *
 * Section: Personal Information
 * SubSection: Personal Information
 * ApplicantType: Beneficiary
 * Question: (Q_2408) Do you want the SSA to issue you a Social Security card?
 *
 *
 * ---------------------------------------------------------
 */

@Component
class EmploymentAuthorizationVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "EmploymentAuthorizationVisibilityConstraintRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return new Outcome(RelationshipTypeConstants.YES.value, true)
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category != ImmigrationBenefitCategory.EAD) {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
            questionNodeInstance.setVisibility(false);
        }
    }
}
