package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance

class FormQuestionEvaluationContext {

    ImmigrationBenefitCategory immigrationBenefitCategory
    PdfForm pdfForm
    EasyVisaNodeInstance easyVisaNodeInstance

    FormQuestionEvaluationContext(ImmigrationBenefitCategory immigrationBenefitCategory,
                                  PdfForm pdfForm, EasyVisaNodeInstance easyVisaNodeInstance) {
        this.immigrationBenefitCategory = immigrationBenefitCategory
        this.pdfForm = pdfForm
        this.easyVisaNodeInstance = easyVisaNodeInstance
    }

    String getQuestionnaireVersion() {
        return this.easyVisaNodeInstance.questVersion
    }

    String getEasyVisaId() {
        return this.easyVisaNodeInstance.id
    }

    String getFormId() {
        return this.pdfForm.formId
    }
}
