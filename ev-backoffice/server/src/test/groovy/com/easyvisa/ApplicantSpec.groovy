package com.easyvisa

import grails.testing.gorm.DomainUnitTest
import groovy.time.TimeCategory
import spock.lang.Specification

class ApplicantSpec extends Specification implements DomainUnitTest<Applicant> {

    def setup() {
    }

    def cleanup() {
    }

    void "test age limit rules"(date, result) {
        when:
        Applicant applicant = new Applicant(dateOfBirth: date)
        boolean isValid = applicant.validate(['dateOfBirth'])
        then:
        isValid == result
        where:
        date                         | result
        (new Date() - 1).clearTime() | true
        new Date().clearTime()       | true
        getDate(121)                 | false
        getDate(120)                 | true
        getDate(119)                 | true
    }


    Date getDate(int yearCount) {
        Date date
        use(TimeCategory) { date = (new Date() - yearCount.years).clearTime() }
        date
    }
}