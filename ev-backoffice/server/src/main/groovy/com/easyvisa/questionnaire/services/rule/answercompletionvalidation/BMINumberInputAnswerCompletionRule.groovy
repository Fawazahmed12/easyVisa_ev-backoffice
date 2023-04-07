package com.easyvisa.questionnaire.services.rule.answercompletionvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerCompletionValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class BMINumberInputAnswerCompletionRule implements IAnswerCompletionValidationRule {

    private static String RULE_NAME = 'BMINumberInputAnswerCompletionRule'

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
        String answerCompletionValidationRuleParam = questionNodeInstance.getAnswerCompletionValidationRuleParam()
        int MAX_BMI_INPUT_VALUE = answerCompletionValidationRuleParam as Integer;
        String answerValue = answer.getValue();
        return (answerValue.toInteger() <= MAX_BMI_INPUT_VALUE) ? true : false;
    }
}
