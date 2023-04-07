package com.easyvisa.dto

import com.easyvisa.Package
import groovy.transform.CompileStatic

@CompileStatic
class PackageResponseDto {

    Package aPackage
    List<MessageResponseDto> messages = []
}
