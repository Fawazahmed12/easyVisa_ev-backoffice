package com.easyvisa

import grails.testing.gorm.DomainUnitTest
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification

class UserSpec extends Specification implements DomainUnitTest<User> {

    def setup() {
    }

    def cleanup() {
    }

    void "test username rules"(username, isValid) {
        when:
        User user = new User(username: username)
        then:
        user.validate(['username']) == isValid
        where:
        username                    | isValid
        'username1'                 | true
        'username'                  | true
        '1122334455'                | true
        'a1122334455'               | true
        '1122334455z'               | true
        'user.name'                 | true
        getRandomAlphaNumString(7)  | false
        getRandomAlphaNumString(8)  | true
        getRandomAlphaNumString(64) | true
        getRandomAlphaNumString(65) | false
    }

    String getRandomAlphaNumString(int length) {
        RandomStringUtils.random(length, true, true).toLowerCase()
    }
}