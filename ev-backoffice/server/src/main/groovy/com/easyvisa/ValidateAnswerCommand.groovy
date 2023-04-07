package com.easyvisa

import com.easyvisa.questionnaire.Answer
import grails.validation.Validateable

class ValidateAnswerCommand implements Validateable {
    Long id
    String value = ''
    String path
    Long applicantId
    Long packageId
    String sectionId
    String subsectionId
    String questionId
    Integer index // will hold repeating answers index

    Boolean hasAnswerCompleted;

    Answer toAnswer() {
        return new Answer(packageId: this.packageId, applicantId: this.applicantId, index: this.index, value: this.value,
                sectionId: this.sectionId, subsectionId: this.subsectionId, questionId: this.questionId, path: this.path)
    }
}
