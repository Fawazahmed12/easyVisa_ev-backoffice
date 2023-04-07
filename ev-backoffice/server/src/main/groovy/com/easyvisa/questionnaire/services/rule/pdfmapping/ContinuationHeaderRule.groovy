package com.easyvisa.questionnaire.services.rule.pdfmapping

import com.easyvisa.utils.PdfUtils
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
 * This rule adds information about continuation sheet header.
 */
@Component
class ContinuationHeaderRule extends BasePdfFieldMappingRule {
    private static String RULE_NAME = "ContinuationHeaderPrintRule"

    @Autowired
    PdfRuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerFieldMappingRules(RULE_NAME, this)
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
        String[] expressions = splitParamsValue(params)
        ContinuationSheetHeaderInfo info = pdfMappingEvaluationContext.continuationSheetHeaderInfo
        FieldMappingDetail detail = pdfFieldDetail.fieldMappingDetail
        addAutoField(expressions[0], info.lastName, pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[1], info.firstName, pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[2], info.middleName, pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[3], info.alienNumber, pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[4], normalize(detail.continuationSheetPage), pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[5], normalize(detail.continuationSheetPart), pdfFieldDetail, pdfMappingEvaluationContext)
        addAutoField(expressions[6], normalize(detail.continuationSheetItem), pdfFieldDetail, pdfMappingEvaluationContext)
    }

    private void addAutoField(String expression, String value, PdfFieldDetail pdfFieldDetail,
                              PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        FieldMappingDetail mappingDetail = pdfFieldDetail.fieldMappingDetail

        Integer maxIndex = pdfFieldDetail.answerValueObjectList.max { it.index }.index
        if (maxIndex == null) {
            maxIndex = 0
        }
        Integer formFieldCount = mappingDetail.formFieldCount

        if ((maxIndex >= formFieldCount) && !pdfFieldDetail.fieldMappingDetail.fieldExpressions.find {it == 'auto'}) {
            List<AnswerValueObject> answers = []
            List<String> expressions = []
            (0..<formFieldCount)?.each {
                answers.add(new AnswerValueObject(new Answer('value': '', 'index': it)))
                expressions.add(PdfUtils.AUTO_ID)
            }
            answers.add(new AnswerValueObject(new Answer('value': value, 'index': formFieldCount)))
            expressions.add(expression)
            FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                    'fieldType': mappingDetail.fieldType,
                    'fieldExpressions': expressions,
                    'formFieldCount': formFieldCount,
                    'continuationSheetEasyVisaId': mappingDetail.continuationSheetEasyVisaId,
                    'continuationSheetId': mappingDetail.continuationSheetId,
                    'continuationSheetName': mappingDetail.continuationSheetName)
            pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail(questionId: PdfUtils.CONTINUATION_HEADER_ID,
                    'answerValueObjectList': answers, 'fieldMappingDetail': fieldMappingDetail))
        }
    }

    private String normalize(String text) {
        text.replaceAll('#', '')
    }
}
