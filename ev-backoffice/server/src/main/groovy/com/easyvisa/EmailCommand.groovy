package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class EmailCommand implements grails.validation.Validateable {

    Long id
    String content
    Long packageId
    Long representativeId
    EmailTemplateType templateType
    String subject
    Boolean sendEmail = false
}
