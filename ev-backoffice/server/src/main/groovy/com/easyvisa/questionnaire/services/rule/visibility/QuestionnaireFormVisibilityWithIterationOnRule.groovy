package com.easyvisa.questionnaire.services.rule.visibility


import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class QuestionnaireFormVisibilityWithIterationOnRule extends QuestionnaireFormVisibilityOnRule {

    private static String RULE_NAME = "QuestionnaireFormVisibilityWithIterationOnRule"


    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        Boolean visibility = evaluateQuestionnaireFormVisibilityWithIterationOnRule(ruleEvaluationContext)
        easyVisaNodeInstance.setVisibility(visibility)
    }

    Boolean evaluateQuestionnaireFormVisibilityWithIterationOnRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        return evaluateQuestionnaireFormVisibilityOnRule(ruleEvaluationContext) && evaluateQuestionnaireFormVisibilityByIteration(ruleEvaluationContext)
    }

    @Override
    List<PdfForm> getPdfForm(ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] ruleParams = easyVisaNodeInstance.getVisibilityRuleParam().split("\\|")
        String[] ruleParamQuestionnaireForm = ruleParams[1].split(',')
        List<PdfForm> pdfFormList = ruleParamQuestionnaireForm.collect { PdfForm.getByFormId(it) }.toList()
        return pdfFormList
    }

    Boolean evaluateQuestionnaireFormVisibilityByIteration(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Integer repeatingIndex = questionNodeInstance.getRepeatingIndex(); //it is zero based value
        Integer currentIterationCount = ++repeatingIndex;
        Integer iterationCount = getIterationParamValue(ruleEvaluationContext);
        return (currentIterationCount >= iterationCount)
    }

    private Integer getIterationParamValue(ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        String[] ruleParams = easyVisaNodeInstance.getVisibilityRuleParam().split("\\|")
        String iterationValue = ruleParams[0]
        return iterationValue.toInteger()
    }
}

