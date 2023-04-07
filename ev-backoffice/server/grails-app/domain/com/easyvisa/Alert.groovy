package com.easyvisa

import groovy.transform.ToString

@ToString(includes = 'id', includeSuperProperties=true, includeNames = true, includePackage = false)
class Alert extends EvSystemMessage {

    static constraints = {
        processRequest nullable: true
    }

    static mapping = {
        tablePerHierarchy false
    }

    User recipient
    ProcessRequest processRequest

}
