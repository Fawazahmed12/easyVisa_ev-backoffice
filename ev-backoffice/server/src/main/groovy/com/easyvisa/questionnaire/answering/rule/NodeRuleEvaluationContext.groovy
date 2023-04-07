package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.EasyVisaNodeInstance

import java.time.LocalDate
import java.util.function.Predicate
import java.util.stream.Collectors

class NodeRuleEvaluationContext {

    List<Answer> answerList
    EasyVisaNodeInstance easyVisaNodeInstance
    Long packageId
    Long applicantId

    NodeRuleEvaluationContext(List<Answer> answerList, EasyVisaNodeInstance easyVisaNodeInstance,
                              Long packageId, Long applicantId) {
        this.answerList = answerList
        this.easyVisaNodeInstance = easyVisaNodeInstance
        this.packageId = packageId
        this.applicantId = applicantId
    }


    Answer findAnswerByPath(String questionPath) {
        Answer matchedAnswer = this.answerList.stream()
                .filter({ answer -> answer.path == questionPath })
                .findFirst()
                .orElse(null)
        return matchedAnswer;
    }


    Boolean matchAnswer(String questionPath,  matcherPredicate) {
        Answer answer = this.findAnswerByPath(questionPath)
        return Answer.isValidAnswer(answer) && matcherPredicate(answer);
    }

    List<Answer> findAnswerListByPath(String questionPath) {
        List<Answer> matchedAnswerList = this.answerList.stream()
                .filter({ answer -> answer.path == questionPath })
                .collect(Collectors.toList());
        return matchedAnswerList;
    }

    List<Answer> findAnswerListByPathILike(String questionPath) {
        List<Answer> matchedAnswerList = this.answerList.stream()
                .filter({ answer -> answer.path.startsWith(questionPath) })
                .collect(Collectors.toList());
        return matchedAnswerList;
    }

    String getQuestionnaireVersion() {
        return this.easyVisaNodeInstance.questVersion;
    }

    LocalDate getCurrentDate() {
        return this.easyVisaNodeInstance.currentDate
    }
}
