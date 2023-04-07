package com.easyvisa

import com.easyvisa.enums.EmailTemplateType

class EmailTemplateCommand implements grails.validation.Validateable {

    List<EmailTemplateType> templateType
    Long representativeId
    Long packageId
    Boolean defaultTemplate = false

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    LegalRepresentative getAttorney() {
        representativeId ? LegalRepresentative.get(representativeId) : null
    }
}
