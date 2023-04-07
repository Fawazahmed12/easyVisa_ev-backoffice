package com.easyvisa.questionnaire

import com.easyvisa.enums.SectionCompletionState

class SectionCompletionStatus {

    Long id
    Long applicantId
    Long packageId
    String sectionId
    SectionCompletionState completionState
    Double completedPercentage
    Date dateCreated
    Date lastUpdated

    static constraints = {
        applicantId nullable: false
        packageId nullable: false
        sectionId nullable: false
        completionState nullable: false
        completedPercentage nullable: false
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'section_completion_status_id_seq']
    }

}
