package com.easyvisa.pdffilling.rules

import com.easyvisa.pdffilling.AbstractPdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldContext
import com.easyvisa.questionnaire.answering.PdfFieldDetail

import java.text.MessageFormat

class CountryStateContinuationSheetFieldHandler extends AbstractPdfFieldHandler {

    private static final String Q_66 = 'Q_66'
    private static final String Q_67 = 'Q_67'
    private static final String STATE_TEXT = "Residence {0} - State: {1}, Country: United States\n"
    private static final String COUNTRY_TEXT = "Residence {0} - State: N/A, Country: {1}\n"

    @Override
    void populateField(PdfFieldContext pdfFieldContext) {
        setResidence(pdfFieldContext)
        setHandlerHeader(pdfFieldContext, [Q_66, Q_67])
    }

    private void setResidence(PdfFieldContext pdfFieldContext) {
        PdfFieldDetail country = getPdfFieldDetail(pdfFieldContext, Q_67)
        PdfFieldDetail state = getPdfFieldDetail(pdfFieldContext, Q_66)
        String result = combineValue(country, state)
        Boolean continuation = pdfFieldContext.continuation
        setValues(getPdfFields(country ? country : state, continuation), [result], pdfFieldContext.filename, pdfFieldContext.acroForm, pdfFieldContext.continuation)
    }

    private String combineValue(PdfFieldDetail country, PdfFieldDetail state) {
        Integer formCount = getFormCount(country, state)
        StringBuilder result = StringBuilder.newInstance()
        Integer current = formatLine(result, state, formCount + 1, formCount, STATE_TEXT)
        formatLine(result, country, current, formCount, COUNTRY_TEXT)
        result.toString()
    }

    private PdfFieldDetail getPdfFieldDetail(PdfFieldContext pdfFieldContext, String questionId) {
        pdfFieldContext.pdfFieldDetailList.find {
            it.questionId == questionId
        }
    }

    Integer formatLine(StringBuilder builder, PdfFieldDetail detail, Integer current, Integer formCount, String linePattern) {
        Integer iteration = current
        if (detail) {
            detail.answerValueObjectList.each {
                if (it.index + 1 > formCount) {
                    builder << MessageFormat.format(linePattern, iteration++, it.printValue)
                }
            }
        }
        iteration
    }

    Integer getFormCount(PdfFieldDetail country, PdfFieldDetail state) {
        if (country) {
            return country.fieldMappingDetail.formFieldCount
        }
        state.fieldMappingDetail.formFieldCount
    }
}
