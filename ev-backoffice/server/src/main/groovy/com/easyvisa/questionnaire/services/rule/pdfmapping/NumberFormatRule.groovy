package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import com.easyvisa.utils.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule formats number to money (adding comma after each three numbers)
 *
 */
@Component
class NumberFormatRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "NumberFormatPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        pdfFieldDetail.answerValueObjectList.each {
            it.value = NumberUtils.formatUSNumber(it.value.replaceAll(',', ''))
        }
    }

}
