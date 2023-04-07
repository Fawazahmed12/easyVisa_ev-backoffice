package com.easyvisa

import com.easyvisa.enums.EasyVisaSystemMessageType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
abstract class EvSystemMessage {

    /**
     * Name for EasyVisa Source for alerts
     */
    public static final String EASYVISA_SOURCE = 'EasyVisa'

    static constraints = {
        source nullable: true
        body nullable: true
        messageType nullable: true
    }

    static mapping = {
        body sqlType: 'text'
        tablePerHierarchy false
        id generator: 'native', params: [sequence: 'ev_system_message_id_seq']
    }

    String subject
    String source
    String body

    Boolean isRead = Boolean.FALSE
    Boolean isStarred = Boolean.FALSE
    EasyVisaSystemMessageType messageType

    Date dateCreated
    Date lastUpdated

}
