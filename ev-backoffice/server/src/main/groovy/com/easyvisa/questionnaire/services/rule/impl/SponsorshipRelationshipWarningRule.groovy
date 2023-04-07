package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.PdfForm
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Intro Questions
 * SubSection: Sponsorship Relationship
 * Question: (Q_27) How is the Principle Beneficiary related to you?
 * Notes:  This rule will send the warning message to the Attorney, If user selects 'Spouse' value as relationship answer.
 *            and also the selected immigration category is has a Form: 129F
 *
 * This question connected to the forms 129, 130, 134, 864
 * P.Notes (129F): If this response is selected, the attorney should get a Warning that says:
 *                     "Your client [Petitioner First Name Petitioner Last Name], a Petitioner/Sponsor in a K-1 Fiancé Visa Immigration Benefit category package,
 *                     responded to the question "How is the Principle Beneficiary related to you?" with an answer of "Spouse".
 *                     You may want to contact this client to verify their marital status as well as to whom they are married,
 *                     as you may have to change the Immigration Benefit category for this package.
 */

@CompileStatic
@Component
class SponsorshipRelationshipWarningRule extends BaseComputeRule {

    private static String RULE_NAME = 'SponsorshipRelationshipWarningRule'
    private static String SPOUSE_RELATIONSHIP = 'spouse'

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private String sponsorshipRelationshipWarningTemplate = '/email/internal/packageWarningSponsorshipRelationship'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    AsyncService asyncService
    AlertService alertService
    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer sponsorshipRelationshipAnswer = questionNodeInstance.getAnswer()
        Boolean isIncludedInForm129F = packageQuestionnaireService.isQuestionIncluded(nodeRuleEvaluationContext, PdfForm.I129F)
        Boolean canTriggerWarning = isIncludedInForm129F && sponsorshipRelationshipAnswer.doesMatch(SPOUSE_RELATIONSHIP)
        return new Outcome(sponsorshipRelationshipAnswer.getValue(), canTriggerWarning)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        createPackageWarning(answerContext)
    }


    /**
     *
     * Template placeholders
     * [Insert Client Name]
     */
    private void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = packageObj.client
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String attorneyWarningMessage = constructWarningMessage(applicant)
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                attorneyWarningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }

    private String constructWarningMessage(Applicant applicant) {
        return alertService.renderTemplate(sponsorshipRelationshipWarningTemplate, [(CLIENTNAME_PLACEHOLDER): applicant.getName()])
    }
}
