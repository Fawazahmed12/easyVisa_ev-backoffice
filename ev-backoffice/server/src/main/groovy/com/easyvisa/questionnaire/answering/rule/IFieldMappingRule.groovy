package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.answering.PdfFieldDetail

interface IFieldMappingRule {

    void register()

    void updatePdfMapping(PdfMappingEvaluationContext pdfMappingEvaluationContext, PdfFieldDetail pdfFieldDetail, String params)
}
