package com.easyvisa


import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class AuditLog {

    String username
    String event
    String oldValue
    String newValue

    Date dateCreated

    static mapping = {
        oldValue sqlType: 'text'
        newValue sqlType: 'text'
        id generator: 'native', params: [sequence: 'audit_log_id_seq']
    }

    String getContent() {
        "Event - ${event} , Old value: $oldValue , New value: $newValue"
    }
}
