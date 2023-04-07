package com.easyvisa

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
@GrailsCompileStatic
class Tax {

    BigDecimal total
    String avaTaxId
    Address billingAddress

    Date dateCreated

    static mapping = {
        id generator: 'native', params: [sequence: 'tax_id_seq']
    }

}
