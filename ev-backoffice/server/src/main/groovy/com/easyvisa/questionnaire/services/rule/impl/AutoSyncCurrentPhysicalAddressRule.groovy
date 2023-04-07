package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AnswerService
import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Package
import com.easyvisa.enums.ImmigrationBenefitCategory
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
 * SubSection: Current Physical Address
 * Notes:  This rule will copy the current question answer to the ruleParam path question,
 *         only if the question(Q_69) "Is your current mailing address the same as your
 *         current physical address?" is 'yes'... ,
 *         (i.e) If the auto-populate is true then here we are syncing
 *         the current question-answer from (Current Physical Address),  to
 *         its corresponding question-answer (Current Mailing Address).
 */
@Component
class AutoSyncCurrentPhysicalAddressRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncCurrentPhysicalAddressRule";

    private static String MAILING_ADDRESS_BENEFICIARY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015"
    private static String MAILING_ADDRESS_765_BENEFICIARY_PATH = "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_6038"


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
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String questionnaireRuleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        String[] ruleParams = questionnaireRuleParam.split("\\|");
        ruleParams.each{ ruleParam ->
            this.evaluateAutoSyncCurrentPhysicalAddressAnswer(ruleParam, nodeRuleEvaluationContext);
        }
    }


    private void evaluateAutoSyncCurrentPhysicalAddressAnswer(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        if (this.evaluateAutoPopulateCurrentMailingAddressRule(ruleParam, nodeRuleEvaluationContext)) {
            this.autoPopulateFromCurrentPhysicalAddressFields(ruleParam, nodeRuleEvaluationContext);
        }
    }


    Boolean evaluateAutoPopulateCurrentMailingAddressRule(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        String MAILING_ADDRESS_POPULATE_PATH = this.getMailingAddressPopulatePath(ruleParam, nodeRuleEvaluationContext);
        Answer currentMailingAddressPopulateAnswer = nodeRuleEvaluationContext.findAnswerByPath(MAILING_ADDRESS_POPULATE_PATH)
        if (!Answer.isValidAnswer(currentMailingAddressPopulateAnswer)) {
            return false
        }

        String isCurrentMailingAddressSameAsPhysicalAddress = EasyVisaNode.normalizeAnswer(currentMailingAddressPopulateAnswer.getValue())
        if (isCurrentMailingAddressSameAsPhysicalAddress == RelationshipTypeConstants.YES.value) {
            return true
        }
        return false
    }


    private void autoPopulateFromCurrentPhysicalAddressFields(String ruleParam, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        List answerPathParts = [answer.sectionId, answer.subsectionId, answer.questionId];
        String[] ruleParams = ruleParam.split(";");
        Map<String, String> fieldsToTransfer = new HashMap<>();
        fieldsToTransfer[answerPathParts.join('/')] = ruleParams[1];
        answerService.populateAutoFields(nodeRuleEvaluationContext.packageId, nodeRuleEvaluationContext.applicantId, fieldsToTransfer);
    }


    private String getMailingAddressPopulatePath(String ruleParam, NodeRuleEvaluationContext ruleEvaluationContext) {
        String[] ruleParams = ruleParam.split(";");
        String MAILING_ADDRESS_POPULATE_PATH = ruleParams[0];

        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        Applicant applicant = Applicant.get(ruleEvaluationContext.applicantId);
        ImmigrationBenefit immigrationBenefit = aPackage.getImmigrationBenefitByApplicant(applicant)
        if (immigrationBenefit.category == ImmigrationBenefitCategory.EAD && MAILING_ADDRESS_POPULATE_PATH == MAILING_ADDRESS_BENEFICIARY_PATH) {
            return MAILING_ADDRESS_765_BENEFICIARY_PATH;
        }
        return MAILING_ADDRESS_POPULATE_PATH;
    }
}
