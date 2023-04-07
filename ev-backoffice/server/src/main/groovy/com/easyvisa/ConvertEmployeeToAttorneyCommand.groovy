package com.easyvisa

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class ConvertEmployeeToAttorneyCommand implements Validateable {

    PaymentMethodCommand paymentMethod
    ProfileCommand profile

}
