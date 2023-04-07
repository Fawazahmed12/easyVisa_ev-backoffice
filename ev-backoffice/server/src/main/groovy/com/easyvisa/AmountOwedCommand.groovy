package com.easyvisa

class AmountOwedCommand implements grails.validation.Validateable{

    BigDecimal owed

    static constraints={
        owed nullable: true, min:0 as BigDecimal
    }
}
