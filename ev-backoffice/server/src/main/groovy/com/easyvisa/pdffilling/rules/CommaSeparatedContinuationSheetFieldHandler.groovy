package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext
import com.easyvisa.questionnaire.answering.PdfFieldDetail

class CommaSeparatedContinuationSheetFieldHandler extends AbstractPdfFieldHandler {

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        setCommaSeparatedValues(pdfFieldContext)
        setHandlerHeader(pdfFieldContext, [pdfFieldContext.pdfFieldDetail.questionId])
    }

    private void setCommaSeparatedValues(PdfFieldContext pdfFieldContext) {
        PdfFieldDetail fieldDetail = pdfFieldContext.pdfFieldDetail
        StringBuilder result = StringBuilder.newInstance()
        Integer start = fieldDetail.fieldMappingDetail.formFieldCount
        Integer end = fieldDetail.answerValueObjectList.size() - 1
        if (start <= end) {
            (start..end).each {
                if (result) {
                    result << ', '
                }
                result << fieldDetail.answerValueObjectList[it].printValue
            }
            Boolean continuation = pdfFieldContext.continuation
            setValues(getPdfFields(fieldDetail, continuation), [result.toString()], pdfFieldContext.filename,
                    pdfFieldContext.acroForm, pdfFieldContext.continuation)
        }
    }

}
