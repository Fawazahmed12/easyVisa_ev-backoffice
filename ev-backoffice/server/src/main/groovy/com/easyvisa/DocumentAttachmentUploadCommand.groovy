package com.easyvisa

import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.RequiredDocument
import com.easyvisa.document.SentDocument
import org.springframework.web.multipart.MultipartFile

class DocumentAttachmentUploadCommand extends DocumentAttachmentCommand {

    MultipartFile attachment

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
