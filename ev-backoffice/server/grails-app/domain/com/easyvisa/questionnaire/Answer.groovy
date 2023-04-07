package com.easyvisa.questionnaire

import com.easyvisa.User
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import org.apache.commons.lang.StringUtils

/**
 * Answer is always stored as String (due to encryption requirements)
 * Each Answer is also paired with a path - through which we can globally identify the section/subsection/question/repeatinggroup
 * // Example Path : 1 <sectionId>/<subsectionId>/<questionId>/0  (if the field is part of repeating group)
 * // Example Path : 2 <sectionId>/<subsectionId>/<questionId> (if it is direct field)
 */

class Answer {

    Long id
    String value = ''
    String path
    Long applicantId
    Long packageId
    String sectionId
    String subsectionId
    String questionId
    Integer index // will hold repeating answers index
    Date dateCreated
    Date lastUpdated

    User createdBy
    User updatedBy

    Answer() {
    }

    static mapping = {
        value type: 'text'
        id generator: 'native', params: [sequence: 'answer_id_seq']
    }

    static constraints = {
        applicantId nullable: false
        packageId nullable: false
        sectionId nullable: false
        subsectionId nullable: false
        questionId nullable: false
        path nullable: true, unique: ['packageId', 'applicantId']
        index nullable: true
        value nullable: true
        dateCreated nullable: true
        lastUpdated nullable: true
        createdBy nullable: true
        updatedBy nullable: true
    }


    static boolean isValidAnswer(Answer answer) {
        if (!answer) {
            return false
        }
        return StringUtils.isNotEmpty(answer.value);
    }


    boolean isYesValue() {
        return getValue().equalsIgnoreCase(RelationshipTypeConstants.YES.value)
    }

    boolean isNoValue() {
        return getValue().equalsIgnoreCase(RelationshipTypeConstants.NO.value)
    }

    boolean doesMatch(valueToMatch) {
        return getValue() == valueToMatch
    }

    Answer clone() {
        Answer answer1 = new Answer(packageId: this.packageId, applicantId: this.applicantId, index: this.index,
                sectionId: this.sectionId, subsectionId: this.subsectionId, questionId: this.questionId, path: this.path,
                value: this.value)
        return answer1;
    }
}
