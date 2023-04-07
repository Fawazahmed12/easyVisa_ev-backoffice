package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance
import com.easyvisa.questionnaire.answering.QuestionNodeInstance

class AnswerEvaluationContext {

    Long packageId
    Long applicantId
    List<Answer> answerList
    List<String> excludedPercentageCalculationQuestions


    Boolean hasExcludedPercentageCalculationQuestion(QuestionNodeInstance questionNodeInstance) {
        return excludedPercentageCalculationQuestions.contains(questionNodeInstance.id)
    }
}
