package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule adds value of simple fields to more expressions (PDF fields)
 */
@Component
class DupeRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "DupePrintRule"

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
        String questionId = getParam(paramsArray, 0)
        String[] valuesToCheck = splitParamsValue(getParam(paramsArray, 1))
        String[] expressions = splitParamsValue(getParam(paramsArray, 2))
        PdfFieldDetail pdfFieldToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionId)
        if (pdfFieldToCheck) {
            AnswerValueObject exist = pdfFieldToCheck.answerValueObjectList.find {valuesToCheck.contains(it.value)}
            if (exist) {
                pdfFieldDetail.fieldMappingDetail.fieldExpressions.addAll(expressions)
            }
        }
    }

}
