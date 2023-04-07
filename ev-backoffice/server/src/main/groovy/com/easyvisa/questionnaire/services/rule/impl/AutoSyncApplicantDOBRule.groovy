package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.ApplicantService
import com.easyvisa.Petitioner
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Birth Information
 * SubSection: Birth Information
 * Question: (Q_88/Q_2202) Date of Birth
 *
 * Note: This rule will sync the applicant dateOfBirth value (from questionnaire) to its Applicant object
 *
 * **/

@Component
class AutoSyncApplicantDOBRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncApplicantDOBRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    ApplicantService applicantService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), Answer.isValidAnswer(answer));
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId);
        applicant.dateOfBirth = DateUtil.toDate(answer.value);
        this.applicantService.saveApplicant(applicant);
    }
}
