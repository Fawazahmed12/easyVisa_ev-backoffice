package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class GetEmailTemplateVariableCommand implements grails.validation.Validateable {

    List<EmailTemplateType> emailTemplate

}
