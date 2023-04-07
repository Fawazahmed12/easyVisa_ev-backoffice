package com.easyvisa.pdffilling.rules.form

import com.easyvisa.utils.PdfUtils
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField

class DropdownPdfFieldSetter implements IPdfFieldSetter {

    @Override
    void setValue(PDField field, String value, String filename, Boolean continuation) {
        PDComboBox comboBox = (PDComboBox) field
        PdfUtils.setNewValueToComboBox(value, comboBox, field, filename)
        PdfUtils.checkAutoScale(filename, value, field)
        comboBox.setValue(value)
    }


}
