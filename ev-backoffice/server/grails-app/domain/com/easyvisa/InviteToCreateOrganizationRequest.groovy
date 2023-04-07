package com.easyvisa


import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class InviteToCreateOrganizationRequest extends ProcessRequest {

    LegalRepresentative representative

    ProcessRequest acceptRequest() {
        processService.acceptInvitationToCreateOrganization(this)
    }

    ProcessRequest denyRequest() {
        processService.denyInvitationToCreateOrganization(this)
    }
}
