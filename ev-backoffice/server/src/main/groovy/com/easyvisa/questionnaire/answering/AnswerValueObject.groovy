package com.easyvisa.questionnaire.answering

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.util.DateUtil
import org.apache.commons.lang3.StringUtils

import java.time.LocalDate

class AnswerValueObject {

    Long id
    String value = ''
    String printValue = ''
    String path
    Long applicantId
    Long packageId
    String sectionId
    String subsectionId
    String questionId
    Integer index // will hold repeating answers index

    AnswerValueObject() {
        //default constructor
    }

    AnswerValueObject(Answer answer, String dataType = '') {
        this.id = answer.id
        this.value = answer.value
        this.printValue = answer.value
        this.path = answer.path
        this.applicantId = answer.applicantId
        this.packageId = answer.packageId
        this.sectionId = answer.sectionId
        this.subsectionId = answer.subsectionId
        this.questionId = answer.questionId
        this.index = answer.index // will hold repeating answers index
        this.formatDateValue(dataType)
    }

    AnswerValueObject(Answer answer, String dataType,  String pdfFieldRelationshipValue) {
        this(answer, dataType);
        if(StringUtils.isNotEmpty(pdfFieldRelationshipValue)) {
            this.value = pdfFieldRelationshipValue
            this.printValue = pdfFieldRelationshipValue
        }
    }

    void setValue(String value) {
        this.value = value
        this.printValue = value
    }

    private void formatDateValue(String dataType) {
        if (dataType == 'date') {
            LocalDate date = DateUtil.localDate(this.value)
            String pdfFormDate = DateUtil.pdfFormDate(date)
            this.value = pdfFormDate
            this.printValue = pdfFormDate
        }
    }

    AnswerValueObject copy() {
        AnswerValueObject result = new AnswerValueObject()
        result.id = id
        result.value = value
        result.printValue = printValue
        result.path = path
        result.applicantId = applicantId
        result.packageId = packageId
        result.sectionId = sectionId
        result.subsectionId = subsectionId
        result.questionId = questionId
        result.index = index
        result
    }
}
