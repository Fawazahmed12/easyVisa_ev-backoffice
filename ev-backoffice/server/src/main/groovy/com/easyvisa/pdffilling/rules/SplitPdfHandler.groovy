package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext

class SplitPdfHandler extends AbstractPdfFieldHandler {

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        List<String>  pdfFields = getPdfFields(pdfFieldContext)
        List<String> answers = getSplitValues(pdfFieldContext, pdfFields.size())

        setValues(pdfFields, answers, pdfFieldContext.filename, pdfFieldContext.acroForm)
    }

    private List<String> getSplitValues(PdfFieldContext pdfFieldContext, int fieldsSize) {
        List<String> answers = getAnswersList(pdfFieldContext)
        int qSize = fieldsSize - 1
        if (answers.size() > 1) {
            throwException("Split type should have only one value to set. Field names: [${pdfFieldContext.pdfFieldDetail.fieldMappingDetail.fieldExpressions}]. Values [${answers}]")
        }
        def splitList = []
        answers.get(0).reverse().each { splitList[qSize--] = it }
        splitList
    }

}
