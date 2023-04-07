package com.easyvisa

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'authority', includeNames = true, includePackage = false)
class Role implements Serializable {

    private static final long serialVersionUID = 1

    final static String ATTORNEY = 'ROLE_ATTORNEY'
    final static String EV = 'ROLE_EV'
    final static String USER = 'ROLE_USER'
    final static String EMPLOYEE = 'ROLE_EMPLOYEE'
    final static String OWNER = 'ROLE_OWNER'


    String authority

    Role(String authority) {
        this()
        this.authority = authority
    }

    static constraints = {
        authority blank: false, unique: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'role_id_seq']
        cache true
    }
}
