package com.easyvisa

import grails.validation.Validateable
import groovy.transform.CompileStatic

@CompileStatic
class ChangePasswordCommand implements Validateable {

    String oldPassword
    String newPassword

}
