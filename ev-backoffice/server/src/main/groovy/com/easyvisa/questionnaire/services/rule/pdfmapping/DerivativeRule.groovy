package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.ImmigrationBenefit
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule will print predefined word in the parameter for the given answer/pdf form.
 */
@Component
class DerivativeRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "DerivativePrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        ImmigrationBenefit immigrationBenefit = pdfMappingEvaluationContext.aPackage.getBenefitForApplicantId(pdfMappingEvaluationContext.applicantId);
        if (immigrationBenefit && !immigrationBenefit.direct) {
            pdfFieldDetail.fieldMappingDetail.fieldExpressions = splitParamsValue(params)
        }
    }

}
