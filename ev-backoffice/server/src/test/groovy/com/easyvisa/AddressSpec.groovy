package com.easyvisa

import com.easyvisa.enums.State
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class AddressSpec extends Specification implements DomainUnitTest<Address> {

    def setup() {
    }

    def cleanup() {
    }

    void 'test zipCode format constraints'(zipCode, isValid) {
        when:
        Address address = new Address(zipCode: zipCode)
        then:
        address.validate(['zipCode']) == isValid
        where:
        zipCode      | isValid
        '1111'       | false
        '1234567890' | false
        '12345'      | true
        'a12345'     | false
        '123456789'  | true
        'a123456789' | false
    }

    void 'test zipCode/postalCode combination'(zipCode, postalCode, isValid) {
        when:
        Address address = new Address(zipCode: zipCode, postalCode: postalCode)
        then:
        address.validate(['zipCode', 'postalCode']) == isValid
        where:

        zipCode | postalCode | isValid
        null    | '1122334'  | true
        '11223' | null       | true
        null    | null       | true
        '11223' | '1122443'  | false
    }

    void 'test state/province combination'(state, province, isValid) {
        when:
        Address address = new Address(state: state, province: province)
        then:
        address.validate(['state', 'province']) == isValid
        where:

        state         | province    | isValid
        null          | null        | true
        null          | 'province1' | true
        State.ALABAMA | null        | true
        State.ALASKA  | 'province'  | false
    }
}