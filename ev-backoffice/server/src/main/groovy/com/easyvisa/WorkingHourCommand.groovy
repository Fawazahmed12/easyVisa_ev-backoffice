package com.easyvisa


import java.time.DayOfWeek

class WorkingHourCommand {
    DayOfWeek dayOfWeek
    TimeFields start
    TimeFields end
    Long id

    WorkingHour asWorkingHour() {
        WorkingHour workingHour = new WorkingHour(id: id, dayOfWeek: dayOfWeek)
        workingHour.with {
            startMinutes = start.minutes
            startHour = start.hour
            endMinutes = end.minutes
            endHour = end.hour
        }
        workingHour
    }
}

class TimeFields {

    Integer hour
    Integer minutes
}