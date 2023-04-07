package com.easyvisa

import com.easyvisa.enums.TaxItemType

class TaxEstimationCommand implements grails.validation.Validateable {

    TaxItemType type
    Long packageId
    Address address
    //uses for calculating package changes only
    PackageCommand packageObj

    static constraints = {
        packageId nullable:true
        address nullable:true
        packageObj nullable:true
    }
}