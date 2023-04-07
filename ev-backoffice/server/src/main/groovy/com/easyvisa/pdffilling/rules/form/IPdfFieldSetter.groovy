package com.easyvisa.pdffilling.rules.form

import org.apache.pdfbox.pdmodel.interactive.form.PDField

interface IPdfFieldSetter {

    void setValue(PDField field, String value, String filename, Boolean continuation)

}