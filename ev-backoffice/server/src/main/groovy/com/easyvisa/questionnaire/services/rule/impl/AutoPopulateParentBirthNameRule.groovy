package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section: Family Information
 * SubSection: Parent 1
 * Question: (Q_2882) Is this parent's current name the same as their birth name?
 * Notes:  If the user answered Yes, then populated the 'Current Name' for 'Birth Name'
 *
 * Section: Family Information
 * SubSection: Parent 2
 * Question: (Q_2900) Is this parent's current name the same as their birth name?
 * Notes:  If the user answered Yes, then populated the 'Current Name' for 'Birth Name'
 */

@Component
class AutoPopulateParentBirthNameRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoPopulateParentBirthNameRule";

    private static String PARENT1 = "Parent 1";
    private static String PARENT2 = "Parent 2";

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

        if (this.evaluateAutoPopulateParentBirthNameRule(nodeRuleEvaluationContext)) {
            return new Outcome(RelationshipTypeConstants.YES.value, true);
        }
        return new Outcome(RelationshipTypeConstants.NO.value, true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        if (this.evaluateAutoPopulateParentBirthNameRule(nodeRuleEvaluationContext)) {
            this.autoPopulateFromCurrentNameFields(nodeRuleEvaluationContext);
        } else {
            this.resetPopulatedCurrentNameFields(nodeRuleEvaluationContext);
        }
    }

    private void resetPopulatedCurrentNameFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        List<String> currentNameFields = fieldsToTransfer.values().toList();
        answerService.removeAutoFillFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, currentNameFields);
    }


    private void autoPopulateFromCurrentNameFields(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Map<String, String> fieldsToTransfer = this.getFieldTransferMapper(nodeRuleEvaluationContext);
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer);
    }

    private Map<String, String> getFieldTransferMapper(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String ruleParam = questionNodeInstance.getRuleParam();
        Map<String, String> fieldsToTransfer = new HashMap<>();
        if(ruleParam == PARENT1){
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2883'] = 'Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2886';
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2884'] = 'Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2887';
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2885'] = 'Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2888';
        } else if(ruleParam == PARENT2){
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2901'] = 'Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2904';
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2902'] = 'Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2905';
            fieldsToTransfer['Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2903'] = 'Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2906';
        }
        return fieldsToTransfer;
    }


    // This method only calls if used answered the question (i.e) either yes or no... So here no needs to check the answer as valid or not
    private Boolean evaluateAutoPopulateParentBirthNameRule(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        String isCurrentNameSameAsBirthName = EasyVisaNode.normalizeAnswer(answer.getValue())
        if (isCurrentNameSameAsBirthName == RelationshipTypeConstants.YES.value) {
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
