package com.easyvisa

import com.easyvisa.enums.ErrorMessageType
import org.apache.http.HttpStatus

class EasyVisaException extends Exception {

    String message
    int errorCode = HttpStatus.SC_UNPROCESSABLE_ENTITY
    String errorMessageCode
    String errorSubMessageCode
    List params
    List subParams
    ErrorMessageType errorMessageType

}
