package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Physical Address Abroad
 * ApplicantType: Benficiary
 * Question: (Q_2078) What is the secondary address description?
 *           (Q_2079) Apartment/Suite/Floor
 *
 * If the question(Q_2074) "Is your current Physical Address Abroad the same as the address you listed above in the 'Current Physical Address' section?"
 * is 'Yes', then database needs to copy the data from the 'Current Physical Address' subsection and
 * populate the same corresponding fields into the 'Physical Address Abroad' IDs in the
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
 * and also value of "Does your address have a secondary description (i.e. apartment, suite, or floor)?"
 * is true , then this question is valid even if it is invisible..
 */

@Component
class AutoSyncAddressDescriptionValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AutoSyncAddressDescriptionValidationRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    AutoSyncAddressFieldValidationRule autoSyncAddressFieldValidationRule;

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
        String SECONDARY_ADDRESS_DESCRIPTION_PATH = ruleParams[1];

        EasyVisaNodeInstance easyVisaNodeInstance = ruleEvaluationContext.getEasyVisaNodeInstance()
        if (!this.autoSyncAddressFieldValidationRule.validateAnswerVisibility(ruleEvaluationContext)) {
            return easyVisaNodeInstance.isVisibility();
        }

        Answer secondaryAddressDescriptionAnswer = ruleEvaluationContext.findAnswerByPath(SECONDARY_ADDRESS_DESCRIPTION_PATH)
        if (!Answer.isValidAnswer(secondaryAddressDescriptionAnswer)) {
            return easyVisaNodeInstance.isVisibility();
        }

        String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(secondaryAddressDescriptionAnswer.getValue())
        if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return easyVisaNodeInstance.isVisibility();
    }
}
