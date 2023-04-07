package com.easyvisa

import com.easyvisa.enums.EmailTemplateType
import grails.validation.Validateable

class FindEmailCommand implements Validateable {

    Long packageId
    EmailTemplateType templateType
    Long representativeId

    Package getPackageObj() {
        if (packageId) {
            Package.get(packageId)
        }
    }

    LegalRepresentative getRepresentative() {
        if (representativeId) {
            LegalRepresentative.get(representativeId)
        }
    }
}
