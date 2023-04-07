package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.services.RuleEvaluator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Legal Status in U.S.
 * SubSection: Legal Status in U.S. and Government ID Numbers
 * Question: Q_6099: Have you ever had an Alien Registration Number (A-Number)?
 * Notes:  For immigration category K-1, the above question ONLY gets asked if the user answered
 *         'Lawful Permanent Resident' to the below question
 *         Q_109: 'What is your Legal Status in the United States?'
 *
 *          This question connected with the Forms - 129F, 130, 134, 751, 864
 *          But this question has P.Notes for below 2 forms, which constraint its visibility
 *          P.Notes (134): This question ONLY gets asked if the user answered 'Lawful Permanent Resident' to the question 'What is your Legal Status in the United States?'
 *          P.Notes (751): This question ONLY gets asked if response to this question "What is the Legal Status (in the United States) of your Sponsoring Petitioner?" was 'Lawful Permanent Resident/Green Card holder'
 */

@CompileStatic
@Component
class FianceVisaAlienNumberVisibilityConstraintRule extends BaseComputeRule {

    private static String RULE_NAME = "FianceVisaAlienNumberVisibilityConstraintRule"

    private static String LEGAL_STATUS_IN_US_PATH = "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109"
    private static String FIANCEVISA_LEGAL_STATUS_VALUE = "lawful_permanent_resident"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer legalStatusInUSAnswer = questionNodeInstance.answer
        String relationshipType = Answer.isValidAnswer(legalStatusInUSAnswer) ? legalStatusInUSAnswer.getValue() : RuleEvaluator.DEFAULT_PATH
        Boolean isIncludedInForm134orForm751 = packageQuestionnaireService.isQuestionIncludedInAnyForm(nodeRuleEvaluationContext, [PdfForm.I134, PdfForm.I751])
        return new Outcome(relationshipType, isIncludedInForm134orForm751)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (!this.evaluateLegalStatusApplicableRule(nodeRuleEvaluationContext)) {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
            questionNodeInstance.setVisibility(false)
        }
    }

    private Boolean evaluateLegalStatusApplicableRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer legalStatusInUSAnswer = nodeRuleEvaluationContext.findAnswerByPath(LEGAL_STATUS_IN_US_PATH)
        return Answer.isValidAnswer(legalStatusInUSAnswer) && legalStatusInUSAnswer.doesMatch(FIANCEVISA_LEGAL_STATUS_VALUE)
    }
}
