package com.easyvisa

import com.easyvisa.enums.PracticeArea
import com.easyvisa.enums.RegistrationStatus

class AttorneyCommand extends EmployeeCommand {

    String language
    String mobilePhone
    String email
    String lastName
    String middleName
    String firstName
    String password
    String username
    String faxNumber
    String officePhone
    Set<PracticeArea> practiceAreas
    Address officeAddress
    String facebookUrl
    String twitterUrl
    String linkedinUrl
    String websiteUrl
    String youtubeUrl
    RegistrationStatus registrationStatus
    List<FeeScheduleCommand> feeSchedule

    static List getAttorneyFields() {
        ['practiceAreas',
         'faxNumber',
         'officePhone',
         'mobilePhone',
         'youtubeUrl',
         'websiteUrl',
         'linkedinUrl',
         'twitterUrl',
         'facebookUrl']
    }
}
