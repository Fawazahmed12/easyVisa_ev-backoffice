package com.easyvisa

import com.easyvisa.enums.RegistrationStatus
import grails.testing.services.ServiceUnitTest
import org.apache.http.HttpStatus
import spock.lang.Specification

class ProfileServiceSpec extends Specification implements ServiceUnitTest<AttorneyService> {

    void "test attorney registrationStatus update works for valid statuses"(RegistrationStatus oldStatus, RegistrationStatus newStatus) {
        when:
        LegalRepresentative attorney = new LegalRepresentative(registrationStatus: oldStatus)
        attorney = service.maybeUpdateAttorneyRegistrationStatus(attorney, newStatus)
        then:
        attorney.registrationStatus == newStatus
        where:
        oldStatus                                  | newStatus
        RegistrationStatus.REPRESENTATIVE_SELECTED | RegistrationStatus.CONTACT_INFO_UPDATED
    }

    void "test attorney registrationStatus update does not throw exception if new and old status ar same"(RegistrationStatus status) {
        when:
        LegalRepresentative attorney = new LegalRepresentative(registrationStatus: status)
        attorney = service.maybeUpdateAttorneyRegistrationStatus(attorney, status)
        then:
        attorney.registrationStatus == status
        where:
        status                                     | _
        RegistrationStatus.NEW                     | _
        RegistrationStatus.EMAIL_VERIFIED          | _
        RegistrationStatus.REPRESENTATIVE_SELECTED | _
        RegistrationStatus.CONTACT_INFO_UPDATED    | _
        RegistrationStatus.COMPLETE                | _
    }

    void "test attorney registrationStatus update throws exception for invalid statuses"(RegistrationStatus oldStatus, RegistrationStatus newStatus) {
        when:
        LegalRepresentative attorney = new LegalRepresentative(registrationStatus: oldStatus)
        attorney = service.maybeUpdateAttorneyRegistrationStatus(attorney, newStatus)
        then:
        def e = thrown(EasyVisaException)
        e.errorCode == HttpStatus.SC_UNPROCESSABLE_ENTITY
        e.errorMessageCode == 'registrationstatus.not.updatable'
        where:
        oldStatus                               | newStatus
        RegistrationStatus.NEW                  | RegistrationStatus.EMAIL_VERIFIED
        RegistrationStatus.EMAIL_VERIFIED       | RegistrationStatus.CONTACT_INFO_UPDATED
        RegistrationStatus.CONTACT_INFO_UPDATED | RegistrationStatus.COMPLETE
        RegistrationStatus.COMPLETE             | RegistrationStatus.NEW
    }
}