package com.easyvisa


import groovy.transform.ToString

import java.time.DayOfWeek

@ToString(includes = 'id', includeNames = true, includePackage = false)
class WorkingHour {

    Integer startHour
    Integer startMinutes
    Integer endHour
    Integer endMinutes
    DayOfWeek dayOfWeek

    Date dateCreated
    Date lastUpdated

    static constraints = {
        startHour min: 0, max: 24, nullable: true
        endHour min: 0, max: 24, nullable: true
        startMinutes min: 0, max: 60, nullable: true
        endMinutes min: 0, max: 60, nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'working_hour_id_seq']
    }
}
