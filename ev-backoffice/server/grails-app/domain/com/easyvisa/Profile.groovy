package com.easyvisa

import com.easyvisa.enums.NotificationType
import com.easyvisa.utils.DomainUtils
import grails.compiler.GrailsCompileStatic
import grails.web.mapping.LinkGenerator
import groovy.transform.ToString
import org.springframework.validation.Errors

@ToString(includes = 'easyVisaId', includeNames = true, includePackage = false)
@GrailsCompileStatic
class Profile {

    transient LinkGenerator grailsLinkGenerator
    transient PackageService packageService
    String easyVisaId
    String firstName
    String middleName
    String lastName
    String practiceName
    String email
    String language
    User user
    EasyVisaFile profilePhoto
    Address address
    Date lastMonthlyCharge
    Date lastMonthlyPayment
    Set<EmailPreference> emailPreferences = []
    BigDecimal maintenanceFee
    BigDecimal cloudStorageFee

    Date dateCreated
    Date lastUpdated

    static transients = ['packageService', 'grailsLinkGenerator']

    static constraints = {
        middleName blank: true, nullable: true
        firstName blank: false, nullable: false
        lastName blank: false, nullable: false
        practiceName blank: false, nullable: true
        user nullable: true, unique: true
        email nullable: true, email: true, validator: { String email, Profile profile, Errors errors ->
            if (email) {
                List existing = Profile.findAllByEmailAndIdNotEqual(email, profile.id)
                if (existing && (profile.easyVisaId && !profile.easyVisaId.startsWith(DomainUtils.EVID_APPLICANT_PREFIX))) {
                    errors.rejectValue('email', 'profile.email.unique')
                }
            }
        }
        language nullable: true
        easyVisaId validator: { String val, Profile obj ->
            final String currentId = obj.getPersistentValue('easyVisaId')
            if (currentId && currentId != val) {
                ['noteditable']
            }
        }
        profilePhoto nullable: true
        address nullable: true
        lastMonthlyCharge nullable: true
        lastMonthlyPayment nullable: true
        maintenanceFee nullable: true
        cloudStorageFee nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'profile_id_seq']
        tablePerHierarchy false
        autowire true
    }

    static mappedBy = [profilePhoto: "none"]

    static hasMany = [emailPreferences: EmailPreference]

    String getFullName() {
        "${firstName ?: ''} ${middleNameExcludedNA(middleName) ?: ''} ${lastName ?: ''}"
    }

    String getTitle() {
        "${lastName}, ${firstName}"
    }

    String getName() {
        "${firstName ?: ''} ${lastName ?: ''}"
    }

    String getProfilePhotoUrl() {
        if (user && profilePhoto) {
            grailsLinkGenerator.link(uri: "/api/public/users/${user.id}/profile-picture/${profilePhoto.lastUpdated.time}", absolute: true)
        }
    }

    Boolean getEmailPreference(NotificationType type) {
        EmailPreference preference = this.emailPreferences.find { it.type == type }
        if (preference) {
            return preference.preference
        }
        Boolean.FALSE
    }

    def beforeUpdate() {
        if (isDirty('firstName') || isDirty('lastName')) {
            Package.withSession {
                Applicant applicant = Applicant.findByProfile(this)
                if (applicant) {
                    packageService.updatePackageTitlesForApplicant(applicant)
                }
            }
        }
    }

    /**
     * Copies profile for non registered Applicants.
     * Uses for transferred packages
     * @return copied object
     */
    Profile copy() {
        Profile copy = new Profile()
        copy.easyVisaId = easyVisaId
        copy.firstName = firstName
        copy.middleName = middleName
        copy.lastName = lastName
        copy.email = email
        copy.language = language
        copy.practiceName = practiceName
        copy
    }

    private String middleNameExcludedNA(String value) {
        return value?.toLowerCase()?.equals("n/a") ? "" : value
    }
}
