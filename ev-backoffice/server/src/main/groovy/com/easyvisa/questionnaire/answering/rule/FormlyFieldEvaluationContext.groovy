package com.easyvisa.questionnaire.answering.rule


import com.easyvisa.questionnaire.dto.IFieldGroup

class FormlyFieldEvaluationContext {
    String displayOrderChildren
    IFieldGroup parentFieldGroup

    FormlyFieldEvaluationContext(String displayOrderChildren, IFieldGroup parentFieldGroup) {
        this.displayOrderChildren = displayOrderChildren
        this.parentFieldGroup = parentFieldGroup
    }
}
