package com.easyvisa.document

import com.easyvisa.Applicant
import com.easyvisa.Package

class SentDocument extends BaseDocument {

    String formId
    Date sentDate
    Boolean isApproved

    static constraints = {
        sentDate nullable: true
        isApproved nullable: true
    }

    @Override
    BaseDocument copy(Package newPackage, Applicant newApplicant) {
        SentDocument newDoc = new SentDocument()
        copy(newDoc, newPackage, newApplicant)
        newDoc.formId = formId
        newDoc.sentDate = sentDate
        newDoc.isApproved = isApproved
        newDoc
    }

}
