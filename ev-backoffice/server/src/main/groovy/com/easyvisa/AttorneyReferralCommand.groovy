package com.easyvisa

class AttorneyReferralCommand implements grails.validation.Validateable {

    String email

    static constraints = {
        email nullable:false, email:true
    }

}
