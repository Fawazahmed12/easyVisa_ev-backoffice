package com.easyvisa.enums

enum OrganizationType {

    LAW_FIRM('Law Firm'),
    RECOGNIZED_ORGANIZATION('Recognized Organization'),
    SOLO_PRACTICE('Solo Practice')

    final String displayName

    OrganizationType(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}