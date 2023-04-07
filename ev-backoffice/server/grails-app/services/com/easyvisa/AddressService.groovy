package com.easyvisa

import grails.gorm.transactions.Transactional

class AddressService {

    @Transactional
    Address updateAddress(Address oldAddress, Address newAddress) {
        if (oldAddress) {
            oldAddress.with {
                line1 = newAddress.line1
                line2 = newAddress.line2
                state = newAddress.state
                province = newAddress.province
                postalCode = newAddress.postalCode
                zipCode = newAddress.zipCode
                city = newAddress.city
                country = newAddress.country
            }
            oldAddress
        } else {
            newAddress
        }
    }
}
