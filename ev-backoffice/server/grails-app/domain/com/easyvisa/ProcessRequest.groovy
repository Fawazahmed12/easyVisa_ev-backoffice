package com.easyvisa

import com.easyvisa.enums.ProcessRequestState
import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class ProcessRequest {

    ProcessService processService

    Profile requestedBy
    ProcessRequestState state = ProcessRequestState.PENDING

    Date dateCreated
    Date lastUpdated

    static transients = ['processService']
    static mapping = {
        id generator: 'native', params: [sequence: 'process_request_id_seq']
        autowire true
    }

    ProcessRequest acceptRequest() {
        state = ProcessRequestState.ACCEPTED
        this
    }


    ProcessRequest denyRequest() {
        state = ProcessRequestState.DECLINED
        this
    }
}
