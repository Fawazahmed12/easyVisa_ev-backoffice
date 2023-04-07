package com.easyvisa

import com.easyvisa.document.RequiredDocument
import grails.databinding.BindingFormat

class USCISEditionDateCommand implements grails.validation.Validateable {
    String formId

    @BindingFormat('MM-dd-yyyy')
    Date editionDate

    @BindingFormat('MM-dd-yyyy')
    Date expirationDate


    UscisEditionDate getUSCISEditionDate(User currentUser, Organization organization) {
        UscisEditionDate uscisEditionDate = new UscisEditionDate(
                formId: this.formId,
                organization: organization,
                editionDate: this.editionDate,
                expirationDate: this.expirationDate,
                createdBy: currentUser,
                updatedBy: currentUser,
                dateCreated: new Date(),
                lastUpdated: new Date()
        );
        return uscisEditionDate;
    }
}
