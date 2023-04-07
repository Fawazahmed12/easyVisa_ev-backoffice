package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *  When you get to this Rule, data is already filtered by PdfFieldFilteringService
 *  This rule addresses the following scenarios:
 * 1. Employed/Not Self Employed
 * *    If Q_1013 self-employed is not checked, then
 * *        we just go ahead and populate as usual
 * *    Params should contain Q_1013 and required value - yes
 * 2. Employed/Self Employed
 * *    If Q_1013 self employed is checked, then
 * *        Change the fieldExpression to auto so that Employed for this entry does not get checked
 * *        Should we also remove Employer Name here?
 * *
 *
 */
@Component
class CurrentEmploymentEmployedRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = 'CurrentEmploymentEmployedRule'

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
        String expression = getParam(paramsArray, 2)
        Integer iterationsToCheck = pdfFieldDetail?.answerValueObjectList?.size()
        PdfFieldDetail pdfToCheck = pdfMappingEvaluationContext.getPdfFieldDetail(questionIdToCheck)
        if (pdfToCheck) {

            (0..<iterationsToCheck).each { i ->
                String value = pdfToCheck.answerValueObjectList.find { it.index == i }?.value
                // If Q_1013 (self-employed) is yes then set expression to auto so that
                // so that Employed checkbox does not get checked on the pdf for this iteration
                if (value != null && values.contains(value)) {
                    pdfFieldDetail.fieldMappingDetail.fieldExpressions[i] = expression
                }
            }
        }
    }

}
