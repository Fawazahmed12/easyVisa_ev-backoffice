package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AlertService
import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.RelationshipTypeConstants

abstract class BaseAlienFianceRule extends BaseComputeRule {

    private static String CLIENTNAME_PLACEHOLDER = '\\[Insert client name\\]'

    @Override
    public Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        List<Answer> answerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(nodeRuleEvaluationContext.getPackageId(), nodeRuleEvaluationContext
                .getApplicantId(), this.getFieldPath());
        Outcome negativeAnswer = new Outcome(RelationshipTypeConstants.NO.name(), false);
        if (answerList.isEmpty()) {
            return negativeAnswer;
        }

        Answer answer = answerList[0];
        if (answer.getValue().toLowerCase() == "yes") {
            return new Outcome(RelationshipTypeConstants.YES.name(), true);
        }
        return negativeAnswer;
    }

    void createPackageWarning(String templateMessage, AlertService alertService, NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        Package packageObj = Package.get(nodeRuleEvaluationContext.packageId)
        Applicant petitionerApplicant = packageObj.client
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId);
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        String warningMessage = templateMessage.replaceAll(CLIENTNAME_PLACEHOLDER, petitionerApplicant.getName());
        alertService.createPackageWarning(packageObj, applicant, EasyVisaSystemMessageType.QUESTIONNAIRE_WARNING,
                warningMessage, questionNodeInstance.id, questionNodeInstance.answer)
    }

    @Override
    void updateVisibilityOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext) {

    }


    abstract String getFieldPath();
}
