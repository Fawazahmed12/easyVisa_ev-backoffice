package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext
import com.easyvisa.questionnaire.answering.PdfFieldDetail

class ChildrenUnder18ContinuationSheetFieldHandler extends AbstractPdfFieldHandler {

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        setChildren(pdfFieldContext)
        setHandlerHeader(pdfFieldContext, [pdfFieldContext.pdfFieldDetail.questionId])
    }

    private void setChildren(PdfFieldContext pdfFieldContext) {
        PdfFieldDetail fieldDetail = pdfFieldContext.pdfFieldDetail
        Integer currentNum = fieldDetail.fieldMappingDetail.formFieldCount + 1
        StringBuilder result = StringBuilder.newInstance()
        Integer start = fieldDetail.fieldMappingDetail.formFieldCount
        Integer end = fieldDetail.answerValueObjectList.size() - 1
        if (start <= end) {
            (start..end).each {
                String value = fieldDetail.answerValueObjectList[it].printValue
                if (result) {
                    result << ', '
                }
                result << "Child ${currentNum++}: ${value} Old"
            }
            Boolean continuation = pdfFieldContext.continuation
            setValues(getPdfFields(fieldDetail, continuation), [result.toString()], pdfFieldContext.filename, pdfFieldContext.acroForm, pdfFieldContext.continuation)
        }
    }

}
