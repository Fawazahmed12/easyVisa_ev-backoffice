package com.easyvisa

import com.easyvisa.utils.ExceptionUtils

class WarningCommand extends EasyVisaSystemMessageCommand {

    Long representativeId
    Long organizationId

    Organization findOrganization() {
        if (organizationId == null) {
            throw ExceptionUtils.createUnProcessableDataException('default.blank.message', null, ['organizationId'])
        }
        Organization organization = Organization.get(organizationId)
        if (!organization) {
            throw ExceptionUtils.createUnProcessableDataException('organization.not.found', null, [organizationId])
        }
        organization
    }

    LegalRepresentative findAttorney() {
        if (representativeId == null) {
            return null
        }
        LegalRepresentative result = LegalRepresentative.get(representativeId)
        if (!result) {
            throw ExceptionUtils.createUnProcessableDataException('employee.not.found', null , [representativeId])
        }
        result
    }

}
