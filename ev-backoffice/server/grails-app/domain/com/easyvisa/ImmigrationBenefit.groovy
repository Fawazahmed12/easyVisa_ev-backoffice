package com.easyvisa

import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.enums.ProcessRequestState
import com.easyvisa.enums.RelationshipType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class ImmigrationBenefit {

    ImmigrationBenefitCategory category
    Applicant applicant
    RelationshipType relationshipToPrincipal
    List<AccountTransaction> applicantTransactions = []
    BigDecimal perApplicantFee
    Boolean paid = Boolean.FALSE
    BigDecimal fee
    Boolean direct
    Long sortPosition
    ProcessRequestState optIn = ProcessRequestState.PENDING
    CitizenshipStatus citizenshipStatus

    Date dateCreated
    Date lastUpdated

    static constraints = {
        direct nullable:true
        perApplicantFee nullable:true
        fee nullable:true
        sortPosition nullable:true
        citizenshipStatus nullable:true
        //TODO:uncomment when UI is ready to provide relationship value
        relationshipToPrincipal nullable:true/*, validator: { relationshipToPrincipal, benefit, errors ->
            if (!benefit.direct && !relationshipToPrincipal) {
                errors.rejectValue('relationshipToPrincipal', 'package.relationship.to.principle')
            }
        }*/
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'immigration_benefit_id_seq']
        optIn defaultValue:"'PENDING'"
    }

    static hasMany = [applicantTransactions:AccountTransaction]

    BigDecimal getFee() {
        fee ?: 0
    }

    ImmigrationBenefit copy() {
        ImmigrationBenefit copy = new ImmigrationBenefit()
        copy.category = category
        copy.applicant = applicant.copy()
        copy.relationshipToPrincipal = relationshipToPrincipal
        copy.perApplicantFee = perApplicantFee
        copy.paid = paid
        copy.direct = direct
        copy.sortPosition = sortPosition
        copy.optIn = optIn
        copy.citizenshipStatus = citizenshipStatus
        copy
    }

}
