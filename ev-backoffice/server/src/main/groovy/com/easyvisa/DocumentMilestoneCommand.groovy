package com.easyvisa

import com.easyvisa.document.DocumentMilestone
import grails.databinding.BindingFormat

class DocumentMilestoneCommand implements grails.validation.Validateable {

    Long packageId
    String milestoneTypeId
    @BindingFormat('MM-dd-yyyy')
    Date milestoneDate

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    DocumentMilestone getDocumentMilestone() {
        DocumentMilestone documentMilestone = new DocumentMilestone(
                aPackage: this.easyVisaPackage,
                milestoneDate: this.milestoneDate,
                milestoneTypeId: this.milestoneTypeId
        );
        return documentMilestone;
    }
}
