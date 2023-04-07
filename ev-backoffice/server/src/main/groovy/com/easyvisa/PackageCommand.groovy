package com.easyvisa

import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.model.ApplicantType

class PackageCommand implements grails.validation.Validateable {

    List<ApplicantCommand> applicants
    Long representativeId
    PackageStatus status
    BigDecimal owed
    Long organizationId
    Boolean skipReminders

    Organization getOrganization() {
        Organization.get(organizationId)
    }

    List<ApplicantCommand> getPetitioners() {
        applicants.findAll { it.applicantType == ApplicantType.Petitioner.uiValue }
    }
}