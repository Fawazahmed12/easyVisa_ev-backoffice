package com.easyvisa

import com.easyvisa.enums.NotificationType
import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class PackageReminder {

    NotificationType notificationType
    Date lastSent
    Boolean stopped = Boolean.FALSE
    Package aPackage

    Date dateCreated
    Date lastUpdated

    static mapping = {
        id generator: 'native', params: [sequence: 'package_reminder_id_seq']
    }

}
