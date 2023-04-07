package com.easyvisa.document

import com.easyvisa.Applicant
import com.easyvisa.EasyVisaFile
import com.easyvisa.Package
import com.easyvisa.User
import com.easyvisa.enums.DocumentType

class DocumentAttachment {

    BaseDocument documentReference // It can be either RequiredDocument or SignedDocument or ReceivedDocuemnt
    DocumentType documentType
    EasyVisaFile file

    Boolean isRead = Boolean.FALSE
    Boolean isApproved
    Date dispositionDate
    User dispositionBy
    String rejectionMailSubject
    String rejectionMailMessage

    Date dateCreated
    Date lastUpdated
    User createdBy
    User updatedBy

    static constraints = {
        isApproved nullable: true
        dispositionDate nullable: true
        dispositionBy nullable: true
        rejectionMailSubject nullable: true
        rejectionMailMessage nullable: true
    }

    static mapping = {
        id generator: 'native', params: [sequence: 'document_attachment_id_seq']
    }

    DocumentAttachment copy() {
        DocumentAttachment copy = new DocumentAttachment()
        copy.documentType = documentType
        copy.isRead = isRead
        copy.isApproved = isApproved
        copy.dispositionDate = dispositionDate
        copy.dispositionBy = dispositionBy
        copy.rejectionMailSubject = rejectionMailSubject
        copy.rejectionMailMessage = rejectionMailMessage
        copy.dateCreated = dateCreated
        copy.lastUpdated = lastUpdated
        copy.createdBy = createdBy
        copy.updatedBy = updatedBy
        copy
    }
}
