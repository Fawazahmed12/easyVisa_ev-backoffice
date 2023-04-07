package com.easyvisa


import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.sql.DataSource

@Service
@Transactional
class DuplicateRemovalUtilService {

    DataSource dataSource
    ProfileService profileService
    Sql sql

    @PostConstruct
    void initSql() throws Exception {
        sql = new Sql(dataSource)
    }

    /**
     * We are having multiple Profiles with the same email. To solve this, we need to make an email field as UNIQUE in the database.
     * Before adding any DB changes, first we need to cleanup the existing duplicated Profile records without adding any other side-effects.
     *
     * It is NOT a good idea to remove the duplicated Profiles, as it has many dependents.( Even blessed organization employee Profiles also having duplicate records),
     * If we have a requirement to remove all duplicated Profile entries means then we need to remove all its dependents too. which includes
     * Packages, Applicant, Employee, OrganizatonEmployee, EMailPrefernces, User, UserRole and the list will go More
     * So removing Profile is NOT a good one. Solution for this is to make a duplicated Profiles with uniq email values.
     * And also now updated the email field in Profile table as UNIQUE ignore case, So hereafter Database itself would not allow us to add duplicate email
     *
     * First find all the Profiles and create one mapper(emailToProfileMapper) using email, which is used to check the existance of our updated newEmail
     * Then find all the email duplicated Profiles. And then iterate all the email duplicated Profiles
     * Using 'generateUniqEmailWithAppender' method we are making the duplicate email as unique one.
     * i.e Appending an incrementer count to the username of an email with plus sign and check its availability using 'emailToProfileMapper'.
     * If we have found any existance of new email with appended count, then repeat the incrementer and check the same until get the non existant email.
     * Then update the Profile using new unique email by a service method from ProfileService
     *
     * Example: If we are having multiple Profiles with the email like: john.dee@gmail.com
     * Then through the above approach we are adding an incrementer count to the above email like this,
     * 1. john.dee+1@gmail.com
     * 2. john.dee+2@gmail.com
     * */
    void removeDuplicateProfileEntries() {
        Map<String, Integer> duplicateEmailToIndexMapper = [:]
        List updatedProfiles = [];
        Integer duplicateCount = 0;
        List<GroovyRowResult> profileEmails = this.getAllEmailsFromProfile();
        Map<String, GroovyRowResult> emailToProfileMapper = [:]
        profileEmails.each { GroovyRowResult profileData ->
            String currentEmail = profileData.email.toLowerCase()
            emailToProfileMapper[currentEmail] = profileData;
        }

        List<GroovyRowResult> duplicateProfiles = this.getDuplicateProfilesWithSameEmail();
        duplicateProfiles.each { GroovyRowResult profileData ->
            String currentEmail = profileData.email.toLowerCase()
            Integer matchedCount = duplicateEmailToIndexMapper[currentEmail];
            if (matchedCount != null) {
                def emailAppenderData = this.generateUniqEmailWithAppender(profileData.email, matchedCount, emailToProfileMapper);
                String newEmail = emailAppenderData['newEmail'];
                System.out.println("${profileData.email} <--> ${newEmail}")
                updatedProfiles.push([index: ++duplicateCount, profileId: profileData.id, easyVisaId: profileData.easy_visa_id,
                                      currentEmail: profileData.email, newEmail: newEmail])
                duplicateEmailToIndexMapper[currentEmail] = emailAppenderData['matchedCount'];
            } else {
                duplicateEmailToIndexMapper[currentEmail] = 0; //default first item count
            }
        }
        System.out.println("")
        updatedProfiles.each { profileData ->
            System.out.println("Updated --> Id:${profileData.profileId} ${profileData.currentEmail} with ${profileData.newEmail}")
            this.profileService.updateProfileEmail(Profile.findById(profileData.profileId as Long), profileData.newEmail, true);
        }
    }


    private Map generateUniqEmailWithAppender(String currentEmail, Integer matchedCount, Map<String, GroovyRowResult> emailToProfileMapper) {
        String[] emailFields = currentEmail.split('@');
        String newEmail = currentEmail;
        while (emailToProfileMapper[newEmail.toLowerCase()] != null) {
            matchedCount++;
            newEmail = "${emailFields[0]}+${matchedCount}@${emailFields[1]}"
            if (emailFields[0].contains('+')) {
                newEmail = "${emailFields[0]}${matchedCount}@${emailFields[1]}"
            }
        }
        return ['newEmail': newEmail, 'matchedCount': matchedCount];
    }


    /**
     *  This query finds duplicate email appears in multiple Profiles
     * */
    List<GroovyRowResult> getDuplicateProfilesWithSameEmail() {
        String query = """select ou.* from profile ou
                            where email <> '' AND (select count(*) from profile inr
                            where LOWER(inr.email) = LOWER(ou.email)) > 1
                            order by ou.email
                       """
        List<GroovyRowResult> result = sql.rows(query)
        return result
    }


    List<GroovyRowResult> getAllEmailsFromProfile() {
        String query = """select * from profile where email <> '';"""
        List<GroovyRowResult> result = sql.rows(query)
        return result
    }
}
