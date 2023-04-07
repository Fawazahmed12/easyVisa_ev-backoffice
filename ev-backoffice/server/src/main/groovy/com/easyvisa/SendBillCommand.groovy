package com.easyvisa

import grails.validation.Validateable

class SendBillCommand implements Validateable {
    List<ChargeCommand> charges
    String email

    Map getFeeCharges() {
        List feeCharges = []
        BigDecimal total = BigDecimal.ZERO
        this.charges?.each {
            BigDecimal subTotal = it.each * it.quantity
            Map charge = [description: it.description, each: it.each, qty: it.quantity, subTotal: subTotal]
            total = total + subTotal
            feeCharges << charge
        }
        return [charges: feeCharges, total: total]
    }
}
