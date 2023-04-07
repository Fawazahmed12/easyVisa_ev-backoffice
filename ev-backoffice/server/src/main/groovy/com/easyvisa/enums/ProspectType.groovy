package com.easyvisa.enums

enum ProspectType {

    PHONE('Phone'),
    OFFICE('Office Number'),
    EMAIL('Email'),
    FAX('Fax'),
    LOCATION('Location'),
    PROFILE('Profile')

    final String mSName

    ProspectType(String mSName) {
        this.mSName = mSName
    }

    static ProspectType findByName(String name) {
        values().find { it.mSName == name }
    }
}
