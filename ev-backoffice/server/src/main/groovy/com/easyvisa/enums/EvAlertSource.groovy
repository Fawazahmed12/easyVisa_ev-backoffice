package com.easyvisa.enums

import com.easyvisa.EvSystemMessage

enum EvAlertSource {

    EASYVISA(EvSystemMessage.EASYVISA_SOURCE),
    USCIS('USCIS'),
    DHS('DHS'),
    DOS('DOS'),
    NVC('NVC'),
    AILA('AILA'),
    SSA('SSA'),
    SCOTUS('SCOTUS'),
    DOJ('DOJ'),
    IRS('IRS'),
    US_GOV('US Gov.')

    final String displayName

    EvAlertSource(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }

}