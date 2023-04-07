package com.easyvisa.enums

enum CitizenshipStatus {

    LPR('Lawful Permanent Resident'),
    U_S_CITIZEN('U.S. Citizen'),
    ALIEN('Alien'),
    U_S_NATIONAL('U.S. National')

    final String displayName

    CitizenshipStatus(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
