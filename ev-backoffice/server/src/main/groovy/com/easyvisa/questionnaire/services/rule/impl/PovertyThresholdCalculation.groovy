package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.ImmigrationBenefitAssetValue
import com.easyvisa.PackageService
import com.easyvisa.PovertyGuideline
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import grails.compiler.GrailsCompileStatic

/**
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
 * */

@GrailsCompileStatic
class PovertyThresholdCalculation {

    //PovertyThreshold Filed PathsPovertyGuideline
    // Changed State from current physical address to Current Mailing Address
    private static String SELECTED_STATE_FIELD_PATH = "Sec_addressHistory/SubSec_currentMailingAddress/Q_76"
    private static String HOUSEHOLD_SIZE_FIELD_PATH = "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311"
    private static String MILITARY_STATUS_FIELD_PATH = "Sec_criminalAndCivilHistory/SubSec_currentMilitaryService/Q_1102"

    //PetitionerIncome Filed Paths
    //Sum off assets paths
    private static String PETITIONER_INCOME_FIELD_PATH = "Sec_incomeHistory/SubSec_incomeHistory/Q_132"
    //bankDepositCurrentAccountBalance
    private static String BANKDEPOSITS_FIELD_PATH = "Sec_assets/SubSec_bankDeposits/Q_907/"
    //personalPropertyMarketValue
    private static String PERSONELPROPERTY_FIELD_PATH = "Sec_assets/SubSec_personalProperty/Q_912"
    //realEstateMarketValue
    private static String REALESTATE_MARKETVALUE_FIELD_PATH = "Sec_assets/SubSec_realEstate/Q_927"
    //mortgageBalance
    private static String REALESTATE_MARTGAGEBALANCE_FIELD_PATH = "Sec_assets/SubSec_realEstate/Q_926"
    //totalSumOfYourLifeInsurance
    private static String LIFEINSURANCE_FIELD_PATH = "Sec_assets/SubSec_lifeInsurance/Q_930"
    //financialInstrumentsCurrentValue
    private static String FINANCIAL_INSTRUMENTS_FIELD_PATH = "Sec_assets/SubSec_financialInstruments/Q_940"


    private static double ARMED_FORCES_MULTIPLIER = 1.00
    private static double NON_ARMED_FORCES_MULTIPLIER = 1.25

    boolean doesPetitionerIncomeMeetsMinimumProvertyGuideline(PackageService packageService, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        def povertyThresholdValue = this.findPovertyThresholdValue(nodeRuleEvaluationContext)
        def calculatedIncomeValue = this.findCalculatedIncomeValue(packageService, nodeRuleEvaluationContext)
        if (povertyThresholdValue && calculatedIncomeValue) {
            return (calculatedIncomeValue >= povertyThresholdValue)
        }
        return false
    }

    boolean doesPovertyThresholdHasValidValues(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        def povertyThresholdValue = this.findPovertyThresholdValue(nodeRuleEvaluationContext)
        return (povertyThresholdValue != 0)
    }

    /**
     *  Formula:
     *  ---------
     *  (PovertyGuidelineBaseline + ((HouseHoldSize -1) * AdditionalHouseholdSizeMultiplier))
     *  *
     *  MilitaryStatusIncomeMultiplier
     *  */
    private double findPovertyThresholdValue(NodeRuleEvaluationContext nodeRuleEvaluationContext) {


        Answer selectedStateAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), SELECTED_STATE_FIELD_PATH)
        Answer houseHoldSizeAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), HOUSEHOLD_SIZE_FIELD_PATH)
        Answer militaryStatusAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), MILITARY_STATUS_FIELD_PATH)
        if (!Answer.isValidAnswer(selectedStateAnswer) || !Answer.isValidAnswer(houseHoldSizeAnswer) || !Answer.isValidAnswer(militaryStatusAnswer)) {

            return 0
        }


        State selectedStateValue = State.valueOfDisplayName(selectedStateAnswer.value)
        PovertyGuideline selectedStatePovertyGuideline = PovertyGuideline.findByState(selectedStateValue)
        if (!selectedStatePovertyGuideline) {
            return 0
        }

        def militaryStatusIncomeMultiplier = NON_ARMED_FORCES_MULTIPLIER

        if (militaryStatusAnswer) {
            String militaryStatusValue = EasyVisaNode.normalizeAnswer(militaryStatusAnswer.getValue())
            militaryStatusIncomeMultiplier = (militaryStatusValue == RelationshipTypeConstants.YES.value) ? ARMED_FORCES_MULTIPLIER : NON_ARMED_FORCES_MULTIPLIER
        }

        int houseHoldSize = houseHoldSizeAnswer.getValue() as int
        def povertyThresholdValue = (selectedStatePovertyGuideline.basePrice + ((houseHoldSize - 1) * selectedStatePovertyGuideline.addOnPrice)) * militaryStatusIncomeMultiplier

        return povertyThresholdValue
    }

    //
    /**
     * Formula:
     *  ---------
     *  PetitionerIncome + (SumOfAssets / CalculatedAssetValue)
     *
     *  */
    private double findCalculatedIncomeValue(PackageService packageService, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer petitionerIncomeAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), PETITIONER_INCOME_FIELD_PATH)
        double petitionerIncome = getAnswerValue(petitionerIncomeAnswer)

        double sumOfAssetsValue = this.calculateSumOfPetitionerAssets(nodeRuleEvaluationContext)
        int calculatedAssetValue = ((Integer)getImmigrationBenefitAssetValue(packageService, nodeRuleEvaluationContext))?.intValue()

        double calculatedIncomeValue = petitionerIncome + (sumOfAssetsValue / calculatedAssetValue)
        return calculatedIncomeValue
    }

    def getImmigrationBenefitAssetValue(PackageService packageService, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        ImmigrationBenefitCategory directBenefitCategory = packageService.getDirectBenefitCategory(nodeRuleEvaluationContext.packageId)
        String benefitCategoryId = directBenefitCategory.getEasyVisaId()
        ImmigrationBenefitAssetValue immigrationBenefitAssetValue = ImmigrationBenefitAssetValue.findByEasyVisaId(benefitCategoryId)
        def calculatedAssetValue = immigrationBenefitAssetValue?.assetValue ?: 1
        return calculatedAssetValue
    }


    double calculateSumOfPetitionerAssets(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        double totalBankDeposit = findAndIterateAssetValue(nodeRuleEvaluationContext, BANKDEPOSITS_FIELD_PATH)
        double totalPersonelPropertiesMarketValue = findAndIterateAssetValue(nodeRuleEvaluationContext, PERSONELPROPERTY_FIELD_PATH)
        double totalFinancialInstrumentsValue = findAndIterateAssetValue(nodeRuleEvaluationContext, FINANCIAL_INSTRUMENTS_FIELD_PATH)
        double totalLifeInsuranceValue = findAndIterateAssetValue(nodeRuleEvaluationContext, LIFEINSURANCE_FIELD_PATH)
        //Total RealEstate value is the sum of the difference between its marketVale and its mortgageValue
        double totalRealEstateMarketValue = findAndIterateAssetValue(nodeRuleEvaluationContext, REALESTATE_MARKETVALUE_FIELD_PATH)
        double totalRealEstateMortgageValue = findAndIterateAssetValue(nodeRuleEvaluationContext, REALESTATE_MARTGAGEBALANCE_FIELD_PATH)
        double totalRealEstateValue = (totalRealEstateMarketValue - totalRealEstateMortgageValue)

        double sumOfAssets = totalBankDeposit + totalPersonelPropertiesMarketValue +
                totalRealEstateValue + totalFinancialInstrumentsValue + totalLifeInsuranceValue
        return sumOfAssets
    }


    double findAndIterateAssetValue(NodeRuleEvaluationContext nodeRuleEvaluationContext, String fieldPath) {
        List<Answer> assetAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(),
                nodeRuleEvaluationContext.getApplicantId(), "${fieldPath}%")
        double totalAssetValue = 0
        assetAnswerList.each { answer ->
            totalAssetValue += getAnswerValue(answer)
        }
        return totalAssetValue
    }


    double getAnswerValue(Answer answer) {
        def defaultyAnswerValue = 0
        if (!Answer.isValidAnswer(answer)) {
            return defaultyAnswerValue
        }

        String answerValue = answer.getValue()
        if (answerValue.isNumber()) {
            return answerValue as double
        }
        return defaultyAnswerValue
    }
}
