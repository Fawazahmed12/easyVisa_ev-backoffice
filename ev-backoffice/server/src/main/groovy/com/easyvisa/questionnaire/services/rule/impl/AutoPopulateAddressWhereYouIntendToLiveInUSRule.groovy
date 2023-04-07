package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.enums.Country
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
 * Section: Address History
 * SubSection: Physical Address Abroad
 * ApplicantType: Beneficiary
 * Question: (Q_2074) Is your current Physical Address Abroad the same as the address you listed above in the 'Current Physical Address' section?
 * Notes:  If the answer for the above question is 'yes', then auto populate the answers from
 *         'Address History / Current Physical Address' to 'Address History / Physical Address Abroad'
 */

@Component
class AutoPopulateAddressWhereYouIntendToLiveInUSRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateAddressWhereYouIntendToLiveInUSRule";
    private static String CURRENT_PHYSICAL_ADDRESS_COUNTRY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2002"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (!this.hasAnsweredAutoPopulateQuestion(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.OTHER.value, false);
        }

        if (this.evaluateAutoPopulatePhysicalAddressAbroadRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.evaluateAutoPopulatePhysicalAddressAbroadRule(nodeRuleEvaluationContext)) {
            this.autoPopulateFromPhysicalAddressAbroadFields(nodeRuleEvaluationContext);
        } else {
            this.resetPopulatedPhysicalAddressAbroadFields(nodeRuleEvaluationContext);
        }
    }

    private void resetPopulatedPhysicalAddressAbroadFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        List<String> physicalAddressAbroadFields = fieldsToTransfer.values().toList();
        answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, physicalAddressAbroadFields);
    }


    private void autoPopulateFromPhysicalAddressAbroadFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer);
    }

    private Map<String, String> getFieldTransferMapper(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = new HashMap<>();
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2066';
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2067';
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2005'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2068';
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2006'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2069';
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2070';
        if(this.evaluateEmployerCountrySelectionRule(nodeRuleEvaluationContext)) {
            //State  to  Province/State/Territory/Prefecture/Parish
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2008'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2071';
            //ZIP Code  to  Postal Code
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2010'] = 'Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2072';
        }
        return fieldsToTransfer;
    }


    private Boolean evaluateEmployerCountrySelectionRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Answer countryAnswer = nodeRuleEvaluationContext.findAnswerByPath(CURRENT_PHYSICAL_ADDRESS_COUNTRY_PATH);
        if (!Answer.isValidAnswer(countryAnswer)) {
            return false
        }

        String selectedCountryValue = countryAnswer.getValue()
        if (selectedCountryValue == Country.UNITED_STATES.getDisplayName()) {
            return true
        }
        return false
    }


    // This method only calls if used answered the question (i.e) either yes or no... So here no needs to check the answer as valid or not
    private Boolean evaluateAutoPopulatePhysicalAddressAbroadRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        String isPhysicalAddressAbroadSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (isPhysicalAddressAbroadSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }

    private Boolean hasAnsweredAutoPopulateQuestion(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer);
    }
}
