package com.easyvisa

import com.easyvisa.enums.Language
import com.easyvisa.enums.PracticeArea

class ProfileCommand implements grails.validation.Validateable {

    String firstName
    String middleName
    String lastName
    String practiceName
    Address officeAddress
    String stateBarNumber
    String uscisOnlineAccountNo

    List<Language> languages
    String mobilePhone
    String email
    String username
    String faxNumber
    String officePhone
    String facebookUrl
    String twitterUrl
    String linkedinUrl
    String websiteUrl
    String youtubeUrl
    Set<PracticeArea> practiceAreas
    String awards
    String experience
    String summary
    List<WorkingHourCommand> workingHours
    List<EducationCommand> education
    List<LicensedRegion> licensedRegions

    List getProfileFields() {
        ['firstName',
         'lastName',
         'middleName',
         'username',
        'practiceName']
    }

    List getEmployeeFields() {
        ['officePhone',
         'mobilePhone',
         'faxNumber',
         'spokenLanguages']
    }

    List getReprsentativeFields() {
        ['awards',
         'experience',
         'facebookUrl',
         'twitterUrl',
         'youtubeUrl',
         'websiteUrl',
         'stateBarNumber',
         'uscisOnlineAccountNo',
         'practiceAreas',
         'summary']
    }
}
