package com.easyvisa

import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.Language
import com.easyvisa.enums.OrganizationType
import com.easyvisa.enums.PracticeArea
import grails.core.GrailsApplication
import grails.web.mapping.LinkGenerator

@SuppressWarnings('GetterMethodCouldBeProperty')
class Organization {

    transient LinkGenerator grailsLinkGenerator
    transient GrailsApplication grailsApplication

    String name
    String profileSummary
    String awards
    String experience
    OrganizationType organizationType
    EasyVisaFile logoFile
    Address address
    //TODO: Add different table for time schedule

    String officePhone
    String mobilePhone
    String faxNumber
    String email
    String facebookUrl
    String linkedinUrl
    String twitterUrl
    String youtubeUrl
    String websiteUrl
    Long yearFounded
    String easyVisaId

    static hasMany = [workingHours   : WorkingHour,
                      spokenLanguages: Language,
                      practiceAreas  : PracticeArea,
                      rosterNames    : String]

    static constraints = {
        email nullable: true
        profileSummary nullable: true
        awards nullable: true
        experience nullable: true
        officePhone nullable: true
        faxNumber nullable: true
        mobilePhone nullable: true
        facebookUrl nullable: true
        linkedinUrl nullable: true
        twitterUrl nullable: true
        youtubeUrl nullable: true
        websiteUrl nullable: true
        yearFounded nullable: true
        spokenLanguages nullable: true
        practiceAreas nullable: true
        address nullable: true
        logoFile nullable: true
        rosterNames nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'organization_id_seq']
        autowire true
    }

    List<Employee> getEmployees() {
        OrganizationEmployee.createCriteria().list {
            projections {
                property('employee')
            }
            eq('organization', this)
        } as List<Employee>
    }

    List<Employee> getActiveEmployees() {
        OrganizationEmployee.createCriteria().list {
            projections {
                property('employee')
            }
            eq('organization', this)
            eq('status', EmployeeStatus.ACTIVE)
        } as List<Employee>
    }


    String getProfilePhotoUrl() {
        if (logoFile) {
            grailsLinkGenerator.link(uri: "/api/public/organizations/${id}/profile-picture/${logoFile.lastUpdated.time}", absolute: true)
        }
    }

    Boolean isSoloPractice() {
        organizationType == OrganizationType.SOLO_PRACTICE
    }

    Boolean isLawFirm() {
        organizationType == OrganizationType.LAW_FIRM
    }

    Boolean getIsBlessed(){
        easyVisaId == grailsApplication.config.easyvisa.blessedOrganizationEVId
    }

    String memberOf() {
        return isSoloPractice() ? OrganizationType.SOLO_PRACTICE.displayName : this.name
    }

}
