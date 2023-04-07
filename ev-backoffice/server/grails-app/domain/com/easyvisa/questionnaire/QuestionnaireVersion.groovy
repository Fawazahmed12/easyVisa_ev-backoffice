package com.easyvisa.questionnaire

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class QuestionnaireVersion {
    String questVersion
    Date startDate
    Date endDate
    Date dateCreated
    Date lastUpdated

    static constraints = {
        questVersion nullable: false
        endDate nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'questionnaire_version_id_seq']
    }
}
