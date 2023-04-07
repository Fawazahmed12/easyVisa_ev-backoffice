package com.easyvisa

class PreviewEmailTemplateCommand implements grails.validation.Validateable {

    Long representativeId
    Long packageId
    String content

    List<ChargeCommand> charges

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    LegalRepresentative getAttorney() {
        representativeId ? LegalRepresentative.get(representativeId) : null
    }

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
