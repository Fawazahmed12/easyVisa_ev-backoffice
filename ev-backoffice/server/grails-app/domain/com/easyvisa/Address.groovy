package com.easyvisa

import com.easyvisa.enums.Country
import com.easyvisa.enums.State
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class Address {

    Country country
    String line1
    String line2
    State state
    String province
    String zipCode
    String postalCode
    String city

    Date dateCreated
    Date lastUpdated

    static constraints = {

        city nullable: true, blank: false
        country nullable: true
        line1 nullable: true, blank: false
        line2 nullable: true, blank: true
        province nullable: true, blank: true
        zipCode nullable: true, validator: { zipCode, address, errors ->
            int zipLength = zipCode?.length() ?: 0

            if (zipLength > 0) {
                if (zipLength == 5 || zipLength == 9) {
                    return true
                }
                errors.rejectValue('zipCode', 'zipCode.invalidLength')
            }
            true
        }

        postalCode nullable: true, validator: { postalCode, address, errors ->
            if (postalCode) {
                if (address.zipCode) {
                    errors.rejectValue('postalCode', 'postalCode.invalidSinceZipCodeIsPresent')
                }
            }
            true
        }

        state nullable: true, validator: { state, address, errors ->
            if (state && address.province) {
                errors.rejectValue('province', 'state.invalidSinceStateIsPresent')
            }
        }
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'address_id_seq']
        cache true
    }

}
