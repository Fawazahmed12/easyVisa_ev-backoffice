package com.easyvisa

import com.easyvisa.enums.AppConfigType
import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class AppConfig {

    AppConfigType type
    String value

    Date dateCreated
    Date lastUpdated

    static mapping = {
        id generator: 'native', params: [sequence: 'app_config_id_seq']
        version false
    }

}
