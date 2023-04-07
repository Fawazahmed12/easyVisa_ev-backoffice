package com.easyvisa.pdffilling.rules.form

import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField

class CheckboxPdfFieldSetter implements IPdfFieldSetter {

    @Override
    void setValue(PDField field, String value, String filename, Boolean continuation) {
        //setting checkbox value
        //all checkboxes unchecked by default
        //those check it, if it was mentioned
        ((PDCheckBox) field).check()
    }

}
