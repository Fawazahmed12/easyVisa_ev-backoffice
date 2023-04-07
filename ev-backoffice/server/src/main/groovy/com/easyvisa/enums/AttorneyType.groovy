package com.easyvisa.enums

enum AttorneyType {

    SOLO_PRACTITIONER('Solo Practitioner'),
    MEMBER_OF_A_LAW_FIRM('Member of a Practice (Law Firm)')

    final String displayName

    AttorneyType(String displayName) {
        this.displayName = displayName
    }
}
