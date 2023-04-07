package com.easyvisa.questionnaire.services.rule.pdfmapping


import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule prepends user answer with answer from a different question.
 *
 */
@Component
class PrependRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "PrependPrintRule"

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
        String separator = paramsArray[0]
        String questionId = paramsArray[1]
        PdfFieldDetail prependField = pdfMappingEvaluationContext.getPdfFieldDetail(questionId)
        if (prependField) {
            Map<Integer, String> valuesToPrependMap = prependField.answerValueObjectList.collectEntries { [it.index, it.value] }
            pdfFieldDetail.answerValueObjectList.each {
                String prependValue = valuesToPrependMap[it.index]
                if (prependValue) {
                    it.value = prependValue + separator + it.value
                }
            }
        }
    }

}
