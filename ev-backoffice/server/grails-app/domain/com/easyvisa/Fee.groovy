package com.easyvisa


import com.easyvisa.enums.ImmigrationBenefitCategory
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class Fee {

    LegalRepresentative representative
    ImmigrationBenefitCategory benefitCategory
    BigDecimal amount

    Date dateCreated
    Date lastUpdated

    static belongsTo = [representative: LegalRepresentative]

    static constraints = {
        //There can only be one fee for an attorney and category
        benefitCategory unique: 'representative'
        amount nullable: false, min: BigDecimal.ZERO
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'fee_id_seq']
    }
}

