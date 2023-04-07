package com.easyvisa

import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class AdditionalFee {

    Package aPackage
    String description
    BigDecimal fee
    Integer quantity

    static belongsTo = [aPackage: Package]

    static constraints = {
        quantity min: 0
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'additional_fee_id_seq']
    }
}

