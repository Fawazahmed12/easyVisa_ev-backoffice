package com.easyvisa


import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class PackageTransferRequest extends ProcessRequest {

    LegalRepresentative representative
    List<Package> packages
    LegalRepresentative oldAssignee
    Organization representativeOrganization
    Organization oldOrganization

    Date dateCreated
    Date lastUpdated


    ProcessRequest acceptRequest() {
        processService.handlePackageTransferRequest(this)
    }

    ProcessRequest denyRequest() {
        processService.denyPackageTransferRequest(this)
    }

}
