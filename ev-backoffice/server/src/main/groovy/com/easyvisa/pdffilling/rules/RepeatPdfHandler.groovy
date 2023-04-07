package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext
import com.easyvisa.pdffilling.PdfFieldHandler
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm

class RepeatPdfHandler extends AbstractPdfFieldHandler {

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        PdfFieldDetail fieldDetail = pdfFieldContext.pdfFieldDetail
        List<PdfFieldDetail> contFields = []
        //getting fields for current repetition, based on continuation sheet id
        String continuationSheetEasyVisaId = fieldDetail.fieldMappingDetail.continuationSheetEasyVisaId
        pdfFieldContext.pdfFieldDetailList.collect() {
            if (continuationSheetEasyVisaId && it.fieldMappingDetail.continuationSheetEasyVisaId == continuationSheetEasyVisaId) {
                contFields << it
            }
        }
        if (!contFields) {
            contFields << fieldDetail
        }
        String customHandler = fieldDetail.fieldMappingDetail.continuationSheetRule
        if (customHandler && pdfFieldContext.continuation) {
            PdfFieldHandler handler =  (PdfFieldHandler)this.getClass().classLoader.loadClass(customHandler)?.newInstance()
            handler.populateField(new PdfFieldContext('pdfFieldDetailList': contFields, 'acroForm': pdfFieldContext.acroForm,
                    'pdfFieldDetail': fieldDetail, 'filename': pdfFieldContext.filename, 'continuation': pdfFieldContext.continuation))
        } else {
            contFields.each {
                Boolean continuation = pdfFieldContext.continuation
                setValues(getPdfFields(it, continuation), getAnswersList(it, continuation, pdfFieldContext.continuationFileNumber),
                        pdfFieldContext.filename, pdfFieldContext.acroForm, continuation)
            }
        }
    }

    @Override
    protected List<String> setValues(List<String> pdfFields, List<String> values, String filename, PDAcroForm acroForm, Boolean continuation) {
        values.eachWithIndex { String value, int i ->
            setValue(pdfFields[i], value, filename, acroForm, continuation)
        }
    }
}
