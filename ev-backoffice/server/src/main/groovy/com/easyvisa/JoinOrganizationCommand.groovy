package com.easyvisa

import com.easyvisa.utils.StringUtils
import org.apache.http.HttpStatus

class JoinOrganizationCommand implements grails.validation.Validateable {
    String evId
    String email

    void validateFields() {
        if (!StringUtils.isValidEmail(this.email)) {
            throw new EasyVisaException(errorMessageCode: 'invalid.email.format', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        }
        if (!StringUtils.isValidEasyVisaId(this.evId)) {
            throw new EasyVisaException(errorMessageCode: 'invalid.easyvisaid.format', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        }
    }
}
