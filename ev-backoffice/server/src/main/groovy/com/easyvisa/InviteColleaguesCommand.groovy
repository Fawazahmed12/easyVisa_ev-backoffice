package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class InviteColleaguesCommand implements grails.validation.Validateable {
    String content
    Long representativeId
    EmailTemplateType templateType
    String subject
    String emails

    LegalRepresentative getRepresentative() {
        LegalRepresentative.get(representativeId)
    }

    List<String> getSplitEmails(){
        return this.emails.split("[,; ]+");
    }
}
