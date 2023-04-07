package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class AttorneyNotificationCommand implements grails.validation.Validateable {

    Long id
    String content
    String subject
    Integer repeatInterval
    EmailTemplateType templateType

    static constraints = {
        id nullable:true
    }

}
