package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.PackageService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**

 * Section: Address History
 * SubSection: Previous Physical Address
 * Applicant: Beneficiary
 *
 *
 * If the benfictCategory is REMOVE_CONDTN, then should display this subsection
 * ELse go with WithInNumberOfYearsRule
 */

@Component
class PreviousPhysicalAddressApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "PreviousPhysicalAddressApplicableRule"

    @Autowired
    WithInNumberOfYearsRule withInNumberOfYearsRule

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @Autowired
    private HaveYouResidedAtOtherAddressRule haveYouResidedAtOtherAddressRule


    PackageQuestionnaireService packageQuestionnaireService

    PackageService packageService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

        if (showSubsection(nodeRuleEvaluationContext)){
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }else{
            return new Outcome(RelationshipTypeConstants.NO.value, false)

        }
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return showSubsection(nodeRuleEvaluationContext)

    }

    private boolean showSubsection(NodeRuleEvaluationContext ruleEvaluationContext){

        if(canValidateAddressHistoryFor5YearsData(ruleEvaluationContext)){
            // Does not have Benefit Category Remove Condition
            // go ahead and perform regular check
            return withInNumberOfYearsRule.matchesVisibilityCondition(ruleEvaluationContext)
        }else{
            // It has Form 751, check if fields are filled
            // if fields are filled and Date of PR is less than current address move in date then show section
            // else keep the section hidden
            if (haveYouResidedAtOtherAddressRule.isResidedAtOtherAddressSincePR(ruleEvaluationContext)) {
                return true
            }
            return false

        }

    }



    Boolean canValidateAddressHistoryFor5YearsData(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return packageService
                .getBenefitCategoryFeature(nodeRuleEvaluationContext.packageId)
                .canValidateAddressHistoryFor5YearsData(nodeRuleEvaluationContext)
    }
}
