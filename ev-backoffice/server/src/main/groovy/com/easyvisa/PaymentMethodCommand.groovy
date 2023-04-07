package com.easyvisa

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class PaymentMethodCommand implements Validateable {

    String customerId
    String cardHolder
    String cardLastFour
    String cardType
    String cardExpiration
    String address1
    String address2
    String addressCity
    String addressState
    String addressCountry
    String addressZip
    String fmPaymentMethodId

    static constraints = {
        fmPaymentMethodId blank: false
        address2 nullable: true
        addressCountry nullable: true
    }

}
