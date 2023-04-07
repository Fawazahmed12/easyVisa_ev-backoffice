package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants

import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import com.easyvisa.questionnaire.services.rule.util.CurrentMailingAddressRuleUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Address History
 * SubSection: Current Mailing Address
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
 * (i.e) if the populate question value is true, then this question is valid even if it is invisible..
 */

@Component
class CurrentMailingAddressValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'CurrentMailingAddressValidationRule'

    private static String MAILING_ADDRESS_BENEFICIARY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015"
    private static String MAILING_ADDRESS_765_BENEFICIARY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_6038"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    CurrentMailingAddressRuleUtil currentMailingAddressRuleUtil

    @PostConstruct
    void register() {

        ruleComponentRegistry.registerAnswerVisibilityValidationRule(RULE_NAME, this)
    }

    @Override
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        // Current Mailing Address questions need to be always visible
        // In the following scenarios:

        Boolean showQuestion = currentMailingAddressRuleUtil.showQuestionForPovertyGuideline(ruleEvaluationContext)

        if(!showQuestion){
            //Question 69 - Mailing address same as current address
            // is hidden, so we need to show all the mailing address questions
            return true
        }
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String MAILING_ADDRESS_POPULATE_PATH = this.getMailingAddressPopulatePath(ruleEvaluationContext)

        Answer currentMailingAddressPopulateAnswer = ruleEvaluationContext.findAnswerByPath(MAILING_ADDRESS_POPULATE_PATH)

        if (!Answer.isValidAnswer(currentMailingAddressPopulateAnswer)) {
            return questionNodeInstance.isVisibility()
        }

        String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(currentMailingAddressPopulateAnswer.getValue())
        if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }

        return questionNodeInstance.isVisibility()
    }


    private String getMailingAddressPopulatePath(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        String[] ruleParams = answerVisibilityValidationRuleParam.split(",")
        String MAILING_ADDRESS_POPULATE_PATH = ruleParams[0]

        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category == ImmigrationBenefitCategory.EAD && MAILING_ADDRESS_POPULATE_PATH == MAILING_ADDRESS_BENEFICIARY_PATH) {
            return MAILING_ADDRESS_765_BENEFICIARY_PATH;
        }
        return MAILING_ADDRESS_POPULATE_PATH;
    }
}
