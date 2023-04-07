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
 * Notes: This rule will HIDE either question or repeatingQuestionGroup's visibility,
 *        Only if it's beneficit category matches with the ruleParam values
 *
 *
 *  ruleParam: Comma Seperated easyVisaId of beneficit categories
 */

@Component
class BenefitCategoryVisibilityOffRule implements IVisibilityComputeRule {

    private static String RULE_NAME = "BenefitCategoryVisibilityOffRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (this.evaluateBenefitCategoryVisibilityOffRule(ruleEvaluationContext)) {
            EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
            easyVisaNodeInstance.setVisibility(false)
        }
    }


    private Boolean evaluateBenefitCategoryVisibilityOffRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        ImmigrationBenefitCategory[] ruleParamBenefitCategories = easyVisaNodeInstance.getVisibilityRuleParam()
                .split(",")
                .collect { String inputBenefitCategory ->
                    return ImmigrationBenefitCategory.getImmigrationBenefitCategoryByEasyVisaId(inputBenefitCategory)
                }
        ImmigrationBenefit immigrationBenefit = getBenefitCategory(ruleEvaluationContext)
        return ruleParamBenefitCategories.contains(immigrationBenefit.category)
    }


    private ImmigrationBenefit getBenefitCategory(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        return aPackage.getImmigrationBenefitByApplicant(applicant)
    }
}
