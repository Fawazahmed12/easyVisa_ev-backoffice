package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.Profile
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail

import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATION
import static com.easyvisa.utils.PdfUtils.SEE_CONTINUATIONS

class BasePdfFieldMappingRule implements IFieldMappingRule {

    @Override
    void register() {
    }

    @Override
    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params) {
    }

    protected String getParam(String[] paramArray, Integer index) {
        if (paramArray && paramArray.size() >= index + 1 && paramArray[index]) {
            return paramArray[index]
        }
        ''
    }

    protected String[] splitParamsSections(String params) {
        if (!params) {
            return []
        }
        params.split(';')
    }

    protected String[] splitParamsValue(String section) {
        section.split(',')
    }

    protected String[] splitQuestionIdValue(String param) {
        param.split('#')
    }

    protected void addAutoField(String[] expression, String value, PdfMappingEvaluationContext pdfMappingEvaluationContext, String questionId = null) {
        List<AnswerValueObject> answers = getAnswerList(value)
        FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                'fieldType': 'simple',
                'fieldExpressions': expression)
        pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail(
                'questionId': questionId,
                'answerValueObjectList': answers,
                'fieldMappingDetail': fieldMappingDetail))
    }

    protected void addAutoFieldAdaptive(FieldMappingDetail detail, String[] expressions, List<AnswerValueObject> answers,
                                      PdfMappingEvaluationContext pdfMappingEvaluationContext) {
        FieldMappingDetail fieldMappingDetail = new FieldMappingDetail(
                'fieldType': detail.fieldType,
                'formFieldCount': detail.formFieldCount,
                'fieldExpressions': expressions.collect { it },
                'continuationSheetEasyVisaId': detail.continuationSheetEasyVisaId,
                'continuationSheetId': detail.continuationSheetId,
                'continuationSheetName': detail.continuationSheetName)
        pdfMappingEvaluationContext.addPdfField(new PdfFieldDetail('answerValueObjectList': answers,
                'fieldMappingDetail': fieldMappingDetail))
    }

    private List<AnswerValueObject> getAnswerList(String value) {
        [getAnswerValueObject(value)]
    }

    protected AnswerValueObject getAnswerValueObject(String value, Integer index = null) {
        new AnswerValueObject(new Answer(value: value, index: index))
    }

    protected String getContinuationText(PdfFieldDetail pdfFieldDetail, Integer maxIndex, Integer extraMaxIndex = -1) {
        Integer expressionsSize = pdfFieldDetail.fieldMappingDetail.fieldExpressions.size()
        if (((maxIndex + 1) > expressionsSize || (extraMaxIndex + 1) > expressionsSize)
                && !pdfFieldDetail.fieldMappingDetail.continuationSheetRule) {
            return SEE_CONTINUATIONS
        }
        SEE_CONTINUATION
    }

    protected Boolean isPetitionerNotMarriedToPrincipleBeneficiary(PdfMappingEvaluationContext pdfMappingContext) {
        String petitionerToBeneficiaryQuestionId = 'Q_27'
        String currentMaritalStatusQuestionId = 'Q_1204'
        PdfFieldDetail relationship = pdfMappingContext.getPdfFieldDetail(petitionerToBeneficiaryQuestionId)
        PdfFieldDetail maritalStatus = pdfMappingContext.getPdfFieldDetail(currentMaritalStatusQuestionId)
        if (maritalStatus?.answerValueObjectList?.find { it.value == 'married' }
                && !relationship?.answerValueObjectList?.find { it.value == 'spouse' }) {
            return  Boolean.TRUE
        }
        Boolean.FALSE
    }

    protected Integer getBeneficiariesCount(PdfMappingEvaluationContext pdfMappingContext) {
        pdfMappingContext.aPackage.benefits.size()
    }

}
