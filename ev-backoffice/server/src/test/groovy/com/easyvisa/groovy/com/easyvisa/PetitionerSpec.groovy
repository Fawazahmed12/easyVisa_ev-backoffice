package com.easyvisa

import grails.testing.gorm.DomainUnitTest
import groovy.time.TimeCategory
import spock.lang.Specification

class PetitionerSpec extends Specification implements DomainUnitTest<Petitioner> {

    def setup() {
    }

    def cleanup() {
    }

    void "test age limit rules"(date, result) {
        when:
        Applicant applicant = new Applicant(dateOfBirth: date)
        Petitioner petitioner = new Petitioner(applicant: applicant)
        boolean isValid = petitioner.validate(['applicant'])
        then:
        isValid == result
        where:
        date         | result
        getDate(121) | false
        getDate(120) | true
        getDate(119) | true
        getDate(19)  | true
        getDate(18)  | true
        getDate(17)  | false
    }


    Date getDate(int yearCount) {
        Date date
        use(TimeCategory) { date = (new Date() - yearCount.years).clearTime() }
        date
    }

}