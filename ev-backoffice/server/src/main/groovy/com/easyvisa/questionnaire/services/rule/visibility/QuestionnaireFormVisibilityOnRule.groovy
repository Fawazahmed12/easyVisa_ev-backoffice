package com.easyvisa.questionnaire.services.rule.visibility

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class QuestionnaireFormVisibilityOnRule extends BasePdfFormVisibilityComputeRule {

    private static String RULE_NAME = "QuestionnaireFormVisibilityOnRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        Boolean visibility = evaluateQuestionnaireFormVisibilityOnRule(ruleEvaluationContext)
        easyVisaNodeInstance.setVisibility(visibility)
    }

    protected Boolean evaluateQuestionnaireFormVisibilityOnRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<PdfForm> pdfFormList = this.getPdfForm(ruleEvaluationContext)
        return packageQuestionnaireService.isQuestionIncludedInAnyForm(ruleEvaluationContext, pdfFormList)
    }
}
