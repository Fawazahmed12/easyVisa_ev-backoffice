package com.easyvisa.dto

import com.easyvisa.enums.ErrorMessageType
import groovy.transform.CompileStatic

@CompileStatic
class MessageResponseDto {

    ErrorMessageType errorMessageType
    String messageCode
    Object[] messageCodeArgs
    String message
}
