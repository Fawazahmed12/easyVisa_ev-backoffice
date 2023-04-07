package com.easyvisa

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class JoinOrganizationRequest extends ProcessRequest {

    Organization organization
    Employee employee

    ProcessRequest acceptRequest() {
        processService.validateAndAcceptJoinOrganizationRequest(this)
    }

    ProcessRequest denyRequest() {
        processService.validateAndDenyJoinOrganizationRequest(this)
    }
}
