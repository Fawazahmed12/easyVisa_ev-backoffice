package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.PackageQuestionnaireService
import com.easyvisa.enums.Country
import com.easyvisa.enums.PdfForm
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.util.CurrentMailingAddressRuleUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Address History
 * SubSection: Current Mailing Address
 * Question: (Q_69/Q_2015/Q_6038) Is your current mailing address the same as your current physical address?
 * Notes:  If the answer for the above question is 'yes', then auto populate the answers from
 *         'Address History / Current Physical Address' to 'Address History / Current Mailing Address'
 *
 *         EV-3331 - Hide this question (Q_69) and set it to false, for form 864,
 *         IF Current Physical Address country is NOT US OR state is NOT in the list of Poverty Guidelines
 *         Show the mailing address subsection
 *         ELSE
 *         Show the question and proceed
 */

@Component
class AutoPopulateCurrentMailingAddressRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateCurrentMailingAddressRule"


    private static String MAILING_SAME_AS_PHYSICAL_ADDRESS = "Sec_addressHistory/SubSec_currentMailingAddress/Q_69"

    @Autowired
    RuleComponentRegistry ruleComponentRegistry


    CurrentMailingAddressRuleUtil currentMailingAddressRuleUtil

    AnswerService answerService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this)
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        // Check if its Form 864 and also current physical address Country and state.
        Boolean showQuestion = currentMailingAddressRuleUtil.showQuestionForPovertyGuideline(nodeRuleEvaluationContext)

        if (!showQuestion) {

            // Current Physical Address Does not conform to Poverty Guidelines
            // Hide this question and set it to no
            // User need to fill in Current Mailing Address Components

            return new Outcome(RelationshipTypeConstants.NO.value, true)
        }

        // Q_69 is shown but not answered
        if (!this.hasAnsweredAutoPopulateQuestion(nodeRuleEvaluationContext)) {

            return new Outcome(RelationshipTypeConstants.OTHER.value, true)
        }

        // Q_69 is answered as Yes
        if (this.evaluateAutoPopulateCurrentMailingAddressRule(nodeRuleEvaluationContext)) {

            return new Outcome(RelationshipTypeConstants.YES.value, true)
        }

        // Q_69 is answered as No

        return new Outcome(RelationshipTypeConstants.NO.value, true)
    }

    @Override
    String determineAnswer(NodeRuleEvaluationContext ruleEvaluationContext, Answer answer, Outcome outcome) {
        Boolean showQuestion = currentMailingAddressRuleUtil.showQuestionForPovertyGuideline(ruleEvaluationContext)
        if (!showQuestion) {
            return RelationshipTypeConstants.NO.value
        } else {
            return answer?.value
        }
    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.evaluateAutoPopulateCurrentMailingAddressRule(nodeRuleEvaluationContext)) {

            this.autoPopulateFromCurrentPhysicalAddressFields(nodeRuleEvaluationContext);
        } else {

            this.resetPopulatedCurrentPhysicalAddressFields(nodeRuleEvaluationContext);
        }
    }

    private void resetPopulatedCurrentPhysicalAddressFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        List<String> currentMailingAddressFields = fieldsToTransfer.values().toList();
        answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, currentMailingAddressFields);
    }


    private void autoPopulateFromCurrentPhysicalAddressFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer);
    }

    private Map<String, String> getFieldTransferMapper(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = questionNodeInstance.getRuleParam();
        Map<String, String> fieldsToTransfer = new HashMap<>();
        if (ruleParam == ApplicantType.Petitioner.name()) {
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_70';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_71';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_72';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_73';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_74';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_75';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_76';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_49'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_77';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_78';
            fieldsToTransfer['Sec_addressHistory/SubSec_currentPhysicalAddress/Q_51'] = 'Sec_addressHistory/SubSec_currentMailingAddress/Q_79';
        } else if (ruleParam == ApplicantType.Beneficiary.name()) {
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2002'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2016';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2001'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2017';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2018';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2019';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2005'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2020';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2006'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2021';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2022';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2008'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2023';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2009'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2024';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2010'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2025';
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2011'] = 'Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2026';
        }
        return fieldsToTransfer;
    }


    // This method only calls if used answered the question (i.e) either yes or no... So here no needs to check the answer as valid or not
    Boolean evaluateAutoPopulateCurrentMailingAddressRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }

    Boolean hasAnsweredAutoPopulateQuestion(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer()
        return Answer.isValidAnswer(answer);
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()

        if (!currentMailingAddressRuleUtil.showQuestionForPovertyGuideline(ruleEvaluationContext)) {

            questionNodeInstance.setVisibility(false)
        } else {

            questionNodeInstance.setVisibility(true)
        }
    }
}
