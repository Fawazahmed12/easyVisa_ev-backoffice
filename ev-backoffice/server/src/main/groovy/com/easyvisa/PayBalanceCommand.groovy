package com.easyvisa

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class PayBalanceCommand implements Validateable {

    BigDecimal balance

}
