package com.easyvisa.pdffilling.rules.form

import com.easyvisa.utils.PdfUtils
import org.apache.pdfbox.pdmodel.interactive.form.PDField

class SimplePdfFieldSetter implements IPdfFieldSetter {

    @Override
    void setValue(PDField field, String value, String filename, Boolean continuation) {
        PdfUtils.setSingleField(continuation, field)
        PdfUtils.checkAutoScale(filename, value, field)
        //setting text value
        field.setValue(value)

    }

}
