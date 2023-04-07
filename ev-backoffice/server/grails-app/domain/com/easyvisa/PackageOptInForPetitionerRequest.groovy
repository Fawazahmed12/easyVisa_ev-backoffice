package com.easyvisa


import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class PackageOptInForPetitionerRequest extends ProcessRequest {

    Package aPackage
    Petitioner petitioner

    ProcessRequest acceptRequest() {
        processService.acceptPackageOptInForPetitionerRequest(this)
    }

    ProcessRequest denyRequest() {
        processService.denyPackageOptInForPetitionerRequest(this)
    }
}
