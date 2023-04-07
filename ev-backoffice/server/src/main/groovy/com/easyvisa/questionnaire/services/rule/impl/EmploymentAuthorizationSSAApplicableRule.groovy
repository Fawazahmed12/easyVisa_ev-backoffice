package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.PackageService
import com.easyvisa.enums.ImmigrationBenefitCategory
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
 *
 * This rule is applicable to the following subsections
 * This subsections's questions  appears only if user answered 'Yes' to the question 'Do you want the SSA to issue you a Social Security card?' in the 'Personal Information' subsection
 * and also selected immigraion-catgeory is EAD.
 *
 * Section: Family Information
 * SubSection: Parent 1
 * ApplicantType: Beneficiary
 *
 * Section: Family Information
 * SubSection: Parent 2
 * ApplicantType: Beneficiary
 *
 * ---------------------------------------------------------
 */

@Component
class EmploymentAuthorizationSSAApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "EmploymentAuthorizationSSAApplicableRule"
    // Do you want the SSA to issue you a Social Security card?
    private static String SSA_ISSUE_FIELD_PATH = "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2408"

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
        if (this.evaluateEmploymentAuthorizationSSAApplicableRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }
        return new Outcome(RelationshipTypeConstants.NO.value, false)
    }

    @Override
    Boolean matchesVisibilityCondition(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Boolean canShowParentSubSection = this.evaluateEmploymentAuthorizationSSAApplicableRule(nodeRuleEvaluationContext);
        return canShowParentSubSection;
    }


    private Boolean evaluateEmploymentAuthorizationSSAApplicableRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        Answer ssaAnswer = ruleEvaluationContext.findAnswerByPath(SSA_ISSUE_FIELD_PATH)
        if (immigrationBenefit.category != ImmigrationBenefitCategory.EAD || !Answer.isValidAnswer(ssaAnswer)) {
            return false
        }
        String legalStatusInUSAnswerValue = EasyVisaNode.normalizeAnswer(ssaAnswer.getValue())
        return (legalStatusInUSAnswerValue == RelationshipTypeConstants.YES.value)
    }
}
