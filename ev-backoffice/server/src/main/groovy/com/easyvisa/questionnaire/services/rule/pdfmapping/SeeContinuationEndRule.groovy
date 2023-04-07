package com.easyvisa.questionnaire.services.rule.pdfmapping

import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATION
import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATIONS

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.rule.BasePdfFieldMappingRule
import com.easyvisa.questionnaire.answering.rule.PdfMappingEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This rule will print 'See continuation sheet(s)' in additional part of each form, if a continuation sheet(s) is expected
 *
 */
@Component
class SeeContinuationEndRule extends BasePdfFieldMappingRule {

    private static String RULE_NAME = "SeeContinuationEndPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] paramValues = splitParamsSections(params)
        String[] expressions = splitParamsValue(getParam(paramValues, 0))

        String sameContinuationId = getParam(paramValues, 1)
        Boolean same = checkSame(sameContinuationId, pdfMappingEvaluationContext)

        String counterId = getParam(paramValues, 2)
        Integer counter = getCounter(pdfMappingEvaluationContext, counterId)

        PdfFieldDetail exist = pdfMappingEvaluationContext.autoFields.find { it.questionId == SEE_CONTINUATION }
        Integer maxIndex = pdfFieldDetail.answerValueObjectList.max { it.index }.index
        if (maxIndex == null) {
            maxIndex = 0
        }
        if ((pdfFieldDetail.fieldMappingDetail.formFieldCount < maxIndex + counter + 1)
                && !pdfFieldDetail.fieldMappingDetail.fieldExpressions.find {it == 'auto'}) {
            if (exist) {
                if ((!same && !counterId) || (same && exist.questionName != sameContinuationId)
                        || (counterId && ((maxIndex + counter + 1) > pdfFieldDetail.fieldMappingDetail.fieldExpressions.size()))) {
                    exist.answerValueObjectList[0].value = SEE_CONTINUATIONS
                }
            } else {
                ContinuationSheetHeaderInfo headerInfo = pdfMappingEvaluationContext.continuationSheetHeaderInfo
                addAutoField(expressions[0], getContinuationText(pdfFieldDetail, maxIndex + counter), pdfMappingEvaluationContext, pdfFieldDetail.questionId)
                addAutoField(expressions[1], headerInfo.lastName, pdfMappingEvaluationContext)
                addAutoField(expressions[2], headerInfo.firstName, pdfMappingEvaluationContext)
                addAutoField(expressions[3], headerInfo.middleName, pdfMappingEvaluationContext)
                addAutoField(expressions[4], headerInfo.alienNumber, pdfMappingEvaluationContext)
            }
        }
    }

    private int getCounter(PdfMappingEvaluationContext pdfMappingEvaluationContext, String counterId) {
        PdfFieldDetail counterDetail = pdfMappingEvaluationContext.getPdfFieldDetail(counterId)
        if (counterDetail && counterDetail.answerValueObjectList.min { it.index }.index  == 0) {
            return counterDetail.answerValueObjectList.max { it.index }.index + 1
        }
        0
    }

    private boolean checkSame(String sameContinuationId, PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        if (sameContinuationId) {
            PdfFieldDetail detail = pdfMappingEvaluationContext.getPdfFieldDetail(sameContinuationId)
            if (detail) {
                Integer maxIndex = detail.answerValueObjectList.max { it.index }.index
                return maxIndex != null && detail.fieldMappingDetail.formFieldCount < maxIndex + 1
            }
        }
        Boolean.FALSE
    }

    private void addAutoField(String expression, String value, PdfMappingEvaluationContext pdfMappingEvaluationContext, String name = null) {
        List<AnswerValueObject> answers = [new AnswerValueObject(new Answer('value': value))]
        FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                'fieldType': 'simple',
                'fieldExpressions': [expression])
        pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail('questionId': SEE_CONTINUATION,
                'questionName': name,
                'answerValueObjectList': answers,
                'fieldMappingDetail': fieldMappingDetail))
    }

}
