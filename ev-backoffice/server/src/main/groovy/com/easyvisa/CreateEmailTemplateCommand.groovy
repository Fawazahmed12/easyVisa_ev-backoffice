package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class CreateEmailTemplateCommand implements grails.validation.Validateable {

    EmailTemplateType templateType
    Long representativeId
    Long packageId
    String content
    String subject

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    LegalRepresentative getAttorney() {
        representativeId ? LegalRepresentative.get(representativeId) : null
    }
}
