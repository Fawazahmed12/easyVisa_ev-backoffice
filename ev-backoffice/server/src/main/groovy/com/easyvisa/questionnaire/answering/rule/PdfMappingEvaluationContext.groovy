package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.Package
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.answering.PdfFieldDetail

class PdfMappingEvaluationContext {
    Package aPackage
    Long applicantId
    String formId
    ContinuationSheetHeaderInfo continuationSheetHeaderInfo
    List<PdfFieldDetail> autoFields = []
    private List<PdfFieldDetail> pdfFieldDetailList

    PdfMappingEvaluationContext(Package aPackage, Long applicantId, String formId,
                                ContinuationSheetHeaderInfo continuationSheetHeaderInfo,
                                List<PdfFieldDetail> PdfFieldDetailList) {
        this.aPackage = aPackage
        this.applicantId = applicantId
        this.formId = formId
        this.continuationSheetHeaderInfo = continuationSheetHeaderInfo
        this.pdfFieldDetailList = PdfFieldDetailList
    }

    String getPackageId() {
        return aPackage.id
    }

    void addPdfField(PdfFieldDetail pdfFieldDetail) {
        autoFields.add(pdfFieldDetail)
    }

    PdfFieldDetail getPdfFieldDetail(String questionId, String[] values = null, Integer index = null) {
        pdfFieldDetailList.find {
            (!values && it.questionId == questionId) ||
                    (values && it.answerValueObjectList.find { (index != null && index == it.index && values.contains(it.value)) || (index == null && values.contains(it.value))})
        }
    }

    PdfFieldDetail getPdfFieldDetail(String[] questionIds) {
        pdfFieldDetailList.find { questionIds.contains(it.questionId) }
    }

    List<PdfFieldDetail> getAllPdfFieldDetails(String[] questionIds) {
        pdfFieldDetailList.findAll { questionIds.contains(it.questionId) }
    }

    PdfFieldDetail getAutoPdfFieldDetail(String[] questionIds) {
        autoFields.find { questionIds.contains(it.questionId) }
    }

    String getAnswerValue(String questionId) {
        def detail = getPdfFieldDetail(questionId)
        detail?.answerValueObjectList?.first()?.value
    }

}
