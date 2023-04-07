package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerCompletionValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.apache.commons.validator.routines.RegexValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Section: Contact Information
 * SubSection: email
 * Question: (Q_86) email address
 *
 * Here, Check to ensure that email has at least 1 character, then the ‘@’ symbol,
 * then 2 or more characters after the ‘@’ symbol, then a period (.),
 * then 2 or more characters after the ‘.’, also NO spaces are allowed.
 *
 * The check mark should ONLY appear if the email was in the correct allowable format.
 */
@Component
class EmailAddressAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'EmailAddressAnswerCompletionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerCompletionValidationRule(this.getRuleName(), this);
    }


    String getRuleName() {
        return RULE_NAME;
    }

    // https://www.w3resource.com/javascript/form/email-validation.php
    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer emailAddressAnswer = questionNodeInstance.getAnswer();
        return this.validateEmailAddress(emailAddressAnswer);
    }


    Boolean validateEmailAddress(Answer emailAddressAnswer) {
        String mailFormat = /^\w+([\.(-|+)]?\w+)*@\w{2,}([\.(-|+)]?\w+)*(\.\w{2,3})+$/;
        RegexValidator validator = new RegexValidator(mailFormat, false);
        if (Answer.isValidAnswer(emailAddressAnswer) && validator.isValid(emailAddressAnswer.getValue())) {
            return true;
        }
        return false;
    }
}