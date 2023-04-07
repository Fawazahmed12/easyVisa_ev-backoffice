package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfMappingRuleInfo
import com.easyvisa.questionnaire.answering.rule.IFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PdfMappingRuleEvaluator {

    @Autowired
    PdfRuleComponentRegistry pdfRuleComponentRegistry

    void evaluateMappingRules(List<PdfFieldDetail> pdfFieldDetailList,
                              PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        pdfFieldDetailList.each {
            PdfMappingRuleInfo pdfMappingRuleInfo = it.getPdfMappingRuleInfo()
            if (pdfMappingRuleInfo != null) {
                String[] ruleClassNames = splitData(pdfMappingRuleInfo.getRuleClassName())
                String[] ruleParams = splitData(pdfMappingRuleInfo.getRuleParam())
                ruleClassNames.eachWithIndex { String ruleClassName, int i ->
                    IFieldMappingRule fieldMappingRule = pdfRuleComponentRegistry.getFieldMappingRule(ruleClassName)
                    String param = null
                    if (ruleParams && ruleParams.length >= i + 1) {
                        param = ruleParams[i]
                    }
                    fieldMappingRule.updatePdfMapping(pdfMappingEvaluationContext, it, param)
                }
            }
        }
        if (pdfMappingEvaluationContext.autoFields) {
            pdfFieldDetailList.addAll(pdfMappingEvaluationContext.autoFields)
        }
    }

    private String[] splitData(String data) {
        if (data) {
            return data.split("\\|")
        }
        data
    }

}
