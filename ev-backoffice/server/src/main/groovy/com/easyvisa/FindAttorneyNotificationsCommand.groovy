package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class FindAttorneyNotificationsCommand implements grails.validation.Validateable {

    List<EmailTemplateType> types

}
