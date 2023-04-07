package com.easyvisa.document

import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.enums.ReceivedDocumentType

class ReceivedDocument extends BaseDocument {

    ReceivedDocumentType receivedDocumentType
    Date receivedDate
    Boolean isApproved

    static constraints = {
        receivedDate nullable: true;
        isApproved nullable: true;
    }

    @Override
    BaseDocument copy(Package newPackage, Applicant newApplicant) {
        ReceivedDocument newDoc = new ReceivedDocument()
        copy(newDoc, newPackage, newApplicant)
        newDoc.receivedDocumentType = receivedDocumentType
        newDoc.receivedDate = receivedDate
        newDoc.isApproved = isApproved
        newDoc
    }
}
