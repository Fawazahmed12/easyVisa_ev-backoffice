package com.easyvisa.utils

import groovy.time.TimeCategory

class DateUtils {

    static Integer getDays(Date firstDate, Date lastDate = new Date()) {
        use(TimeCategory) {
            (lastDate.copyWith([:]).clearTime() - firstDate.copyWith([:]).clearTime()).days
        }
    }
}
