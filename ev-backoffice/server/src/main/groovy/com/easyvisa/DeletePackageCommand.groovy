package com.easyvisa

import grails.databinding.BindingFormat

class DeletePackageCommand implements grails.validation.Validateable {

    @BindingFormat('MM-dd-yyyy')
    Date startDate
    @BindingFormat('MM-dd-yyyy')
    Date endDate
    Long organizationId

    Organization getOrganization() {
        Organization.get(organizationId)
    }
}
