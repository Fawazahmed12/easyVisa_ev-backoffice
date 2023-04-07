package com.easyvisa

class RepeatingQuestionGroupCommand implements grails.validation.Validateable {
    Long packageId
    Long applicantId
    String sectionId
    String subsectionId
    String repeatingGroupId
    Integer index
}
