package com.easyvisa.pdffilling

import com.easyvisa.questionnaire.answering.PdfFieldDetail
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm

class PdfFieldContext {

    List<PdfFieldDetail> pdfFieldDetailList
    PDAcroForm acroForm
    PdfFieldDetail pdfFieldDetail
    String filename
    Boolean continuation
    Integer continuationFileNumber

}
