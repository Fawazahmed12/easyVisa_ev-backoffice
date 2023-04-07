package com.easyvisa.questionnaire.services.rule.visibility

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.IVisibilityComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Notes: This rule will SHOW either question or repeatingQuestionGroup's visibility,
 *       Only if it's beneficit category matches with any of the ruleParam values
 *
 * ruleParam: Comma Seperated easyVisaId of benefit categories
 */

@Component
class BenefitCategoryVisibilityOnRule implements IVisibilityComputeRule {

    private static String RULE_NAME = "BenefitCategoryVisibilityOnRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (evaluateBenefitCategoryVisibilityOnRule(ruleEvaluationContext)) {
            EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
            easyVisaNodeInstance.setVisibility(false)
        }
    }

    private Boolean evaluateBenefitCategoryVisibilityOnRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        ImmigrationBenefitCategory[] ruleParamBenefitCategories = easyVisaNodeInstance.getVisibilityRuleParam()
                .split(",")
                .collect { String inputBenefitCategory ->
                    return ImmigrationBenefitCategory.getImmigrationBenefitCategoryByEasyVisaId(inputBenefitCategory)
                }
        ImmigrationBenefit immigrationBenefit = getBenefitCategory(ruleEvaluationContext)
        return !ruleParamBenefitCategories.contains(immigrationBenefit.category)
    }

    private ImmigrationBenefit getBenefitCategory(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        return aPackage.getImmigrationBenefitByApplicant(applicant)
    }
}
