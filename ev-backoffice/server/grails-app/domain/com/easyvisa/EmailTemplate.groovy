package com.easyvisa

import com.easyvisa.enums.EmailTemplateType
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class EmailTemplate {

    String subject
    String content
    EmailTemplateType templateType
    LegalRepresentative attorney
    Organization organization
    Boolean isFragment = Boolean.FALSE
    EmailPreference preference
    Date dateCreated
    Date lastUpdated

    static constraints = {
        organization nullable:true
        attorney nullable:true
        subject nullable:true
        preference nullable:true
    }

    static mapping = {
        content sqlType: 'text'
        id generator: 'native', params: [sequence: 'email_template_id_seq']
    }

    String getHtmlContent() {
        content.replaceAll("\n", '<br/>')
    }

}

