package com.easyvisa

import com.easyvisa.enums.TimeUnitPeriod

class TimeUnit {

    Short time
    TimeUnitPeriod period

    String toString() {
        "${time} ${period.displayName}"
    }
}
