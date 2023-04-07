package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext

class SimplePdfHandler extends AbstractPdfFieldHandler {

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        setValues(getPdfFields(pdfFieldContext), getAnswersList(pdfFieldContext), pdfFieldContext.filename,
                pdfFieldContext.acroForm, pdfFieldContext)
    }

}
