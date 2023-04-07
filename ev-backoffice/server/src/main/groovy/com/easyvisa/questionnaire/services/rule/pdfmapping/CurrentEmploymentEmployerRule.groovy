package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *  When you get to this Rule, data is already filtered by PdfFieldFilteringService
 *  Move Self-employed to the third index so that
 *    This rule addresses the following scenarios:
 *      1. Employer Name
 * *        Not used if Self-Employed
 * *        change the order/index in case self-employed
 * *
 * *    2.  Occupation
 * *        Based on the index (order of appearance in the list) of self employed,
 * *        we need to change the order - slot self employed occupation into the third position
 * *
 * *
 *
 */
@Component
class CurrentEmploymentEmployerRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = 'CurrentEmploymentEmployerRule'

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
        Integer selfEmployedSlot = getParam(paramsArray, 2) as Integer

        PdfFieldDetail pdfToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionIdToCheck)
        // We need to slot each answer at the correct place
        // Employed but NOT self-employed Employer Name should be the top two entries.

        if (pdfToCheck) {

            List notSelfEmployedIdx = pdfToCheck.answerValueObjectList.findAll { !values.contains(it.value) }*.index
            // Change index of pdfFieldDetail answers (Question in the current rule context) where NOT self-employed

            Integer newIndex = 0
            pdfFieldDetail.answerValueObjectList.eachWithIndex { AnswerValueObject entry, int i ->
                if (notSelfEmployedIdx.contains(entry.index))
                    entry.index = newIndex++
            }

            Integer selfEmployedIdx = pdfToCheck.answerValueObjectList.find { values.contains(it.value) }?.index
            // Slot self employed into the third position - drive this by params
            AnswerValueObject selfEmployedAns = pdfFieldDetail.answerValueObjectList?.find { it.index == selfEmployedIdx }
            selfEmployedAns?.index = selfEmployedSlot
        }
    }

}
