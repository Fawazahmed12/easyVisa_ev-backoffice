package com.easyvisa

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class InviteToOrganizationRequest extends ProcessRequest {

    Organization organization
    Employee employee

    Date dateCreated
    Date lastUpdated

    ProcessRequest acceptRequest() {
        processService.validateAndAcceptInvitationToJoinOrganization(this)
    }

    ProcessRequest denyRequest() {
        processService.validateAndDeclineInvitationToJoinOrganization(this)
    }
}
