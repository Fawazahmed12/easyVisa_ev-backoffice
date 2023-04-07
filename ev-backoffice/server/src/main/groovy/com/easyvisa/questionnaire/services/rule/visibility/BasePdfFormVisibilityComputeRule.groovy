package com.easyvisa.questionnaire.services.rule.visibility

import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.IVisibilityComputeRule

abstract class BasePdfFormVisibilityComputeRule implements IVisibilityComputeRule {

    List<PdfForm> getPdfForm(ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] ruleParamQuestionnaireForm = easyVisaNodeInstance.getVisibilityRuleParam().split(',')
        List<PdfForm> pdfFormList = ruleParamQuestionnaireForm.collect { PdfForm.getByFormId(it) }.toList()
        return pdfFormList
    }
}
