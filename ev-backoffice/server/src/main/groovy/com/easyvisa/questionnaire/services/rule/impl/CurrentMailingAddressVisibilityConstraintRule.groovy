package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.util.CurrentMailingAddressRuleUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Current Mailing Address
 * Question: Q_70, Q_71, Q_72,Q_73,Q_74, Q_75, Q_76, Q_78, Q_79
 * Notes:  EV-3331: If the benefit category has Form 864 then current mailing address has to be in the United States
 *          with state in the list of Poverty Guidelines.
 *          In case Current Physical Address does NOT conform to the above guideline then:
 *              - Hide the Question "Is your current mailing address the same as Physical Address?"
 *              - Show all current mailing address questions
 *              - Hardcode Country to US
 *              - Update State list to include only the ones in the Poverty Guidelines list
 *          In case Current Physical Address does conform to the poverty guidelines
 *              - Show the Question Q_69 "Is your current mailing address the same as Physical Address?"
 *              - Based on the answer to Q_69, show or hide mailing address questions
 *              - Hardcode Country to US
 *              - Update State list to include only the ones in the Poverty Guidelines list
 *
 *          If the benefit category does NOT have Form 864 then -
 *              - Visibility of current mailing address questions will depend ONLY on the answer to Q_69
 *              - Allow any Country and state
 */

@Component
class CurrentMailingAddressVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "CurrentMailingAddressVisibilityConstraintRule"
    private static String MAILING_SAME_AS_PHYSICAL_ADDRESS = "Sec_addressHistory/SubSec_currentMailingAddress/Q_69"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    AutoPopulateCurrentMailingAddressRule autoPopulateCurrentMailingAddressRule

    CurrentMailingAddressRuleUtil currentMailingAddressRuleUtil

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return new Outcome(answer?.value, true)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()

        if (currentMailingAddressRuleUtil.isQuestionVisible(ruleEvaluationContext)) {
            questionNodeInstance.setVisibility(true)
        } else {
            questionNodeInstance.setVisibility(false)
        }
    }
}
