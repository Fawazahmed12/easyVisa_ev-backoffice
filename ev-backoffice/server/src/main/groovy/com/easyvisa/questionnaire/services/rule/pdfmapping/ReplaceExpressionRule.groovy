package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule changes particular expression if check of another question is passed.
 */
@Component
class ReplaceExpressionRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = 'ReplaceExpressionPrintRule'

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail,
                          String params) {
        String[] paramsArray = splitParamsSections(params)
        String questionIdToCheck = getParam(paramsArray, 0)
        String[] values = splitParamsValue(getParam(paramsArray, 1))
        String expression = getParam(paramsArray, 2)
        Integer iterationsToCheck = getParam(paramsArray, 3) as Integer
        PdfFieldDetail pdfToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionIdToCheck)
        if (pdfToCheck) {
            Boolean change = Boolean.FALSE
            (0..<iterationsToCheck).each { i ->
                String value = pdfToCheck.answerValueObjectList.find { it.index == i }?.value
                if (value != null && values.contains(value)) {
                    change = Boolean.TRUE
                } else if (value != null) {
                    change = Boolean.FALSE
                }
            }
            (0..<iterationsToCheck).each { i ->
                if (change && pdfToCheck.answerValueObjectList.find { it.index == i && values.contains(it.value) }) {
                    pdfFieldDetail.fieldMappingDetail.fieldExpressions[i] = expression
                }
            }
        }
    }

}
