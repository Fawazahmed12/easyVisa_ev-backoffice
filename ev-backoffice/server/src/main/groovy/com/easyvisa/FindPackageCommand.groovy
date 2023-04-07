package com.easyvisa

import com.easyvisa.enums.*
import grails.databinding.BindingFormat

class FindPackageCommand extends PaginationCommand implements grails.validation.Validateable {

    List<PackageStatus> status
    List<CitizenshipStatus> petitionerStatus
    @BindingFormat('MM-dd-yyyy')
    Date closedDateStart
    @BindingFormat('MM-dd-yyyy')
    Date closedDateEnd
    @BindingFormat('MM-dd-yyyy')
    Date openedDateStart
    @BindingFormat('MM-dd-yyyy')
    Date openedDateEnd
    @BindingFormat('MM-dd-yyyy')
    Date lastAnsweredOnDateStart
    @BindingFormat('MM-dd-yyyy')
    Date lastAnsweredOnDateEnd
    List<ImmigrationBenefitCategory> benefitCategory
    Boolean isOwed
    List<State> states
    List<Country> countries
    Long representativeId
    String email
    String easyVisaId
    String lastName
    String mobileNumber
    String search
    Long organizationId
    Boolean shrink = Boolean.TRUE

    Boolean hasApplicantFilters() {
        states || countries || email || easyVisaId || lastName || mobileNumber || search
    }

    LegalRepresentative getRepresentative() {
        if (representativeId) {
            LegalRepresentative.get(representativeId)
        }
    }

    Date getClosedDateStart() {
        closedDateStart?.clearTime()
    }

    Date getClosedDateEnd() {
        if (closedDateEnd) {
            (closedDateEnd + 1).clearTime()
        }
    }

    Date getOpenedDateEnd() {
        if (openedDateEnd) {
            (openedDateEnd + 1).clearTime()
        }
    }

    Date getLastAnsweredOnDateEnd() {
        if (lastAnsweredOnDateEnd) {
            (lastAnsweredOnDateEnd + 1).clearTime()
        }
    }

    Date getOpenedDateStart() {
        openedDateStart?.clearTime()
    }

    Date getLastAnsweredOnDateStart() {
        lastAnsweredOnDateStart?.clearTime()
    }

    String getSortFieldName() {
        String fieldName
        switch (sort) {
            case 'status': fieldName = 'status'; break
            case 'applicants': fieldName = 'p.title'; break
            case 'ques': fieldName = 'p.questionnaire_completed_percentage'; break
            case 'lastActive': fieldName = 'p.last_active_on'; break
            case 'owed': fieldName = 'p.owed'; break
            case 'representative': fieldName = 'last_name'; break
            case 'creationDate': fieldName = 'p.date_created'; break
            case 'docs': fieldName = 'p.document_completed_percentage'; break
            default: fieldName = 'p.date_created'
        }
        fieldName
    }

    // EV-1989 : Do not return Closed packages unless explicitly
    // specified by request.
    List<PackageStatus> getStatus() {
        if (!status) {
            return [PackageStatus.BLOCKED, PackageStatus.LEAD, PackageStatus.OPEN]
        } else {
            status
        }
    }

    List<PackageStatus> getApplicantStatus() {
        if (!status) {
            return [PackageStatus.BLOCKED, PackageStatus.OPEN]
        } else {
            status
        }
    }

    void checkSingleListParams(def params) {
        if (status == null && params.status) {
            status = Arrays.asList(params.status)
        }
        if (petitionerStatus == null && params.petitionerStatus) {
            petitionerStatus = Arrays.asList(params.petitionerStatus)
        }
        if (benefitCategory == null && params.benefitCategory) {
            benefitCategory = Arrays.asList(params.benefitCategory)
        }
        if (states == null && params.states) {
            states = Arrays.asList(params.states)
        }
        if (countries == null && params.countries) {
            countries = Arrays.asList(params.countries)
        }
    }
}