package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerCompletionValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 *
 * This rule will check the answerValue as integer and checks the numericCharcter length
 * by using rule-param.
 *
 * This rule is common for the  following questions
 *  1. Zip Code   (EXACTLY 5 numbers)
 *  2. Social Security Number (If any)    (EXACTLY 9 digits)
 *          ( Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_113)
 */

@Component
class NumberInputAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'NumberInputAnswerCompletionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerCompletionValidationRule(RULE_NAME, this);
    }

    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer answer = questionNodeInstance.getAnswer();
        if(!Answer.isValidAnswer(answer)){
            return false;
        }
        String answerValue = answer.getValue();
        String numericCharacterLength = questionNodeInstance.getAnswerCompletionValidationRuleParam()
        return (answerValue.isBigInteger() && answerValue.length() == numericCharacterLength.toInteger()) ? true : false;
    }
}
