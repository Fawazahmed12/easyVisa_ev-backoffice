package com.easyvisa

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class UserDevice {

    User user
    String userAgent

    Date dateCreated
    Date lastUpdated

    static mapping = {
        table 'ev_user_device'
        id generator: 'native', params: [sequence: 'ev_user_device_id_seq']
    }

}
