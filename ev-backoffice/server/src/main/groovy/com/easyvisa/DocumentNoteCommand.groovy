package com.easyvisa

import com.easyvisa.document.DocumentNote
import com.easyvisa.enums.DocumentNoteType

class DocumentNoteCommand implements grails.validation.Validateable {

    Long packageId
    DocumentNoteType documentNoteType
    String subject

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    DocumentNote getDocumentNote(User currentUser) {
        DocumentNote documentNote = new DocumentNote(
                aPackage: this.easyVisaPackage,
                subject: this.subject,
                documentNoteType: this.documentNoteType,
                creator: currentUser.profile,
                createdBy: currentUser,
                updatedBy: currentUser
        );
        return documentNote;
    }
}
