package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule allows to populate user answers on a form if they are passed answer validation for population.
 */
@Component
class CheckRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "CheckPrintRule"

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
        String[] values = splitParamsValue(getParam(paramsArray, 0))
        String questionIdToCheck = getParam(paramsArray, 1)
        String[] expressions = splitParamsValue(getParam(paramsArray, 2))
        String skipIndexCheckString = getParam(paramsArray, 3)
        Boolean skipIndexCheck = skipIndexCheckString ? skipIndexCheckString as Boolean : false
        if (questionIdToCheck) {
            thirdPartyCheck(pdfMappingEvaluationContext, questionIdToCheck, pdfFieldDetail, values, expressions,
                    skipIndexCheck)
        } else {
            currentCheck(pdfFieldDetail, values)
        }
    }

    private void thirdPartyCheck(PdfMappingEvaluationContext pdfMappingEvaluationContext, String questionIdToCheck,
                                 PdfFieldDetail pdfFieldDetail, String[] values, String[] expressions,
                                 Boolean skipIndexCheck) {
        PdfFieldDetail pdfToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionIdToCheck)
        pdfFieldDetail.answerValueObjectList.each {
            Integer index = it.index
            AnswerValueObject exist = null
            if (pdfToCheck) {
                exist = pdfToCheck.answerValueObjectList.find {
                    String value = it.value
                    ((skipIndexCheck && values.find { it.equalsIgnoreCase(value) })
                            || (!skipIndexCheck && it.index == index && values.find { it.equalsIgnoreCase(value) }) )
                }
            }
            if (exist) {
                pdfFieldDetail.fieldMappingDetail.fieldExpressions = expressions
            }
        }
    }

    private List<AnswerValueObject> currentCheck(PdfFieldDetail pdfFieldDetail, values) {
        pdfFieldDetail.answerValueObjectList.each {
            if (!values.contains(it.value.toLowerCase())) {
                it.value = null
            } else {
                it.value = it.value.capitalize()
            }
        }
    }

}
