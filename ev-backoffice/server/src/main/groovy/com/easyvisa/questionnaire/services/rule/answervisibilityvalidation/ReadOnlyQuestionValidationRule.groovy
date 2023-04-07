package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * In questionnaire, there are some questions which has default values...
 * And don't need to display this question in questionnaire page..
 * But need to print its default value in USCIS forms..
 *
 * In this case, we are setting its visibility as false from rule called ReadOnlyVisibilityConstraintRule,
 * So here this AnswerValidationRule make that question as valid even if it is invisible to the questionnaire page..
 *
 * Example:
 *
 * Section: Address History
 * SubSection: Current Physical Address
 * Question: (Q_6000) When did you move out of this address?
 */

@Component
class ReadOnlyQuestionValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'ReadOnlyQuestionValidationRule'

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
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        Answer readonlyAnswer = questionNodeInstance.getAnswer();
        return Answer.isValidAnswer(readonlyAnswer);
    }
}
