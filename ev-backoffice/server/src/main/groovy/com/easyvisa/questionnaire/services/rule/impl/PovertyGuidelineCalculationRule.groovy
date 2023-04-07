package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import grails.compiler.GrailsCompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 *
 *
 * This class  contains calculation to determine if a Petitioner's income meets minimum Poverty Guidelines or not.
 * The formula to calculate that is in Excel file: 'Drop Down Lists and Data Tables v19' in
 * worksheet tab: 'Poverty Guideline Calculation'.
 *
 *  For this rule, we need to calculate the following 2 values
 *  1. Poverty Threshold Value
 *  2. Calculated Income Value
 *
 * If the calculated income meets or exceeds minumum poverty level guidelines, then tick box 1a in
 * form 864 and jump to section 'Relationship to Petitioner'.
 *

 'Calculated Income Value'  is based on the following questions
 =========================================

 Required questions:
 ----------------------
 1.  Petitioner Income
 2.  Sum of Assets
 3.  Immigration Benefit Category

 Formula:
 ---------
 PetitionerIncome + (SumOfAssets / CalculatedAssetValue)

 Notes:
 1.  Based on the following question,
 'What is your current individual annual income? (Q_132)'
 we will decide the value of 'PetitionerIncome '.
 2.  Based on the sum of following questions,  we will find the value of 'SumOfAssets '.
 a. 'Current Balance' (Assets/Bank Deposits/Q_907)
 b. 'Market Value' (Assets/Personal Property/Q_912)
 c. Difference between the sum of 'Market Value'(Assets/Real Estate/Q_927)
 and 'Mortgage Balance(s)'(Assets/Real Estate/Q_926)
 d. 'What is the total sum of your life insurance?
 Assets/Life Insurance/Q_930)
 e. 'Current Value' (Assets/Financial Instruments/Q_940)
 3.  Here based on the 'Immigration Benefit category' we can get 'CalculatedAssetValue'
 value from 'Calculated Asset Value (From Immigration Benefit category)'  table.



 'Poverty Threshold'  is based on the following questions
 ========================================

 Required questions:
 ----------------------
 1. Selected State
 2. Household Size
 3. Are you currently serving in the Armed Forces of the United States?

 Formula:
 ---------
 (PovertyGuidelineBaseline + ((HouseHoldSize -1) * AdditionalHouseholdSizeMultiplier))
 *
 MilitaryStatusIncomeMultiplier

 Notes:
 1.  Here based on the following question
 'xxxxxxx'
 we can get 'PovertyGuidelineBaseline' and 'AdditionalHouseholdSizeMultiplier' values
 from 'annual income guideline' table.

 2.  Based on the following question,
 'How many lawful permanent residents whom you are currently obligated to support
 based on your previous submission of Form I-864 as a petitioning, substitute, or
 joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA,
 as a petitioning sponsor?
 (Family Information / Household Size/Dependents / Q_1311)'
 we will decide the value of 'HouseHoldSize'.

 3.  Based on the following question,
 'Are you currently serving in the Armed Forces of the United States? (Q_1101)'
 we will decide the value of 'MilitaryStatusIncomeMultiplier'.
 (If yes, then the value is 1.00,   If  no, then the value is 1.25) (edited)

 -----------------------------------------
 */

@GrailsCompileStatic
@Component
class PovertyGuidelineCalculationRule extends BaseComputeRule {

    private static String RULE_NAME = "PovertyGuidelineCalculationRule"

    private static String OTHER_PERSON_SPONSOR_WARNING_TYPE = "OtherPersonSponsorWarning"
    private static String RELATIVES_INCOME_SUPPORT_WARNING_TYPE = "RelativesIncomeSupportWarning"

    //Warning Question Filed Paths
    private static String OTHER_SPONSOR_FIELD_PATH = "Sec_familyInformation/SubSec_householdSizeDependents/Q_1313"
    private static String RELATIVES_INCOME_SUPPORT_FIELD_PATH = "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314"

    private static String CLIENTNAME_PLACEHOLDER = 'clientName'

    private String otherPersonSponsorWarning = '/email/internal/packageWarningOtherPersonSponsor'
    private String relativesIncomeSupportWarning = '/email/internal/packageWarningRelativesIncomeSupport'


    @Autowired
    RuleComponentRegistry ruleComponentRegistry
    AsyncService asyncService

    PackageService packageService

    AlertService alertService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return new Outcome(RelationshipTypeConstants.YES.value, true)
    }


    boolean doesPetitionerIncomeMeetsMinimumProvertyGuideline(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        PovertyThresholdCalculation povertyThresholdCalculation = new PovertyThresholdCalculation()
        return povertyThresholdCalculation.doesPetitionerIncomeMeetsMinimumProvertyGuideline(packageService, nodeRuleEvaluationContext)
    }


    boolean doesPovertyThresholdHasValidValues(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        PovertyThresholdCalculation povertyThresholdCalculation = new PovertyThresholdCalculation()
        return povertyThresholdCalculation.doesPovertyThresholdHasValidValues(nodeRuleEvaluationContext)
    }


    //This method only gets called, if petitioner-income meets poverty-guideline value..
    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        easyVisaNodeInstance.setVisibility(false)

        if(!this.doesPovertyThresholdHasValidValues(nodeRuleEvaluationContext)) {
            easyVisaNodeInstance.setVisibility(false)
            return
        }

        if (!this.doesPetitionerIncomeMeetsMinimumProvertyGuideline(nodeRuleEvaluationContext)) {
            easyVisaNodeInstance.setVisibility(true)
        }
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.doesPetitionerIncomeMeetsMinimumProvertyGuideline(nodeRuleEvaluationContext)) {
            EasyVisaNodeInstance easyVisaNodeInstance = nodeRuleEvaluationContext.getEasyVisaNodeInstance()
            String ruleArgumentType = easyVisaNodeInstance.getDefinitionNode().getRuleParam()
            switch (ruleArgumentType) {
                case OTHER_PERSON_SPONSOR_WARNING_TYPE:
                    evaluateAnswerAndSendWarning(nodeRuleEvaluationContext, OTHER_SPONSOR_FIELD_PATH, otherPersonSponsorWarning)
                    break

                case RELATIVES_INCOME_SUPPORT_WARNING_TYPE:
                    evaluateAnswerAndSendWarning(nodeRuleEvaluationContext, RELATIVES_INCOME_SUPPORT_FIELD_PATH, relativesIncomeSupportWarning)
            }
        }
    }


    private void evaluateAnswerAndSendWarning(NodeRuleEvaluationContext nodeRuleEvaluationContext, String fieldPath, String warningMessage) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.packageId,
                nodeRuleEvaluationContext.applicantId, fieldPath)
        if (answerList.isEmpty()) {
            return
        }

        Answer answer = answerList[0]
        String answerValue = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (answerValue == RelationshipTypeConstants.YES.value) {
            asyncService.runAsync({
                createPackageWarning(warningMessage, nodeRuleEvaluationContext)
            }, "Send Package [${nodeRuleEvaluationContext.packageId}] warning (PovertyGuidelineCalculationRule) for Answer(s) ${nodeRuleEvaluationContext.answerList*.id} and field path [${fieldPath}] of Applicant [${nodeRuleEvaluationContext.applicantId}]")

        }
    }


    private void createPackageWarning(String templateMessage, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant petitionerApplicant = packageObj.client
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String warningMessage = alertService.renderTemplate(templateMessage, [(CLIENTNAME_PLACEHOLDER):petitionerApplicant.getName()])
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                warningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }
}
