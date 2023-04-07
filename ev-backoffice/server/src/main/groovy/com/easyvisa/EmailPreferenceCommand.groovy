package com.easyvisa

import com.easyvisa.enums.NotificationType

class EmailPreferenceCommand implements grails.validation.Validateable {

    NotificationType type
    Boolean preference

}
