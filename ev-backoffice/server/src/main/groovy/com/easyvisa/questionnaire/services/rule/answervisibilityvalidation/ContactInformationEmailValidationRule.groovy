package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ContactInformationEmailValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'ContactInformationEmailValidationRule'
    private static String EMAIL_AVAILABILITY_FIELD_PATH = 'Sec_contactInformationForBeneficiary/SubSec_emailForBeneficiary/Q_6015'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerVisibilityValidationRule(RULE_NAME, this);
    }

    @Override
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext) {

    }

    @Override
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer emailAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(EMAIL_AVAILABILITY_FIELD_PATH);
        // Here don't treat this answer as valid, Otherwise system checks all question's answer using
        // 'Answer.isValidAnswer' inorder to update section-completion status
        if (Answer.isValidAnswer(emailAvailabilityAnswer) && emailAvailabilityAnswer.value == 'true') {
            return false;
        }
        return questionNodeInstance.isVisibility();
    }
}
