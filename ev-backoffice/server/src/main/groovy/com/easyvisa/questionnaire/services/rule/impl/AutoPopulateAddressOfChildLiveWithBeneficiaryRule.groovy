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
 * Section: Family Information
 * SubSection: Children Information
 * ApplicantType: Beneficiary
 * Question: (Q_2762) Does [insert this child's name] live with the Beneficiary,  [insert Beneficiary Name]?
 * Notes:  If the answer for the above question is 'yes', then auto populate the answers from
 *         'Address History / Current Physical Address' to 'Family Information / Children Information'
 */

@Component
class AutoPopulateAddressOfChildLiveWithBeneficiaryRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateAddressOfChildLiveWithBeneficiaryRule";
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

        if (this.evaluateAutoPopulateAddressOfChildLiveWithBeneficiaryRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.evaluateAutoPopulateAddressOfChildLiveWithBeneficiaryRule(nodeRuleEvaluationContext)) {
            this.autoPopulateAddressOfChildLiveWithBeneficiaryFields(nodeRuleEvaluationContext);
        } else {
            this.resetPopulatedAddressOfChildLiveWithBeneficiaryFields(nodeRuleEvaluationContext);
        }
    }

    private void resetPopulatedAddressOfChildLiveWithBeneficiaryFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Integer targetRepeatingIndex = questionNodeInstance.repeatingIndex;
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext, targetRepeatingIndex);
        List<String> addressOfChildLiveWithBeneficiaryFields = fieldsToTransfer.values().toList();
        answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, addressOfChildLiveWithBeneficiaryFields);
    }


    private void autoPopulateAddressOfChildLiveWithBeneficiaryFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Integer targetRepeatingIndex = questionNodeInstance.repeatingIndex;
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext, targetRepeatingIndex);
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer);
    }

    private Map<String, String> getFieldTransferMapper(NodeRuleEvaluationContext nodeRuleEvaluationContext, Integer targetRepeatingIndex) {
        Map<String, String> fieldsToTransfer = new HashMap<>();
        fieldsToTransfer[CURRENT_PHYSICAL_ADDRESS_COUNTRY_PATH] = 'Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/' + targetRepeatingIndex; ;
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/${targetRepeatingIndex}";
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/${targetRepeatingIndex}";
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2005'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2767/${targetRepeatingIndex}";
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2006'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2768/${targetRepeatingIndex}";
        fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/${targetRepeatingIndex}";
        if (this.evaluateEmployerCountrySelectionRule(nodeRuleEvaluationContext)) {
            //State  to  State
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2008'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2770/${targetRepeatingIndex}";
            //ZIP Code  to  ZIP Code
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2010'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2772/${targetRepeatingIndex}";
        } else {
            //Province/State/Territory/Prefecture/Parish  to  Province/State/Territory/Prefecture/Parish
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2009'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2771/${targetRepeatingIndex}";
            //Postal Code  to  Postal Code
            fieldsToTransfer['Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2011'] = "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2773/${targetRepeatingIndex}";
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
    private Boolean evaluateAutoPopulateAddressOfChildLiveWithBeneficiaryRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        String isAddressOfChildLiveWithBeneficiarySameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (isAddressOfChildLiveWithBeneficiarySameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
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
