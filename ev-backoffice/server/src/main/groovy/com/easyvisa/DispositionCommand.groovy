package com.easyvisa

import com.easyvisa.utils.ExceptionUtils

class DispositionCommand extends EasyVisaSystemMessageCommand {

    Long representativeId
    Long organizationId
    Boolean approve
    String rejectionMailMessage
    String rejectionMailSubject

    @Override
    String getSortFieldName() {
        String fieldName
        switch (sort) {
            case 'client': fieldName = 'package.title'; break
            case 'date': fieldName = 'dateCreated'; break
            default: fieldName = 'dateCreated'
        }
        fieldName
    }

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
