package com.easyvisa

import com.easyvisa.enums.Language
import com.easyvisa.enums.PracticeArea

class OrganizationCommand implements grails.validation.Validateable {

    String name
    String summary
    String awards
    String experience
    List<Language> languages
    Address officeAddress
    String officePhone
    String mobilePhone
    String faxNumber
    String email
    String facebookUrl
    String twitterUrl
    String linkedinUrl
    String websiteUrl
    String youtubeUrl
    Set<PracticeArea> practiceAreas
    List<WorkingHourCommand> workingHours
    List<String> rosterNames
    Long yearFounded
}
