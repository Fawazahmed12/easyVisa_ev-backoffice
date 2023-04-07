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
 * Section: Intro Questions
 * SubSection: Previous Immigration (Visa) Petitions You Filed for Another Person
 * Question: (Q_12) What was that person's Alien Registration Number (A-number)?
 * Notes:  Valid only if user entered 8 to 9 digits number..
 *         Otherwise mark it as incomelete (i.e) unanswered sign…
 */

@Component
class AlienNumberAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'AlienNumberAnswerCompletionRule'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerCompletionValidationRule(RULE_NAME, this);
    }

    @Override
    Boolean validateAnswerCompletion(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance();
        Answer alienNumberAnswer = questionNodeInstance.getAnswer();
        if(!Answer.isValidAnswer(alienNumberAnswer)){
            return false;
        }

        String alienNumberValue = alienNumberAnswer.getValue();
        if (alienNumberValue.isInteger()) {
            int alienIntegerNumber = alienNumberValue as Integer
            return (alienIntegerNumber>0 && alienNumberValue.size()>=8 && alienNumberValue.size()<=9)
        }
        return false;
    }
}
