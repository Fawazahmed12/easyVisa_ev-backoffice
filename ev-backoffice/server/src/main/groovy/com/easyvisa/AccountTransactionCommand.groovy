package com.easyvisa

import grails.validation.Validateable

class AccountTransactionCommand implements Validateable {

    String memo
    BigDecimal amount

    static constraints = {
        memo blank: false, maxSize: 255
    }

}
