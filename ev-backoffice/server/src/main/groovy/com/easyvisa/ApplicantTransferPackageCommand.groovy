package com.easyvisa

class ApplicantTransferPackageCommand implements grails.validation.Validateable {

    Long packageId
    Long representativeId
    Long organizationId

    Package getPackage() {
        Package.get(packageId)
    }

    LegalRepresentative getRepresentative() {
        LegalRepresentative.get(representativeId)
    }

    Organization getOrganization() {
        Organization.get(organizationId)
    }

}
