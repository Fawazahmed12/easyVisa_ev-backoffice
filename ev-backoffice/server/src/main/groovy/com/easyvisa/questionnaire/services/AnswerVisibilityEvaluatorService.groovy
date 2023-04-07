package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityEvaluator
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AnswerVisibilityEvaluatorService implements IAnswerVisibilityEvaluator {

    @Autowired
    RuleActionHandler ruleMatchHandler;

    boolean evaluate(AnswerEvaluationContext answerEvaluationContext, QuestionNodeInstance questionNodeInstance) {
        if (StringUtils.isNotEmpty(questionNodeInstance.getAnswerVisibilityValidationRule())) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerEvaluationContext.answerList, questionNodeInstance, answerEvaluationContext.packageId, answerEvaluationContext.applicantId);
            Boolean hasValidAnswer = ruleMatchHandler.validateAnswerVisibility(questionNodeInstance.getAnswerVisibilityValidationRule(), nodeRuleEvaluationContext);
            return hasValidAnswer;
        }
        return questionNodeInstance.isVisibility();
    }

    @Override
    void populateAnswer(AnswerEvaluationContext answerEvaluationContext, QuestionNodeInstance questionNodeInstance) {
        if (StringUtils.isNotEmpty(questionNodeInstance.getAnswerVisibilityValidationRule())) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(answerEvaluationContext.answerList, questionNodeInstance, answerEvaluationContext.packageId, answerEvaluationContext.applicantId);
            ruleMatchHandler.populatePdfFieldAnswer(questionNodeInstance.getAnswerVisibilityValidationRule(), nodeRuleEvaluationContext);
        }
    }
}

