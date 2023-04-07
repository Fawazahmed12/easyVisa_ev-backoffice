package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.dto.AnswerValidationDto

import java.util.stream.Collectors

class AnswerValidationRuleEvaluationContext {

    Answer answerToSave;
    List<Answer> answerList
    QuestionNodeInstance questionNodeInstance
    Long packageId
    Long applicantId

    AnswerValidationRuleEvaluationContext(Answer answerToSave, List<Answer> answerList, QuestionNodeInstance questionNodeInstance,
                                          Long packageId, Long applicantId) {
        this.answerToSave = answerToSave
        this.answerList = answerList
        this.questionNodeInstance = questionNodeInstance
        this.packageId = packageId
        this.applicantId = applicantId
    }

    Answer findAnswerByPath(String questionPath) {
        Answer matchedAnswer = this.answerList.stream()
                .filter({ answer -> answer.getPath().startsWith(questionPath) })
                .findFirst()
                .orElse(null)
        return matchedAnswer;
    }

    List<Answer> findAnswerListByPath(String questionPath) {
        List<Answer> matchedAnswerList = this.answerList.stream()
                .filter({ answer -> answer.getPath().startsWith(questionPath) })
                .collect(Collectors.toList());
        return matchedAnswerList;
    }

    AnswerValidationDto constructAnswerValidationDto() {
        AnswerValidationDto answerValidationDto = new AnswerValidationDto();
        answerValidationDto.setQuestionId(this.answerToSave.questionId);
        answerValidationDto.setSubsectionId(this.answerToSave.subsectionId);
        answerValidationDto.setValue(this.answerToSave.value);
        answerValidationDto.setIndex(this.answerToSave.index);
        return answerValidationDto;
    }

    NodeRuleEvaluationContext getNodeRuleEvaluationContext() {
        return new NodeRuleEvaluationContext(this.answerList, this.questionNodeInstance, this.packageId, this.applicantId);
    }

    String getAnswerToSavePath() {
        List answerPathParts = [this.answerToSave.sectionId, this.answerToSave.subsectionId, this.answerToSave.questionId]
        if (this.answerToSave.index >= 0) {
            answerPathParts.add(this.answerToSave.index as String)
        }
        return answerPathParts.join('/')
    }
}
