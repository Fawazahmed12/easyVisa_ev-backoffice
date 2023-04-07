package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.Applicant
import com.easyvisa.Profile
import com.easyvisa.ProfileService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.services.rule.answercompletionvalidation.EmailAddressAnswerCompletionRule
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Contact Information
 * SubSection: email
 * Question: (Q_86/Q_2158) email
 *
 * Notes:  This rule sync the questionnaire answer to profile.email and also it sends basic notification email for security
 */


@Component
class AutoSyncApplicantEmailRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncApplicantEmailRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @Autowired
    AutoSyncUserProfileRule autoSyncUserProfileRule;

    @Autowired
    EmailAddressAnswerCompletionRule emailAddressAnswerCompletionRule;

    ProfileService profileService;

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }


    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        return this.autoSyncUserProfileRule.evaluateOutcome(nodeRuleEvaluationContext);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        Boolean hasValidEmailAddress = this.emailAddressAnswerCompletionRule.validateAnswerCompletion(nodeRuleEvaluationContext);
        if(!hasValidEmailAddress) {
            return;
        }

        this.autoSyncUserProfileRule.triggerFormActionOnSuccessfulMatch(nodeRuleEvaluationContext, previousAnswer);
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer currentAnswer = questionNodeInstance.getAnswer()
        String newEmail = currentAnswer?.value ?: ''
        String oldEmail = previousAnswer?.value ?: ''
        if (StringUtils.isNotEmpty(oldEmail) && StringUtils.isNotEmpty(newEmail)) {
            Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId)
            profileService.updateProfileEmail(applicant.profile, newEmail)
        }
    }
}
