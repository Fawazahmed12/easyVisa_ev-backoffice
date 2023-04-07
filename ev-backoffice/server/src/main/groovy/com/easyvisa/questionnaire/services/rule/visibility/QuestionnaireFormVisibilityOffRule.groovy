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
class QuestionnaireFormVisibilityOffRule extends BasePdfFormVisibilityComputeRule {

    private static String RULE_NAME = "QuestionnaireFormVisibilityOffRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerVisibilityComputeRules(RULE_NAME, this)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (evaluateQuestionnaireFormVisibilityOffRule(ruleEvaluationContext)) {
            EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
            easyVisaNodeInstance.setVisibility(false)
        }
    }

    private Boolean evaluateQuestionnaireFormVisibilityOffRule(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<PdfForm> pdfFormList = this.getPdfForm(ruleEvaluationContext)
        return packageQuestionnaireService.isQuestionIncludedInAnyForm(ruleEvaluationContext, pdfFormList)
    }
}
