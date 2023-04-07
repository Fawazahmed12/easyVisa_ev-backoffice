package com.easyvisa

class TransferPackageCommand implements grails.validation.Validateable {

    List<Long> packageIds
    Long representativeId
    Long organizationId

    List<Package> getPackages() {
        Package.getAll(packageIds)
    }

    LegalRepresentative getRepresentative() {
        LegalRepresentative.get(representativeId)
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }

}
