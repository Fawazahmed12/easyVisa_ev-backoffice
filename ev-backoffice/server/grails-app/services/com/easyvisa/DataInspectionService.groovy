package com.easyvisa

import com.easyvisa.enums.EmployeePosition
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.enums.OrganizationType
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import org.springframework.util.ResourceUtils

@Transactional
class DataInspectionService {

    SqlService sqlService

    void logOrphanWarnings() {
        List<Warning> orphanWarnings = Warning.findAllByAPackageIsNull()
        log.info "Orphan warnings - ${orphanWarnings*.id}"
    }

    void logUnRegisteredApplicants() {
        List<Applicant> applicantWithBenefitCounts = ImmigrationBenefit.createCriteria().list() {
            projections {
                count('id')
                groupProperty('applicant')

            }
            'applicant' {
                'profile' {
                    isNull('user')
                }
            }
        }
        log.info "Unregistered Applicants with more than 1 Package"
        log.info applicantWithBenefitCounts.find { it.first() > 1 }.toString()
    }

    void validateOrganizations() {
        List results = OrganizationEmployee.createCriteria().list {
            createAlias("organization", 'o')
            projections {
                property('o.id')
                property('position')
                property('employee')
                property('o.organizationType')
            }
            eq('status', EmployeeStatus.ACTIVE)
        }

        Map organizationsWithEmployees = results.inject([:], { result, itr ->
            OrganizationType orgType = itr.last() as OrganizationType
            def orgList = result[orgType] ?: []
            orgList << [position: itr[1] as EmployeePosition, employee: itr[2], orgId: itr[0] as Long]
            result[orgType] = orgList
            result
        })
        validateSoloPractices(organizationsWithEmployees[OrganizationType.SOLO_PRACTICE])
        validateLawFirms(organizationsWithEmployees[OrganizationType.LAW_FIRM])
    }

    void validateSoloPractices(List orgs) {
        List moreThanOneMemberOrgs = []
        List withoutPartnerOrgs = []
        orgs?.groupBy { it['orgId'] }?.each {
            List orgEntries = it.value
            Long orgId = it.key

            if (orgEntries.size() > 1) {
                moreThanOneMemberOrgs << orgId
            } else {
                if (orgEntries.first()['position'] != EmployeePosition.PARTNER) {
                    withoutPartnerOrgs << orgId
                }
            }
        }
        log.info "Solo practice with more than 1 members - ${moreThanOneMemberOrgs}"
        log.info "Solo practice without partner - ${withoutPartnerOrgs}"
    }

    void validateLawFirms(List orgs) {
        List lessThanOneMemberOrgs = []
        List withoutPartnerOrgs = []
        orgs?.groupBy { it['orgId'] }?.each {
            List orgEntries = it.value
            Long orgId = it.key
            if (orgEntries.size() < 2) {
                lessThanOneMemberOrgs << orgId
            } else {
                if (orgEntries.first()['position'] != EmployeePosition.PARTNER) {
                    withoutPartnerOrgs << orgId
                }
            }

        }
        log.info "Law firms with less than 2 members - ${lessThanOneMemberOrgs}"
        log.info "Law firms without partner - ${withoutPartnerOrgs}"
    }

    void validateNoEmployeesAreApplicants() {

        List result = sqlService.getApplicantsWhichAreAlsoEmployees()
        List formattedResult = result.collect {
            ['applicantId': it[0], 'employeeId': it[1], 'profileId': it[2]]
        }
        log.info "Applicants which are also employess -> ${formattedResult}"
    }

    def validateAttorneys(){
       Map attorneyIdWithOrgEmployees =  (sqlService.getAllAttorneyIdsWithTheirActiveOrgDetails()?:[]).inject([:]) {acc, i->
            def k = i.first(); def v = i.last()
           acc[k]=acc[k]?acc[k]<<v:[v]
            acc
        }
        List employeesWithMoreThanOneOrg=[]
        List attorneysWithMoreThanTwoOrgs=[]
        attorneyIdWithOrgEmployees.each{k, v->
            Employee emp = Employee.get(k)
            if(emp.isLegalRepresentative()){
                if(v.size()>2){
                    attorneysWithMoreThanTwoOrgs << (emp as LegalRepresentative)
                }
            }
            else if(v.size()>1){
                employeesWithMoreThanOneOrg << emp
            }
        }
        log.info "Employees with more than one orgs -> ${employeesWithMoreThanOneOrg*.id}"
        log.info "Attorneys with more than two active orgs -> ${attorneysWithMoreThanTwoOrgs*.id}"
    }

    void validateData(){
        log.info "*****************************Data Validation START******************************************"
        logOrphanWarnings()
        logUnRegisteredApplicants()
        validateOrganizations()
        validateNoEmployeesAreApplicants()
        validateAttorneys()
        log.info "*****************************Data Validation END********************************************"
    }
}
