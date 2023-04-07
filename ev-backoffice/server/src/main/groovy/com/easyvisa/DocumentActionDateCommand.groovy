package com.easyvisa

import com.easyvisa.document.ReceivedDocument
import com.easyvisa.document.SentDocument
import grails.databinding.BindingFormat

class DocumentActionDateCommand extends DocumentAttachmentCommand {

    @BindingFormat('MM-dd-yyyy')
    Date actionDate

    SentDocument getSentDocument() {
        SentDocument sentDocument = new SentDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                formId: this.attachmentRefId,
                sentDate: this.actionDate
        );
        return sentDocument;
    }

    ReceivedDocument getReceivedDocument() {
        ReceivedDocument receivedDocument = new ReceivedDocument(
                aPackage: this.easyVisaPackage,
                applicant: this.applicant,
                documentType: this.documentType,
                receivedDocumentType: this.attachmentRefId,
                receivedDate: this.actionDate
        );
        return receivedDocument;
    }
}
