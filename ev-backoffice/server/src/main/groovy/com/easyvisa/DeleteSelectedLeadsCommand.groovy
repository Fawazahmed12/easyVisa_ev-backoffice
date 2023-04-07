package com.easyvisa

class DeleteSelectedLeadsCommand implements grails.validation.Validateable {

    List<Long> packageIds
    Long organizationId

    List<Package> getPackages() {
        Package.getAll(packageIds)
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }

}
