package com.easyvisa.enums

enum RegistrationStatus {

    NEW,
    EMAIL_VERIFIED,
    REPRESENTATIVE_SELECTED,
    CONTACT_INFO_UPDATED,
    COMPLETE

    static Map getUpdatableStatuses() {
        [(REPRESENTATIVE_SELECTED): [CONTACT_INFO_UPDATED]]
    }

}
