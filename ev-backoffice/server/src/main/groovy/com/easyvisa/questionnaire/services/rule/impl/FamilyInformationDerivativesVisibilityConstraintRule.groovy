package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Family Information
 * SubSection: SubSec_childrenInformationForBeneficiary
 * Question: (Q_2742) Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
 *
 * Section: Family Information
 * SubSection: SubSec_currentSpouseForBeneficiary
 * Question: (Q_2788) Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
 */

@Component
class FamilyInformationDerivativesVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "FamilyInformationDerivativesVisibilityConstraintRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.value, true);
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {

        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();

        /* if (!this.hasApplicantComesUnderDerivativeBeneficiaryCategory(ruleEvaluationContext)) {
            questionNodeInstance.setVisibility(false);
        } */
    }

    //  This method validates that the selected applicant comes under either ( Family Preference - Initial Application & Fiance Visa)
    private boolean hasApplicantComesUnderDerivativeBeneficiaryCategory(NodeRuleEvaluationContext ruleEvaluationContext) {
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        List<ImmigrationBenefitCategory> derivativeBenefitCategories = ImmigrationBenefitCategory.getDerivativeBeneficiaryAllowedCategories();
        if (derivativeBenefitCategories.contains(immigrationBenefit.category)) {
            return true;
        }
        return false;
    }

}
