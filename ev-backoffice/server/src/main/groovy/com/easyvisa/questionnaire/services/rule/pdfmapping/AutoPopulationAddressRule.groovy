package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule check if auto-populate triggering question value is 'yes'
 * Then in Address Where You Intend to Live in U.S. or Physical Address Abroad subsections
 * populates the word ‘SAME’ only into the 'Street Number and Name' field code and the remaining filed codes for the
 * respective address subsections are BLANK when the form prints out.
 */
@Component
class AutoPopulationAddressRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "AutoPopulationAddressRule"

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
        String[] paramArray = splitParamsSections(params)
        String questionId = paramArray[0]
        String value = (paramArray.size() == 2) ? paramArray[1] : ''
        PdfFieldDetail autoPopulateTriggeringField = pdfMappingEvaluationContext.getPdfFieldDetail(questionId)

        if (autoPopulateTriggeringField && autoPopulateTriggeringField.answerValueObjectList[0].value == 'yes') {
            pdfFieldDetail.answerValueObjectList[0].value = value
        }
    }
}
