package com.easyvisa.questionnaire.services.rule.visibility

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.Country
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.IVisibilityComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 * Notes: This rule will SHOW or Hide Explain US Domicile Requirement based on Physical Address Country
 *
 */

@Component
class ExplainUsDomicileVisibilityRule implements IVisibilityComputeRule {

    private static String RULE_NAME = "ExplainUsDomicileVisibilityRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    private static String CURRENT_ADDRESS_COUNTRY_PATH = "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42"
    private static String CURRENT_ADDRESS_DOMICILE_PATH = "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_53"

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {

        // If its Form 864, and physical address country or Domicile  is not US, then show else hide
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()

        if (packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I864)) {
            Answer currentAddressCountry = ruleEvaluationContext.findAnswerByPath(CURRENT_ADDRESS_COUNTRY_PATH)
            Answer domicileCountry = ruleEvaluationContext.findAnswerByPath(CURRENT_ADDRESS_DOMICILE_PATH)

            if ((currentAddressCountry?.value != Country.UNITED_STATES.displayName) ||
                    (domicileCountry?.value != Country.UNITED_STATES.displayName)) {
                easyVisaNodeInstance.setVisibility(true)
            } else {
                easyVisaNodeInstance.setVisibility(false)
            }
        }

    }
}
