package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.utils.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule calculates number values in a particular iteration and sets then in appropriate fields.
 *
 */
@Component
class SumEachRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "SumEachPrintRule"

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
        String[] expression = splitParamsValue(getParam(paramArray, 0))
        String[] minusQuestions = splitParamsValue(getParam(paramArray, 1))
        List<PdfFieldDetail> minusPdfFieldDetails = pdfMappingEvaluationContext.getAllPdfFieldDetails(minusQuestions)
        List<AnswerValueObject> answers = []
        pdfFieldDetail.answerValueObjectList.each {
            Integer index = it.index
            Long sum = it.value.replaceAll(',', '') as Long
            minusPdfFieldDetails.each {
                String value = it.answerValueObjectList.find { it.index == index }?.value
                if (value) {
                    sum -= value.replaceAll(',', '') as Long
                }
            }
            answers << getAnswerValueObject(NumberUtils.formatUSNumber(sum), index)
        }
        addAutoFieldAdaptive(pdfFieldDetail.fieldMappingDetail, expression, answers, pdfMappingEvaluationContext)
    }

}
