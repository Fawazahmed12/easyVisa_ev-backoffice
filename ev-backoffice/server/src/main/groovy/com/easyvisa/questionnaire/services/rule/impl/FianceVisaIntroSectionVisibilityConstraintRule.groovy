package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Intro Questions
 * SubSection: Previous Immigration (Visa) Petitions You Field for Another Person
 * Question: Q_7: City or town where petition was filed
 *           Q_8: State where petition was filed
 * Notes:  This rule will hide the above 2 questions from RepeatingQuestionGroup if the selected immigration category is K-1
 *
 * Questions in RepeatingQuestion group(RQG_1) connected with Forms 129F, 130 and 134.
 * But in RQG_1, Questions Q_7 and Q_8, are only connected to Form-130 alone,
 * So we should hide these 2 questions, If the selected benefitCategory does not have a Form-130
 */

@Component
class FianceVisaIntroSectionVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "FianceVisaIntroSectionVisibilityConstraintRule"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Boolean isIncludedInForm130 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I130)
        Answer answer = questionNodeInstance.getAnswer()
        return new Outcome(answer.getValue(), !isIncludedInForm130)
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        questionNodeInstance.setVisibility(false)
    }
}
