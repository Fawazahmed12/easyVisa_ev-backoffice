package com.easyvisa

import com.easyvisa.enums.EmailTemplateType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'id')
@ToString(includes = 'id', includeNames = true, includePackage = false)
class Email {

    String subject
    String content
    Package aPackage
    LegalRepresentative attorney
    EmailTemplateType templateType
    Date dateCreated
    Date lastUpdated

    static constraints = {
        attorney nullable:true
        aPackage nullable:true
        templateType unique:'aPackage'
    }

    static mapping = {
        content sqlType:'text'
        id generator: 'native', params: [sequence: 'email_id_seq']
    }

    Organization getOrganization() {
        aPackage?.organization
    }

    String getHtmlContent() {
        content.replaceAll("\n", '<br/>')
    }
}

