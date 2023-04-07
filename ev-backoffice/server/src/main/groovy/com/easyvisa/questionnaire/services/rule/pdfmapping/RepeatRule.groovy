package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule repeats the same value as many times as expressions in the list, for repeating the same value for
 * continuation sheet population
 */
@Component
class RepeatRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "RepeatPrintRule"

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
        Integer repeats = getParam(paramsArray, 1) as Integer
        if (pdfMappingEvaluationContext.getPdfFieldDetail(questionId)) {
            List<AnswerValueObject> answers = pdfFieldDetail.answerValueObjectList
            AnswerValueObject answer = answers[0]
            answer.index = 0
            (1..repeats).each {
                AnswerValueObject dupe = answer.copy()
                answer.index = it
                answers << dupe
            }
        }
    }

}
