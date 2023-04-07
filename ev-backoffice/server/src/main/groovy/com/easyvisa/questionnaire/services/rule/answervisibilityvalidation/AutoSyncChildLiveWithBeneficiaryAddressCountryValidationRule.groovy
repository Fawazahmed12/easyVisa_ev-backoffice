package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.enums.Country
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Family Information
 * SubSection: Children Information
 * Question: (Q_2770) State
 *           (Q_2771) Province/Territory/Prefecture/Parish
 *           (Q_2772) ZIP Code
 *           (Q_2773) Postal Code
 *
 * If the question(Q_2762) "Does [insert this child's name] live with the Beneficiary,  [insert Beneficiary Name]?"
 * is 'Yes', then database needs to copy the data from the 'Current Physical Address' subsection and
 * populate the same corresponding fields into the 'Children Information' IDs in the
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
 * and also relationship value of "In what country does [insert this child's name] live?"
 * is equal to param of this question , then this question is valid even if it is invisible..
 */

@Component
class AutoSyncChildLiveWithBeneficiaryAddressCountryValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AutoSyncChildLiveWithBeneficiaryAddressCountryValidationRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    AutoSyncChildLiveWithBeneficiaryAddressFieldValidationRule autoSyncChildLiveWithBeneficiaryAddressFieldValidationRule;

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
        Integer repeatingIndex = questionNodeInstance.repeatingIndex;
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",");
        String ADDRESS_POPULATE_PATH = "${ruleParams[0]}/${repeatingIndex}";
        String MAILING_ADDRESS_COUNTRY_PATH = ruleParams[1];
        String countryRelationshipType = ruleParams[2];

        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        if (!this.autoSyncChildLiveWithBeneficiaryAddressFieldValidationRule.validateAnswerVisibility(ruleEvaluationContext)) {
            return easyVisaNodeInstance.isVisibility();
        }

        Answer childLiveWithBeneficiaryAddressCountryAnswer = ruleEvaluationContext.findAnswerByPath(MAILING_ADDRESS_COUNTRY_PATH)
        if (!Answer.isValidAnswer(childLiveWithBeneficiaryAddressCountryAnswer)) {
            return questionNodeInstance.isVisibility();
        }

        String selectedCountryValue = childLiveWithBeneficiaryAddressCountryAnswer.getValue()
        String selectedCountryRelationshipType = (selectedCountryValue == Country.UNITED_STATES.getDisplayName()) ? RelationshipTypeConstants.UNITED_STATES.value : RelationshipTypeConstants.OTHER.value
        return selectedCountryRelationshipType == countryRelationshipType;
    }
}
