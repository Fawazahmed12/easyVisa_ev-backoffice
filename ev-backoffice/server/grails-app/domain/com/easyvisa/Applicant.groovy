package com.easyvisa

import com.easyvisa.utils.DomainUtils
import groovy.time.TimeCategory
import groovy.transform.EqualsAndHashCode

@SuppressWarnings('GetterMethodCouldBeProperty')
//TODO: it was a fix of EV-577, just need to check it again
//@EqualsAndHashCode(includes = 'id,profile')
@EqualsAndHashCode(includes = 'id')
class Applicant implements EasyVisaIdGeneratable {

    SqlService sqlService

    Date dateOfBirth
    String mobileNumber
    String homeNumber
    String workNumber
    Boolean inviteApplicant

    User getUser() {
        profile?.user
    }

    static transients = ['sqlService']
    static constraints = {
        dateOfBirth nullable: true, validator: { Date dob ->
            if (dob) {
                Date currentDate = new Date().clearTime()
                if (dob > currentDate) {
                    ['dob.cant.be.in.future']
                } else {
                    Date maxAge
                    use(TimeCategory) {
                        maxAge = currentDate - (120.years)
                    }
                    if (maxAge > dob) {
                        ['age.exceeds.max.limit']
                    }
                }
            }
        }
        mobileNumber nullable: true
        homeNumber nullable: true
        workNumber nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'applicant_seq']
        tablePerHierarchy false
        dateOfBirth sqlType: 'date'
        autowire true
    }

    String getEasyVisaIdPrefix() {
        DomainUtils.EVID_APPLICANT_PREFIX
    }

    String getSequenceName() {
        'client_ev_id_seq'
    }

    String getName() {
        "${profile?.firstName} ${profile?.lastName}"
    }

    Address getHome() {
        profile.address
    }

    Applicant copy() {
        if (user) {
            return this
        }
        Applicant copy = new Applicant()
        copy.dateOfBirth = dateOfBirth
        copy.mobileNumber = mobileNumber
        copy.homeNumber = homeNumber
        copy.workNumber = workNumber
        copy.inviteApplicant = inviteApplicant
        copy.profile = profile.copy()
        copy
    }

}
