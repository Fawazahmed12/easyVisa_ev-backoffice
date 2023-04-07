package com.easyvisa

import com.easyvisa.questionnaire.Answer
import groovy.transform.ToString

@ToString(includes = 'id', includeSuperProperties=true, includeNames = true, includePackage = false)
class Warning extends EvSystemMessage {

    Package aPackage
    Applicant applicant
    String questionId
    Answer answer

    static constraints = {
        questionId nullable: true
        answer nullable: true
    }

    static mapping = {
        tablePerHierarchy false
    }

}
