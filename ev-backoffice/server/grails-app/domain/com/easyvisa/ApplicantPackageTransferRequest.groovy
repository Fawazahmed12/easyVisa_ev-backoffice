package com.easyvisa

import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
@CompileStatic
class ApplicantPackageTransferRequest extends ProcessRequest {

    LegalRepresentative representative
    Package aPackage
    LegalRepresentative oldAssignee
    Organization representativeOrganization
    Organization oldOrganization

    ProcessRequest acceptRequest() {
        processService.acceptHandleApplicantPackageTransferRequest(this)
    }

    ProcessRequest denyRequest() {
        processService.declineHandleApplicantPackageTransferRequest(this)
    }

}
