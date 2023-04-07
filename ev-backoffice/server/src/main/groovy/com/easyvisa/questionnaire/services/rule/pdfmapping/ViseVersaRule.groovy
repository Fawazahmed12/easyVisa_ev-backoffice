package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule changes index of answers if third part question has required value.
 */
@Component
@CompileStatic
class ViseVersaRule extends BasePdfFieldMappingRule {

    private final static String RULE_NAME = 'ViseVersaPrintRule'

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
        String questionId = getParam(paramsArray, 0)
        Integer index = getParam(paramsArray, 1) as Integer
        index--
        String[] values = splitParamsValue(getParam(paramsArray, 2))
        String extraQuestionId = getParam(paramsArray, 3)
        Integer extraIndex = getParam(paramsArray, 4) ? getParam(paramsArray, 4) as Integer : 0
        extraIndex--
        String[] extraValues = splitParamsValue(getParam(paramsArray, 5))
        PdfFieldDetail pdfFieldToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionId)
        Boolean exist = Boolean.FALSE
        if (pdfFieldToCheck) {
            exist = checkValueExist(pdfFieldToCheck, index, values)
        }
        PdfFieldDetail extraPdfFieldToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(extraQuestionId)
        if (extraPdfFieldToCheck) {
            exist = exist && checkValueExist(extraPdfFieldToCheck, extraIndex, extraValues)
        }
        if (exist) {
            AnswerValueObject first = pdfFieldDetail.answerValueObjectList.find { it.index == 0 }
            AnswerValueObject second = pdfFieldDetail.answerValueObjectList.find { it.index == 1 }
            if (first) {
                first.index = 1
            }
            if (second) {
                second.index = 0
            }
        }
    }

    private Boolean checkValueExist(PdfFieldDetail pdfFieldToCheck, Integer index, String[] values) {
        pdfFieldToCheck.answerValueObjectList.find { it.index == index && values.contains(it.value) } != null
    }

}
