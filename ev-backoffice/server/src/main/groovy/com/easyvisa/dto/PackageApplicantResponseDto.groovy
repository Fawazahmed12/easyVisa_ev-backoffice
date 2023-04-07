package com.easyvisa.dto

import com.easyvisa.Applicant
import com.easyvisa.ImmigrationBenefit
import com.easyvisa.Petitioner
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.ImmigrationBenefitGroups
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.questionnaire.model.ApplicantType
import groovy.transform.CompileStatic

@CompileStatic
class PackageApplicantResponseDto {

    PackageApplicantResponseDto(Petitioner petitioner) {
        citizenshipStatus = petitioner.citizenshipStatus
        optIn = petitioner.optIn
        applicant = petitioner.applicant
        applicantType = ApplicantType.Petitioner.uiValue
        inviteApplicant = petitioner.applicant.inviteApplicant
    }

    PackageApplicantResponseDto(ImmigrationBenefit immigrationBenefit) {
        fee = immigrationBenefit.fee
        category = immigrationBenefit.category
        inviteApplicant = immigrationBenefit.applicant.inviteApplicant
        citizenshipStatus = immigrationBenefit.citizenshipStatus
        optIn = immigrationBenefit.optIn
        applicant = immigrationBenefit.applicant
        if (immigrationBenefit.direct) {
            applicantType = immigrationBenefit.category.detectBeneficiaryType().uiValue
        } else {
            applicantType = ApplicantType.Derivative_Beneficiary.uiValue
        }
        relationshipToPrincipal = immigrationBenefit.relationshipToPrincipal
    }

    BigDecimal fee
    ImmigrationBenefitCategory category
    Boolean inviteApplicant
    CitizenshipStatus citizenshipStatus
    ProcessRequestState optIn
    Applicant applicant
    String applicantType
    String relationshipToPrincipal

}
