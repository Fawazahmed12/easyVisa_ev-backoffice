package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.Country
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Current Mailing Address
 * Question: (Q_76) State
 *           (Q_77) Province/Territory/Prefecture/Parish
 *           (Q_78) ZIP Code
 *           (Q_79) Postal Code
 *
 * If the question(Q_69) "Is your current mailing address the same as your current physical address?"
 * is 'Yes', then database needs to copy the data from the 'Current Physical Address' subsection and
 * populate the same corresponding fields into the 'Current Mailing Address' IDs in the
 * database (for the purpose of populating the USCIS paper form).
 * However, the questions and fields should NOT be displayed to the user in the Questionnaire.
 *
 * Case1: In zombie data removal, we are removing all invisible question answers...
 * Case2: In pdf-expression print we are passing, only visible question answers...
 *
 * The above cases are not always true... In some cases  question is valid even it is not visible..
 * If the question has 'AnswerValidationRule' then need to evaluate the rule, based on that we will validate....
 *
 * SO here this rule will check the question validity.
 * (i.e) if the populate question value is true,
 * and also relationship value of "In what country is this current mailing address?"
 * is equal to param of this question , then this question is valid even if it is invisible..
 */

@Component
class CurrentMailingAddressCountryValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'CurrentMailingAddressCountryValidationRule'

    private static String MAILING_ADDRESS_BENEFICIARY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    CurrentMailingAddressValidationRule currentMailingAddressValidationRule;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerVisibilityValidationRule(RULE_NAME, this);
    }

    @Override
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",");
        String MAILING_ADDRESS_POPULATE_PATH = ruleParams[0];
        String MAILING_ADDRESS_COUNTRY_PATH = ruleParams[1];
        String countryRelationshipType = ruleParams[2];

        if (!this.currentMailingAddressValidationRule.validateAnswerVisibility(ruleEvaluationContext)) {
            return questionNodeInstance.isVisibility();
        }

        // Immigraion-Category EAD is only mapped to the Form-765
        // In Form-765, Current Mailing Address subsection don't have Country question
        if(this.hasForm765BeneficiaryMailingAddressQuestion(ruleEvaluationContext)) {
            return true;
        }


        Answer currentMailingAddresCountryAnswer = ruleEvaluationContext.findAnswerByPath(MAILING_ADDRESS_COUNTRY_PATH)
        if (!Answer.isValidAnswer(currentMailingAddresCountryAnswer)) {
            return questionNodeInstance.isVisibility();
        }

        String selectedCountryValue = currentMailingAddresCountryAnswer.getValue()
        String selectedCountryRelationshipType = (selectedCountryValue == Country.UNITED_STATES.getDisplayName()) ? RelationshipTypeConstants.UNITED_STATES.value : RelationshipTypeConstants.OTHER.value
        return selectedCountryRelationshipType == countryRelationshipType;
    }


    private Boolean hasForm765BeneficiaryMailingAddressQuestion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",");
        String MAILING_ADDRESS_POPULATE_PATH = ruleParams[0];

        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category == ImmigrationBenefitCategory.EAD && MAILING_ADDRESS_POPULATE_PATH == MAILING_ADDRESS_BENEFICIARY_PATH) {
            return true;
        }
        return false;
    }
}
