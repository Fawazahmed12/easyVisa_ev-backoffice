package com.easyvisa.document

import com.easyvisa.Applicant
import com.easyvisa.Package

class RequiredDocument extends BaseDocument {

    String documentId
    Boolean isApproved

    static mapping = {
        isApproved nullable: true
    }

    @Override
    BaseDocument copy(Package newPackage, Applicant newApplicant) {
        RequiredDocument newDoc = new RequiredDocument()
        copy(newDoc, newPackage, newApplicant)
        newDoc.documentId = documentId
        newDoc.isApproved = isApproved
        newDoc
    }
}
