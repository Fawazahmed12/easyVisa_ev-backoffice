package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.Country
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Travel to the United States
 * SubSection: Current status in the United States
 *
 * Do not ask the following questions if user selected 'Without Inspection' option in question
 * 'This person arrived in the United States under which legal status? '
 *
 *  Note: The following questions connceted only with the forms 129F & 130
 *
 * Q_2957: Date authorized stay expired or will expire as shown on I-94 (Arrival/Departure Record) or I-95
 * Q_2958: What is the I-94 record number?
 *
 * */
/**
 * Section: Travel to the United States
 * ApplicantType: Beneficiary
 * SubSection: Current status in the United States
 * Question:
 *  Q_2957: Date authorized stay expired or will expire as shown on I-94 (Arrival/Departure Record) or I-95
 *  Q_2958: What is the I-94 record number?
 *
 * P.Notes: Do not ask the above questions if user selected 'Without Inspection' option to the question
 *          Q_2951: 'This person arrived in the United States under which legal status?'
 *
 *
 *  Note: The above 2 questions connected only with the forms 129F & 130
 *        But triggering question Q_2951 connected with the Forms - 129F, 601A and 765
 *
 * */
@Component
@CompileStatic
class LegalStatusDependentApplicableRule extends BaseComputeRule {

    private static String RULE_NAME = "LegalStatusDependentApplicableRule"

    // (Q_2951) What was your legal status when you last entered the United States?
    private static String LEGAL_STATUS_IN_US_PATH = "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2951"

    private String WITHOUT_INSPECTION = "without_inspection"

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
        Boolean isIncludedInForm129Or130 = packageQuestionnaireService.isQuestionIncludedInAnyForm(ruleEvaluationContext, [PdfForm.I129F, PdfForm.I130])
        if (!isIncludedInForm129Or130) {
            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }

        // Since Form-130 does not have triggering question(Q_2951), we need to allow both Q_2957 and Q_2958 irrespective of the answer
        Boolean isIncludedInForm130 = packageQuestionnaireService.isQuestionIncluded(ruleEvaluationContext, PdfForm.I130)
        if(isIncludedInForm130) {
            return new Outcome(RelationshipTypeConstants.YES.value, false)
        }

        Answer legalStatusInUSAnswer = ruleEvaluationContext.findAnswerByPath(LEGAL_STATUS_IN_US_PATH)
        Boolean canEvaluateLegalStatusDependentApplicableRule = Answer.isValidAnswer(legalStatusInUSAnswer) && legalStatusInUSAnswer.doesMatch(WITHOUT_INSPECTION)
        return new Outcome(RelationshipTypeConstants.YES.value, canEvaluateLegalStatusDependentApplicableRule)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        questionNodeInstance.setVisibility(false)
    }
}
