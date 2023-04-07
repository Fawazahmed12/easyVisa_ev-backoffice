package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule shifts population in a form if two different subsections shares the same part on the form
 */
@Component
class ShiftRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "ShiftPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] paramArray = splitParamsSections(params)
        Integer startIndex = getStartIndex(paramArray)
        Integer maxPriorIndex = getMaxIndex(pdfMappingEvaluationContext, paramArray)
        pdfFieldDetail.answerValueObjectList.each {
            it.index = checkNull(it.index, 0)
            if (it.index >= startIndex) {
                it.index += maxPriorIndex + 1
            }
        }
    }

    private Integer getStartIndex(String[] paramArray) {
        String startParam = getParam(paramArray, 2)
        if (startParam) {
            return startParam as Integer
        }
        0
    }

    private Integer getMaxIndex(PdfMappingEvaluationContext pdfMappingEvaluationContext, String[] paramArray) {
        String questionId = getParam(paramArray, 0)
        String value = getParam(paramArray, 1)
        String[] values = []
        if (value) {
            values = splitParamsValue(value)
        }
        PdfFieldDetail priorDetail = pdfMappingEvaluationContext.getPdfFieldDetail(questionId)
        Integer result = -1
        if ((priorDetail && value && priorDetail.answerValueObjectList.find { values.contains(it.value) }) || (priorDetail && !value)) {
            result = priorDetail.answerValueObjectList.max { it.index }.index
            result = checkNull(result, 0)
        }
        checkNull(result, -1)
    }

    private Integer checkNull(Integer result = null, Integer defaultValue) {
        if (result == null) {
            return defaultValue
        }
        result
    }

}
