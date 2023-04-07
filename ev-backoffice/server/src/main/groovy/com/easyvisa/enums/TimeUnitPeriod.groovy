package com.easyvisa.enums

enum TimeUnitPeriod {

    AM('Am'),
    PM('Pm')

    final String displayName

    TimeUnitPeriod(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}