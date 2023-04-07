package com.easyvisa

import com.easyvisa.enums.NotificationType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class EmailPreference {

    static constraints = {
        repeatInterval nullable:true
    }

    NotificationType type
    Boolean preference
    Integer repeatInterval

    static mapping = {
        id generator: 'native', params: [sequence: 'email_preference_id_seq']
    }

}

