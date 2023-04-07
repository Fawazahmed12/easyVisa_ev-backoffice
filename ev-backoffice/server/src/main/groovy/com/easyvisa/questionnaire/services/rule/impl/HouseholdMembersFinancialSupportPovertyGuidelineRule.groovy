package com.easyvisa.questionnaire.services.rule.impl


import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Section: Family Information
 * SubSection: Household Income
 * Question: Do  you have any siblings, parents, or adult children (living in your same residence) who will be combing their income/assets to assist
 *           in supporting the beneficiary/beneficiaries in this application?
 *
 * Rule: Display the above question, only if petitioner-income doest not meets poverty-guideline.
 *       And also based on the answer(yes/no) of the above question, need to display few questions.
 */

@Component
class HouseholdMembersFinancialSupportPovertyGuidelineRule extends PovertyGuidelineCalculationRule {

    private static String RULE_NAME = "HouseholdMembersFinancialSupportPovertyGuidelineRule"

    private static String HOUSEHOLD_MEMBERS_SUPPORT_FIELD_PATH = "Sec_familyInformation/SubSec_householdIncome/Q_1316"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry


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

    /**
     *
     * Rule: Display this question, only if petitioner-income doest not meets poverty-guideline.
     *       'updateVisibilityOnSuccessfulMatch' get called only if 'evaluateOutcome' returns successfulMatch
     *       Thats why here 'evaluateOutcome' method returns 'successfulMatch' as always true.
     */

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.doesPetitionerIncomeMeetsMinimumProvertyGuideline(nodeRuleEvaluationContext)) {
            EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
            easyVisaNodeInstance.setVisibility(false)
        }
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        //Here no need to do any actions (dont need to send warnings)
    }

}
