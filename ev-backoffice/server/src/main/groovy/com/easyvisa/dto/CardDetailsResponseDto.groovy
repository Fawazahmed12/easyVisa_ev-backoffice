package com.easyvisa.dto

import groovy.transform.CompileStatic

@CompileStatic
class CardDetailsResponseDto {

    String customerId
    String cardHolder
    String cardLastFour
    String cardType
    String cardExpiration
    String address1
    String address2
    String addressCity
    String addressState
    String addressCountry
    String addressZip

    void setCardType(String cardType) {
        this.cardType = cardType.capitalize()
    }

    void setCardExpiration(String cardExpiration) {
        StringBuilder sb = new StringBuilder()
        sb.append(cardExpiration[0..1])
                .append('/')
                .append(cardExpiration[2..-1])
        this.cardExpiration = sb.toString()
    }

}
