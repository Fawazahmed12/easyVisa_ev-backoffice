package com.easyvisa

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

import java.time.DayOfWeek

import static java.time.DayOfWeek.*

class LegalRepresentativeSpec extends Specification implements DomainUnitTest<LegalRepresentative> {

    def setup() {
    }

    def cleanup() {
    }

    void "test uscisOnlineAccountNo constraints"(uscisOnlineAccountNo, isValid) {
        when:
        LegalRepresentative legalRepresentative = new LegalRepresentative(uscisOnlineAccountNo: uscisOnlineAccountNo)
        def pt = /^\d{4}-\d{4}-\d{4}/
        then:
        isValid == legalRepresentative.validate(["uscisOnlineAccountNo"])
        where:
        uscisOnlineAccountNo  | isValid
        "1111-2222-33333"     | false
        "1111-2222-333"       | false
        "11111-2222-3333"     | false
        "1111-22222-3333"     | false
        " 1111- 2222 -3333  " | false
        "1111-2222-A"         | false
        "  1111-2222-A"       | false
        "1111-2222-3333A"     | false
        "1111 - 2222 - 3333A" | false
        "1111-2222-3333"      | true
    }


    void "test workingHours should not have duplicates"(List<DayOfWeek> daysOfWeek, Boolean isValid) {
        when:
        LegalRepresentative representative = new LegalRepresentative()
        daysOfWeek.each {
            representative.addToWorkingHours(new WorkingHour(dayOfWeek: it, startMinutes: 0, startHour: 0, endMinutes: 30, endHour: 18))
        }
        then:
        isValid == representative.validate(['workingHours'])
        where:
        daysOfWeek                                     | isValid
        [MONDAY]                                       | true
        [MONDAY, MONDAY]                               | false
        [MONDAY, TUESDAY]                              | true
        [MONDAY, TUESDAY, WEDNESDAY]                   | true
        [MONDAY, TUESDAY, WEDNESDAY,
         THURSDAY, FRIDAY, SATURDAY, SUNDAY]           | true
        [MONDAY, TUESDAY, WEDNESDAY,
         THURSDAY, FRIDAY, SATURDAY, SUNDAY, THURSDAY] | false

    }
}
