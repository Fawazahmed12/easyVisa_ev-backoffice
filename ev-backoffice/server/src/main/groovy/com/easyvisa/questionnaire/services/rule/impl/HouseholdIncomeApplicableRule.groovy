package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *   Apply this rule to the 'SubSection' called 'Household Income' under 'Section':'Family Information'.
 *
 *   Display this subsection only if the following conditions gets success,
 *   a. Petitioner-income does not meets poverty-guideline value
 *   b. User answerd 'Yes' to the question 'Do you have any siblings, parents, or adult children living in your same principal residence who will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?'.
 *      in the subsection called 'Household Size/Dependents' .
 */

@Component
class HouseholdIncomeApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "HouseholdIncomeApplicableRule"
    private static String HOUSEHOLD_MEMBERS_SUPPORT_FIELD_PATH = "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageService packageService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateHouseHoldMembersSupport(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, true)
    }


    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.evaluateHouseholdIncomeApplicableRule(nodeRuleEvaluationContext);
    }

    /**
     *
     * Rule: Display this subsection, only if petitioner-income doest not meets poverty-guideline.
     *       And also the following question has 'Yes' value
     *       Question: 'Do you have any siblings, parents, or adult children living in your same principal residence who will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?'
     */
    private Boolean evaluateHouseholdIncomeApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return !this.doesPetitionerIncomeMeetsMinimumProvertyGuideline(nodeRuleEvaluationContext) && this.evaluateHouseHoldMembersSupport(nodeRuleEvaluationContext);
    }


    private Boolean evaluateHouseHoldMembersSupport(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<Answer> householdSupportAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), HOUSEHOLD_MEMBERS_SUPPORT_FIELD_PATH)
        if (householdSupportAnswerList.isEmpty()) {
            return false
        }

        Answer householdSupportAnswer = householdSupportAnswerList[0]
        String householdSupportAnswerValue = EasyVisaNode.normalizeAnswer(householdSupportAnswer.getValue())
        if (householdSupportAnswerValue == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }


    boolean doesPetitionerIncomeMeetsMinimumProvertyGuideline(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        PovertyThresholdCalculation povertyThresholdCalculation = new PovertyThresholdCalculation()
        if (povertyThresholdCalculation.doesPetitionerIncomeMeetsMinimumProvertyGuideline(packageService, nodeRuleEvaluationContext)) {
            return true
        }
        return false
    }

}
