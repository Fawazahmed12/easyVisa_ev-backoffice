package com.easyvisa

import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.RequiredDocument
import com.easyvisa.document.SentDocument
import grails.databinding.BindingFormat

class DocumentApprovalCommand extends DocumentAttachmentCommand {

    Boolean isApproved;

    RequiredDocument getRequiredDocument() {
        RequiredDocument requiredDocument = new RequiredDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                documentId: this.attachmentRefId,
                isApproved: this.isApproved
        );
        return requiredDocument;
    }

    ReceivedDocument getReceivedDocument() {
        ReceivedDocument receivedDocument = new ReceivedDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                receivedDocumentType: this.attachmentRefId,
                isApproved: this.isApproved
        );
        return receivedDocument;
    }

    SentDocument getSentDocument() {
        SentDocument sentDocument = new SentDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                formId: this.attachmentRefId,
                isApproved: this.isApproved
        );
        return sentDocument;
    }
}
