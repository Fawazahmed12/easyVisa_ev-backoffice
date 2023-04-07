package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.NotificationType
import com.easyvisa.enums.PackageBlockedType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Sec_basisPetitionToRemoveConditionsOnResidence
 * SubSection: SubSec_basisPetitionToRemoveConditionsOnResidence
 * Question: (Q_1701) Is the person you are currently married to, the same person who sponsored you for
 *                    your conditional residence status (conditional 'green card')?
 */

/**
 * ONLY display this question if the user answered 'Spouse' to the question
 * Q_1615: 'What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?’
 * */

@Component
class ConditionalResidenceStatusWarningRule extends BaseComputeRule {

    private static String RULE_NAME = 'ConditionalResidenceStatusWarningRule'
    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private static String EMAIL_SUBJECT = 'Warning on Conditional Residence Status'
    private String conditionalResidenceStatusWarningTemplate = '/email/internal/packageWarningConditionalResidenceStatus'
    private String SPOUSE_RELATIONSHIP = 'Spouse'

    // What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States? Help
    private static String RELATIONSHIP_TO_PETITIONER_FIELD_PATH = "Sec_introQuestionsForBeneficiary/SubSec_introQuestionsForBeneficiary/Q_1615"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    AsyncService asyncService
    AlertService alertService
    EvMailService evMailService
    PackageService packageService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return new Outcome(RelationshipTypeConstants.YES.value, true)
    }


    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        if (!evaluateConditionalResidenceStatusVisibility(ruleEvaluationContext)) {
            QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
            questionNodeInstance.setVisibility(false);
        }
    }

    /**
     * ONLY display this question if the user answered 'Spouse' to the question
     * 'What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?’
     * */
    private Boolean evaluateConditionalResidenceStatusVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        Answer relationshipAnswer = ruleEvaluationContext.findAnswerByPath(RELATIONSHIP_TO_PETITIONER_FIELD_PATH)
        return Answer.isValidAnswer(relationshipAnswer) && (relationshipAnswer.getValue()==SPOUSE_RELATIONSHIP)
    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        if (this.evaluateConditionalResidenceStatusWarningRule(answerContext)) {
            asyncService.runAsync( {
                createPackageWarning(answerContext)
            }, "Send Package [${answerContext.packageId}] warning (ConditionalResidenceStatusWarningRule) for Answer(s) ${answerContext.answerList*.id} and previous Answer [${previousAnswer?.id}] of Applicant [${answerContext.applicantId}]")
            this.changePackageStatusToBlocked(answerContext)
        }
    }

    private Boolean evaluateConditionalResidenceStatusWarningRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer conditionalResidenceStatusAnswer = questionNodeInstance.getAnswer()
        if (!Answer.isValidAnswer(conditionalResidenceStatusAnswer)) {
            return false
        }

        String conditionalResidenceStatusAnswerValue = EasyVisaNode.normalizeAnswer(conditionalResidenceStatusAnswer.getValue())
        return (conditionalResidenceStatusAnswerValue == RelationshipTypeConstants.NO.value)
    }

    /**
     *
     * Template placeholders
     * [Insert Client Name]
     */
    private void createPackageWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String attorneyWarningMessage = constructWarningMessage(packageObj.client)
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                attorneyWarningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }


    private String constructWarningMessage(Applicant applicant) {
        return alertService.renderTemplate(conditionalResidenceStatusWarningTemplate, [(CLIENTNAME_PLACEHOLDER):applicant.getName()])
    }

//    /**
//     *
//     * Template placeholders
//     * [Insert Client Name]
//     */
//    private void sendEmailToAttorney(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
//        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
//        String attorneyWarningMessage = constructWarningMessage(packageObj.client)
//        String repEmail = packageObj.attorney.officeEmail ?: packageObj.attorney.profile.email
//        EmailDto emailDto = evMailService.buildEasyVisaEmailDto(repEmail, EMAIL_SUBJECT, attorneyWarningMessage)
//        evMailService.sendEmail(emailDto)
//    }

    private void changePackageStatusToBlocked(NodeRuleEvaluationContext nodeRuleEvaluationContext){
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        packageService.movePackageToBlockedNotification(aPackage, NotificationType.NOT_SUPPORTED_IMMIGRATION_PROCESS,
                PackageBlockedType.NOT_SUPPORTED_IMMIGRATION)
    }

}
