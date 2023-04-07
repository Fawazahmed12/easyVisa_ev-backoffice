package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.answering.QuestionNodeInstance

interface IAnswerVisibilityEvaluator {
    boolean evaluate(AnswerEvaluationContext answerEvaluationContext, QuestionNodeInstance questionNodeInstance);
    void populateAnswer(AnswerEvaluationContext answerEvaluationContext, QuestionNodeInstance questionNodeInstance);
}
