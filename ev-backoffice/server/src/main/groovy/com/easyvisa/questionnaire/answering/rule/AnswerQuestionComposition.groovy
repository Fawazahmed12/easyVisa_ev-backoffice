package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance

class AnswerQuestionComposition {

    private Answer answer;
    private QuestionNodeInstance questionNodeInstance;

    AnswerQuestionComposition(QuestionNodeInstance questionNodeInstance, Answer answer){
        this.questionNodeInstance = questionNodeInstance;
        this.answer = answer;
    }

    Answer getAnswer() {
        return answer
    }

    void setAnswer(Answer answer) {
        this.answer = answer
    }

    QuestionNodeInstance getQuestionNodeInstance() {
        return questionNodeInstance
    }

    void setQuestionNodeInstance(QuestionNodeInstance questionNodeInstance) {
        this.questionNodeInstance = questionNodeInstance
    }
}
