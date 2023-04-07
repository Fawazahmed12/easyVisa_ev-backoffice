package com.easyvisa

class DeleteTransferredPackagesCommand implements grails.validation.Validateable {

    List<Long> packageIds
    Long organizationId

    List<Package> getPackages() {
        Package.getAll(packageIds)
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }

}
