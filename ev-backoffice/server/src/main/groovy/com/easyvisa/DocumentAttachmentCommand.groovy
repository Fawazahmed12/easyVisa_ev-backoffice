package com.easyvisa

import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.RequiredDocument
import com.easyvisa.document.SentDocument
import com.easyvisa.enums.DocumentType

class DocumentAttachmentCommand implements grails.validation.Validateable {

    Long packageId
    Long applicantId
    DocumentType documentType
    String attachmentRefId // It can be either documentId or formId or ReceivedDocument

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    Applicant getApplicant() {
        applicantId ? Applicant.get(applicantId) : null
    }


    RequiredDocument getRequiredDocument() {
        RequiredDocument requiredDocument = new RequiredDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                documentId: this.attachmentRefId
        );
        return requiredDocument;
    }

    SentDocument getSentDocument() {
        SentDocument sentDocument = new SentDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                formId: this.attachmentRefId
        );
        return sentDocument;
    }

    ReceivedDocument getReceivedDocument() {
        ReceivedDocument receivedDocument = new ReceivedDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                receivedDocumentType: this.attachmentRefId
        );
        return receivedDocument;
    }
}
