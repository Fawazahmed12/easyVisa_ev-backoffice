package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATION
import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATIONS

/**
 * This rule will print 'See continuation sheet(s)' in places, where user explanation is expected, but not in additional
 * info at the end of the form
 *
 */
@Component
class SeeContinuationInsideRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "SeeContinuationInsidePrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] options = splitParamsSections(params)
        List<AnswerValueObject> answerValueObjectList = pdfFieldDetail.getAnswerValueObjectList()
        Integer maxIndex = answerValueObjectList.max { it.index }.index
        if (options[0]) {
            addExpressions(pdfMappingEvaluationContext, options[0], getParam(options, 2), pdfFieldDetail, maxIndex)
        } else {
            addInside(options, pdfMappingEvaluationContext, maxIndex, pdfFieldDetail)
        }
    }

    private void addExpressions(PdfMappingEvaluationContext pdfMappingEvaluationContext, String expressions,
                                String questionsToCheck, PdfFieldDetail pdfFieldDetail, Integer maxIndex) {
        String[] expressionParams = splitParamsValue(expressions)
        String valueToSet = getContinuationText(questionsToCheck, pdfMappingEvaluationContext, maxIndex, pdfFieldDetail)
        List<AnswerValueObject> answerValueObjectList = pdfFieldDetail.getAnswerValueObjectList()
        expressionParams.each {
            FieldMappingDetail mappingDetail = pdfFieldDetail.fieldMappingDetail
            mappingDetail.fieldExpressions.addAll(0, it)
            answerValueObjectList.each {
                if (it.index != null) {
                    it.index++
                } else {
                    it.index = 1
                }
            }
            answerValueObjectList.addAll(0, new AnswerValueObject(new Answer('value': valueToSet, 'index': 0)))
            if ('repeat' == mappingDetail.fieldType) {
                mappingDetail.formFieldCount++
            }
        }
    }

    private void addInside(String[] options, PdfMappingEvaluationContext pdfMappingEvaluationContext, Integer maxIndex,
                           PdfFieldDetail pdfFieldDetail) {
        Integer paramIndex = options[1].toInteger()
        String extraFieldToCheck = getParam(options, 3)
        PdfFieldDetail extraField = pdfMappingEvaluationContext.getPdfFieldDetail(extraFieldToCheck)
        Integer extraMaxIndex = 0
        if (extraField) {
            extraMaxIndex = extraField.answerValueObjectList.max { it.index }.index
        }

        if (maxIndex > paramIndex || extraMaxIndex > paramIndex) {
            List<AnswerValueObject> answerValueObjectList = pdfFieldDetail.getAnswerValueObjectList()
            answerValueObjectList.each {
                if (it.index >= paramIndex) {
                    it.index += 1
                }
            }
            answerValueObjectList.addAll(paramIndex, new AnswerValueObject(
                    new Answer('value': getContinuationText(pdfFieldDetail, maxIndex + 1, extraMaxIndex + 1), 'index': paramIndex)))
        }
    }

    private String getContinuationText(String questionsToCheck, PdfMappingEvaluationContext pdfMappingEvaluationContext,
                                       Integer maxIndex, PdfFieldDetail pdfFieldDetail) {
        String[] questionIds = splitParamsValue(questionsToCheck)
        String valueToSet = SEE_CONTINUATION
        Boolean exist = Boolean.FALSE
        if (pdfMappingEvaluationContext.getPdfFieldDetail(questionIds)) {
            exist = Boolean.TRUE
        }
        if (exist || (maxIndex != null && ((maxIndex + 1) > pdfFieldDetail.fieldMappingDetail.fieldExpressions.size()))) {
            valueToSet = SEE_CONTINUATIONS
        }
        valueToSet
    }
}
