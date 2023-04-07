package com.easyvisa


import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class PackageOptInForImmigrationBenefitRequest extends ProcessRequest {

    Package aPackage
    ImmigrationBenefit immigrationBenefit

    ProcessRequest acceptRequest() {
        processService.acceptPackageOptInForImmigrationBenefitRequest(this)
    }

    ProcessRequest denyRequest() {
        processService.denyPackageOptInForImmigrationBenefitRequest(this)
    }
}
