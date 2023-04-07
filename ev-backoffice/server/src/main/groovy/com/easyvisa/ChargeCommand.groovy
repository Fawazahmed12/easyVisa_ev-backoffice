package com.easyvisa

import grails.validation.Validateable

class ChargeCommand implements Validateable {
    String description
    BigDecimal each
    Integer quantity
}
