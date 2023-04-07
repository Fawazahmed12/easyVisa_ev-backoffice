package com.easyvisa

import com.easyvisa.utils.StringUtils
import grails.validation.Validateable
import org.apache.http.HttpStatus

class ValidateAttorneyCommand extends PaginationCommand implements Validateable {
    String easyVisaId
    String email
    String organizationId

    Organization getOrganization() {
        organizationId ? Organization.get(organizationId) : null
    }

    void validateFields() {
        if (!StringUtils.isValidEmail(this.email)) {
            throw new EasyVisaException(errorMessageCode: 'invalid.email.format', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        }
        if (!StringUtils.isValidEasyVisaId(this.easyVisaId)) {
            throw new EasyVisaException(errorMessageCode: 'invalid.easyvisaid.format', errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY)
        }
    }


}