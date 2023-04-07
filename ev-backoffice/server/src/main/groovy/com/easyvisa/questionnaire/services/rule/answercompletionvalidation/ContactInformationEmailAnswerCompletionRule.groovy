package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.springframework.stereotype.Component

@Component
class ContactInformationEmailAnswerCompletionRule extends EmailAddressAnswerCompletionRule {

    private static String RULE_NAME = 'ContactInformationEmailAnswerCompletionRule'
    private static String EMAIL_AVAILABILITY_FIELD_PATH = 'Sec_contactInformationForBeneficiary/SubSec_emailForBeneficiary/Q_6015'

    String getRuleName() {
        return RULE_NAME;
    }


    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer emailAvailabilityAnswer = ruleEvaluationContext.findAnswerByPath(EMAIL_AVAILABILITY_FIELD_PATH);
        if (Answer.isValidAnswer(emailAvailabilityAnswer) && emailAvailabilityAnswer.value == 'true') {
            return true;
        }
        Answer emailAddressAnswer = questionNodeInstance.getAnswer();
        return this.validateEmailAddress(emailAddressAnswer);
    }
}
