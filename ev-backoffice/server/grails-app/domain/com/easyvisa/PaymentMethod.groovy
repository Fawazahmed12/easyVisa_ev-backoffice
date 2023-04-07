package com.easyvisa

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.LocalDate

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class PaymentMethod {

    User user
    String fmPaymentMethodId
    String cardHolder
    String cardType
    String cardLastFour
    String cardExpiration
    String address1
    String address2
    String addressCity
    String addressState
    String addressCountry
    String addressZip

    static constraints = {
        cardHolder nullable: true
        cardType nullable: true
        cardLastFour size: 4..4, nullable: true
        cardExpiration size:6..6, nullable: true
        address1 nullable: true
        address2 nullable: true
        addressCity nullable: true
        addressState nullable: true
        addressCountry nullable: true
        addressZip nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'payment_method_id_seq']
    }

    Boolean isExpired() {
        LocalDate cardExpDate = LocalDate.of(cardExpiration[2..-1] as Integer, cardExpiration[0, 1] as Integer, 1)
        cardExpDate.plusMonths(1)
        cardExpDate.minusDays(1)
        LocalDate.now().isAfter(cardExpDate)
    }
}
