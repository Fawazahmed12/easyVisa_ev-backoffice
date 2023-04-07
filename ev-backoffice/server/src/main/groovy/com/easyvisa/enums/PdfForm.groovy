package com.easyvisa.enums

/**
 * Holds PDF forms names and PDF filename association.
 */
enum PdfForm {

    I129F('Form_129F'),
    I130('Form_130'),
    I130A('Form_130A'),
    I134('Form_134'),
    I485('Form_485'),
    I601('Form_601'),
    I601A('Form_601A'),
    I693('Form_693'),
    I751('Form_751'),
    I765('Form_765'),
    I824('Form_824'),
    I864('Form_864'),
    N400('Form_400')

    private final String formId

    PdfForm(String formId) {
        this.formId = formId
    }

    String getFormId() {
        return formId
    }

    static PdfForm getByFormId(String formId) {
        PdfForm result = null
        for (it in values()) {
            if (it.getFormId() == formId) {
                result = it
                break
            }
        }
        result
    }
}
