package com.easyvisa

import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.RelationshipType
import com.easyvisa.questionnaire.model.ApplicantType

class ApplicantCommand implements grails.validation.Validateable {

    ApplicantProfileCommand profile
    CitizenshipStatus citizenshipStatus
    ProcessRequestState optIn = ProcessRequestState.PENDING
    BigDecimal fee
    ImmigrationBenefitCategory benefitCategory
    String applicantType
    Boolean inviteApplicant
    RelationshipType relationshipToPrincipal

    Long getId() {
        profile.id
    }

    Boolean getDirect() {
        ApplicantType type = ApplicantType.getByUiValue(applicantType)
        ApplicantType.beneficiaryTypes.contains(type)
    }
}
