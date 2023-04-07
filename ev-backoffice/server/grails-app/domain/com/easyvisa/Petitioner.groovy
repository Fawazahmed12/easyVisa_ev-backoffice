package com.easyvisa

import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.enums.CitizenshipStatus
import groovy.time.TimeCategory

class Petitioner {

    Applicant applicant
    ProcessRequestState optIn = ProcessRequestState.PENDING
    CitizenshipStatus citizenshipStatus

    static constraints = {
        citizenshipStatus nullable: true
        applicant validator: { Applicant applicant ->
            if (applicant.dateOfBirth) {
                Date currentDate = new Date().clearTime()
                Date ageFor18
                use(TimeCategory) { ageFor18 = currentDate - 18.years }
                if (applicant.dateOfBirth > ageFor18) {
                    ['petitioner.less.than.18.years.of.age']
                } else {
                    Date maxAge
                    use(TimeCategory) {
                        maxAge = currentDate - (120.years)
                    }
                    if (maxAge > applicant.dateOfBirth) {
                        ['age.exceeds.max.limit']
                    }
                }
            }
        }
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'petitioner_id_seq']
        optIn defaultValue: "'PENDING'"
    }

    Profile getProfile() {
        applicant.getProfile()
    }

    String getName() {
        "${profile?.firstName} ${profile?.lastName}"
    }

    Petitioner copy() {
        Petitioner copy = new Petitioner()
        copy.applicant = applicant.copy()
        copy.optIn = optIn
        copy.citizenshipStatus = citizenshipStatus
        copy
    }

}
