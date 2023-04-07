package com.easyvisa

import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.Language
import com.easyvisa.utils.DomainUtils
import groovy.transform.ToString

@SuppressWarnings('GetterMethodCouldBeProperty')
@ToString(includeNames = true, includePackage = false)
class Employee implements EasyVisaIdGeneratable {
    //TODO: Add different table for time schedule

    SqlService sqlService

    String officePhone
    String mobilePhone
    String faxNumber
    List<Language> spokenLanguages

    static hasMany = [spokenLanguages: Language, linkedOrganizations: Organization]

    User getUser() {
        profile.user
    }

    static transients = ['sqlService']
    static constraints = {
        officePhone nullable: true
        faxNumber nullable: true
        mobilePhone nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'employee_id_seq']
        tablePerHierarchy false
        autowire true
    }

    String getEasyVisaIdPrefix() {
        DomainUtils.EVID_EMPLOYEE_PREFIX
    }

    String getSequenceName() {
        'employee_ev_id_seq'
    }

    List<Organization> getOrganizations() {
        OrganizationEmployee.createCriteria().list {
            projections {
                property('organization')
            }
            eq('employee', this)
        } as List<Organization>
    }

    List<OrganizationEmployee> getOrganizationEmployees() {
        OrganizationEmployee.createCriteria().list {
            eq('employee', this)
            eq('status', EmployeeStatus.ACTIVE)
        } as List<OrganizationEmployee>
    }

    Boolean isLegalRepresentative() {
        this.instanceOf(LegalRepresentative)
    }

    Address getOfficeAddress() {
        profile.address
    }

}
