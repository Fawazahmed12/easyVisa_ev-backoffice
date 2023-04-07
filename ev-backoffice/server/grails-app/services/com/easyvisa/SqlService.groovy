package com.easyvisa

import com.easyvisa.dto.PaginationResponseDto
import com.easyvisa.dto.QueryDto
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ProcessRequestState
import grails.gorm.transactions.Transactional

/**
 * Service for some methods that are better expressed with SQL rather than hibernate queries.
 */
@Transactional
class SqlService {

    def sessionFactory

    @Transactional
    PaginationResponseDto findPackages(FindPackageCommand findCommand, List applicantIds, List statusList) {
        List result = []
        List count = []
        String sortField = findCommand.sortFieldName
        Boolean filterForApplicants = findCommand.hasApplicantFilters() || applicantIds
        List<Long> searchApplicantIds = []

        if (filterForApplicants) {
            QueryDto applicantQueryDto = new QueryDto('''From applicant a
 join profile p on a.profile_id=p.id
  left join address addr on p.address_id=addr.id''')
            applicantQueryDto.with {
                addAndFragment(findCommand.search, "(p.email ILIKE :search OR p.last_name ILIKE :search OR p.easy_visa_id ILIKE :search OR a.mobile_number ILIKE :search)",
                        ['search': findCommand.search])
                addAndFragment(findCommand.email, "p.email ILIKE :email", ['email': findCommand.email])
                addAndFragment(findCommand.lastName, "p.last_name ILIKE :lastName", ['lastName': findCommand.lastName])
                addAndFragment(findCommand.easyVisaId, "p.easy_visa_id ILIKE :easyVisaId", ['easyVisaId': findCommand.easyVisaId])
                addAndFragment(findCommand.mobileNumber, "a.mobile_number ILIKE :mobileNumber", ['mobileNumber': findCommand.mobileNumber])
                addAndFragment(findCommand.states, "addr.state in (:states)", ['states': QueryDto.listToListOfString(findCommand.states)])
                addAndFragment(findCommand.countries, "addr.country in (:countries)", ['countries': QueryDto.listToListOfString(findCommand.countries)])
                addAndFragment(applicantIds, "a.id in (:applicantIds)", ['applicantIds': applicantIds])
            }
            searchApplicantIds = applicantQueryDto.toSQLQuery(applicantQueryDto.withSelect("a.id"), sessionFactory.currentSession).list()
        }
        if (searchApplicantIds == [] && filterForApplicants) {
            result = []
        } else {
            QueryDto searchQueryDto = new QueryDto('''from (
select distinct p.*,prfl.last_name
from package p
join package_immigration_benefit pib on p.id=pib.package_benefits_id
join employee e on p.attorney_id=e.id
join profile prfl on e.profile_id=prfl.id
join immigration_benefit ib on pib.immigration_benefit_id=ib.id
left join petitioner pet on pet.id=p.petitioner_id''')
            searchQueryDto.with {
                addAndFragment(findCommand.hasApplicantFilters(), '(ib.applicant_id in (:applicantIds) OR pet.applicant_id in (:applicantIds))', ['applicantIds': searchApplicantIds])
                addAndFragment(statusList, "p.status in (:status)", ['status': QueryDto.listToListOfString(statusList)])
                addAndFragment([CitizenshipStatus.ALIEN] == findCommand.petitionerStatus, '(p.petitioner_id is null AND ib.citizenship_status = :citizenshipStatus)', ['citizenshipStatus': CitizenshipStatus.ALIEN.name()])
                addAndFragment(findCommand.petitionerStatus && !findCommand.petitionerStatus?.contains(CitizenshipStatus.ALIEN), '(p.petitioner_id is not null AND pet.citizenship_status in (:citizenshipStatus))', ['citizenshipStatus': findCommand.petitionerStatus.collect{it.name()}])
                addAndFragment(findCommand.petitionerStatus?.contains(CitizenshipStatus.ALIEN) && findCommand.petitionerStatus?.size() > 1, '((p.petitioner_id is null AND ib.citizenship_status = :alien_status) OR (p.petitioner_id is not null AND pet.citizenship_status in (:citizenshipStatus)))', [alien_status: CitizenshipStatus.ALIEN.name(), 'citizenshipStatus': findCommand.petitionerStatus.findAll{it != CitizenshipStatus.ALIEN }.collect{it.name()}])
                addAndFragment(findCommand.benefitCategory, "ib.category in (:benefitCategory)", ['benefitCategory': QueryDto.listToListOfString(findCommand.benefitCategory)])
                addAndFragment(findCommand.isOwed, "p.owed > 0", [:])
                addAndFragment(findCommand.representativeId, 'p.attorney_id = :representativeId', ['representativeId': findCommand.representativeId])
                addAndFragment(findCommand.organizationId, 'p.organization_id = :organizationId', ['organizationId': findCommand.organizationId])
                addAndFragment((findCommand.closedDateEnd && findCommand.closedDateStart), 'p.closed BETWEEN :closedDateStart AND :closedDateEnd', ['closedDateStart': findCommand.closedDateStart, 'closedDateEnd': findCommand.closedDateEnd])
                addAndFragment((findCommand.openedDateStart && findCommand.openedDateEnd), 'p.opened BETWEEN :openedDateStart AND :openedDateEnd', ['openedDateStart': findCommand.openedDateStart, 'openedDateEnd': findCommand.openedDateEnd])
                addAndFragment((findCommand.lastAnsweredOnDateStart && findCommand.lastAnsweredOnDateEnd), 'p.last_active_on BETWEEN :lastAnsweredOnDateStart AND :lastAnsweredOnDateEnd', ['lastAnsweredOnDateStart': findCommand.lastAnsweredOnDateStart, 'lastAnsweredOnDateEnd': findCommand.lastAnsweredOnDateEnd])
                addAndFragment(applicantIds, '((ib.applicant_id in (:applicantIds) and ib.paid = true and ib.opt_in = :accepted) OR (pet.applicant_id in (:applicantIds) and pet.opt_in = :accepted))', ['accepted': ProcessRequestState.ACCEPTED.name(), 'applicantIds': searchApplicantIds])
            }
            //workaround to distinct the result
            searchQueryDto.addAndFragment(true, 'true = true) p', [:])
            if (findCommand.sort == 'status') {
                sortField = "array_position(cast(ARRAY['LEAD', 'BLOCKED', 'OPEN', 'CLOSED', 'TRANSFERRED'] as varchar[]) , p.status)"
            }
            searchQueryDto.setExtraSortFragment('p.id')
            searchQueryDto.setMax(findCommand.max)
            searchQueryDto.setOffset(findCommand.offset)
            searchQueryDto.setSortFragment(sortField)
            searchQueryDto.setDirection(getDirection(findCommand.order))

            result = searchQueryDto.toSQLQuery(searchQueryDto.withSelect("p.*,last_name"), sessionFactory.currentSession).addEntity('p', Package).list()
            count = searchQueryDto.toSQLQuery(searchQueryDto.withCount("count(p.id)"), sessionFactory.currentSession).list()
        }
        new PaginationResponseDto(result: result, totalCount: count ? count.first() : 0)
    }

    @Transactional
    def getApplicantsWhichAreAlsoEmployees() {
        String query = """Select a.id as applicant_id, e.id as emp_id , a.profile_id as profile_id from applicant a join employee e on a.profile_id=e.profile_id"""
        new QueryDto().toSQLQuery(query, sessionFactory.currentSession).list()
    }

    private String getDirection(String direction) {
        direction?.equalsIgnoreCase('DESC') ? 'DESC' : 'ASC'
    }


    @Transactional
    def getAllAttorneyIdsWithTheirActiveOrgDetails() {
        String query = """
    select a.id as attorneyId, oe.id as orgEmpId from employee a
left outer join organization_employee oe
on a.id=oe.employee_id
where oe.status='ACTIVE'
"""
        new QueryDto().toSQLQuery(query, sessionFactory.currentSession).list()
    }

    /**
     * Queries DB sequence for getting next value.
     * @param seqName sequence name
     * @return number
     */
    @Transactional
    Long getNextSequenceId(String seqName) {
        String query = "SELECT nextval('${seqName}')"
        new QueryDto().toSQLQuery(query, sessionFactory.currentSession).uniqueResult() as Long;
    }

    /**
     * Removes package_transfer_request_package
     * @param PackageId sequence name
     * @return The number of entities updated or deleted.
     */
    @Transactional
    int deletePackageTransferRequests(Long packageId) {
        String query = "delete from package_transfer_request_package where package_id = '${packageId}'"
        new QueryDto().toSQLQuery(query, sessionFactory.currentSession).executeUpdate()
    }

    @Transactional
    void dropTableConstraint(String tableName, String constraintName) {
        String dropConstraintQuery = "ALTER TABLE ${tableName} DROP CONSTRAINT ${constraintName};"
        new QueryDto().toSQLQuery(dropConstraintQuery, sessionFactory.currentSession).executeUpdate()
    }

    @Transactional
    void addTableConstraint(String tableName, String constraintName, String fkFieldName, String refTableName) {
        String addConstraintQuery = "ALTER TABLE ${tableName} ADD CONSTRAINT ${constraintName} FOREIGN KEY (${fkFieldName}) REFERENCES ${refTableName}(id);"
        new QueryDto().toSQLQuery(addConstraintQuery, sessionFactory.currentSession).executeUpdate()
    }

    @Transactional
    int deleteDBRecords(String tableName, String fieldName, String fieldValue) {
        String query = "delete from ${tableName} where ${fieldName} = '${fieldValue}'"
        new QueryDto().toSQLQuery(query, sessionFactory.currentSession).executeUpdate()
    }

    @Transactional
    Map<Long, List<Long>> findPendingPackageTransferRequests(List<Package> packages) {
        String packageIds = packages.collect { it.id }.join(', ')
        String query = """select ptrp.package_id, package_transfer_request_packages_id 
                from package_transfer_request_package ptrp 
                where ptrp.package_transfer_request_packages_id in 
                (select pr.id 
                from process_request pr 
                where pr.state = 'PENDING'
                    and pr."class" = 'com.easyvisa.PackageTransferRequest') 
                and package_id in ($packageIds)"""
        List list = new QueryDto().toSQLQuery(query, sessionFactory.currentSession).list()
        Map<Long, List<Long>> result = [:]
        list.each {
            Long packageId = it[0]
            Long requestId = it[1]
            List<Long> requests = result.get(packageId)
            if (requests == null) {
                requests = []
                result.put(packageId, requests)
            }
            requests << requestId
        }
        result
    }
}



