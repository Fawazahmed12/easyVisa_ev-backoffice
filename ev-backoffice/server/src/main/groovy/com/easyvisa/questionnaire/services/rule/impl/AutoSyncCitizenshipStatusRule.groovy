package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Legal Status in U.S.
 * SubSection: Legal Status in U.S. and Government ID Numbers
 * Question: (Q_109) What is your Legal Status in the United States?
 * Options: [United States Citizen, U.S. National, Lawful Permanent Resident/Green Card holder]
 * Notes:  Whenever user change the value for the above question, then need to sync the same value to the petitioner model.
 *          There will no longer be a single Petitioner (at most) per Applicant.
 *          Instead, there will be one for every package where the Applicant acts as the petitioner.
 *          As such, all Petitioner objects in Open packages can be updated.
 *          Those associated with Closed packages will not be changed.
 */

@Component
class AutoSyncCitizenshipStatusRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncCitizenshipStatusRule"

    private static String LPR = 'lawful_permanent_resident'

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'
    private String citizenshipStatusWarningTemplate = '/email/internal/packageWarningCitizenshipStatus'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    ApplicantService applicantService
    PackageQuestionnaireService packageQuestionnaireService
    AlertService alertService
    PackageService packageService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return new Outcome(answer.getValue(), true)
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId)
        Petitioner petitioner = aPackage.getPetitioner()
        CitizenshipStatus citizenshipStatus = this.getCitizenshipStatus(answer)
        petitioner.setCitizenshipStatus(citizenshipStatus)
        applicantService.savePetitioner(petitioner)
        packageQuestionnaireService.updateCitizenshipStatusInOtherOpenPackages(aPackage)
        createPackageWarning(nodeRuleEvaluationContext)
    }


    CitizenshipStatus getCitizenshipStatus(Answer answer) {
        String answerValue = answer.getValue()
        def citizenshipStatusValues = [
                'united_states_citizen'    : CitizenshipStatus.U_S_CITIZEN,
                'lawful_permanent_resident': CitizenshipStatus.LPR,
                'us_national'              : CitizenshipStatus.U_S_NATIONAL,
                'alien'                    : CitizenshipStatus.ALIEN
        ]
        return citizenshipStatusValues[answerValue]
    }

    /**
     *
     * Template placeholders
     * [Insert Client Name]
     */
    private void createPackageWarning(NodeRuleEvaluationContext answerContext) {
        QuestionNodeInstance questionNodeInstance = answerContext.getEasyVisaNodeInstance() as QuestionNodeInstance
        Answer citizenshipStatusAnswer = questionNodeInstance.getAnswer()
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(answerContext.packageId)
        Boolean canTriggerWarning = ImmigrationBenefitCategory.K1K3.equals(directBenefitCategory) &&
                citizenshipStatusAnswer.getValue() == LPR
        if (canTriggerWarning) {
            Package packageObj = Package.get(answerContext.packageId)
            Applicant applicant = packageObj.client
            String attorneyWarningMessage = constructWarningMessage(applicant)
            alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                    attorneyWarningMessage, questionNodeInstance.id, questionNodeInstance.answer)
        }
    }

    private String constructWarningMessage(Applicant applicant) {
        return alertService.renderTemplate(citizenshipStatusWarningTemplate, [(CLIENTNAME_PLACEHOLDER): applicant.getName()])
    }
}
