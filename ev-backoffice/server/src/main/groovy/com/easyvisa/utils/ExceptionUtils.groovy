package com.easyvisa.utils

import com.easyvisa.EasyVisaException
import com.easyvisa.enums.ErrorMessageType
import org.apache.http.HttpStatus

class ExceptionUtils {

    static EasyVisaException createUnProcessableDataException(String errorMessageCode, String message = null,
                                                              List params = null, String errorSubMessageCode = null,
                                                              List subParams = null, ErrorMessageType type = null) {
        new EasyVisaException(errorCode: HttpStatus.SC_UNPROCESSABLE_ENTITY, errorMessageCode: errorMessageCode,
                errorSubMessageCode: errorSubMessageCode, message: message, params: params, subParams: subParams,
                errorMessageType: type)
    }

    static EasyVisaException createAccessDeniedException(String errorMessageCode, String message = null,
                                                         List params = null, ErrorMessageType type = null) {
        new EasyVisaException(errorCode: HttpStatus.SC_FORBIDDEN, errorMessageCode: errorMessageCode,
                message: message, params: params, errorMessageType: type)
    }

    static EasyVisaException createNotFoundException(String errorMessageCode, String message = null,
                                                     List params = null) {
        new EasyVisaException(errorCode: HttpStatus.SC_NOT_FOUND, errorMessageCode: errorMessageCode,
                message: message, params: params)
    }

    static EasyVisaException createLockedException(String errorMessageCode, ErrorMessageType errorMessageType,
                                                   String message = null, List params = null) {
        new EasyVisaException(errorCode: HttpStatus.SC_LOCKED, errorMessageCode: errorMessageCode,
                message: message, params: params, errorMessageType: errorMessageType)
    }

    static EasyVisaException createPackageAccessDeniedException(String message = null) {
        createAccessDeniedException(message ?: 'user.not.allowed.to.access.package')
    }
}
