package com.easyvisa.questionnaire.services.rule.pdfmapping


import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule checks count of repeat answers and if it meets criteria it will populate extra field
 *
 */
@Component
class CountCheckRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "CountCheckPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] paramsArray = splitParamsSections(params)
        Integer count = getParam(paramsArray, 0) as Integer
        String value = getParam(paramsArray, 1)
        String[] expression = splitParamsValue(getParam(paramsArray, 2))
        if (pdfFieldDetail.answerValueObjectList.size() >= count) {
            addAutoField(expression, value, pdfMappingEvaluationContext)
        }
    }

}
