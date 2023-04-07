package com.easyvisa

import grails.databinding.BindingFormat

class ApplicantProfileCommand implements grails.validation.Validateable {

    String firstName
    String middleName
    String lastName

    @BindingFormat('MM-dd-yyyy')
    Date dateOfBirth
    Address homeAddress
    String mobileNumber
    String homeNumber
    String workNumber
    String email
    String username
    Long id

    String getFullName() {
        "${firstName ?: ''} ${middleName ?: ''} ${lastName ?: ''}"
    }

}