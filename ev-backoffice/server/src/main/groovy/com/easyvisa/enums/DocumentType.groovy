package com.easyvisa.enums

enum DocumentType {

    REQUIRED_DOCUMENT('REQUIRED_DOCUMENT', 'Documents Required Prior to Application Submission', true),
    DOCUMENT_SENT_TO_US('DOCUMENT_SENT_TO_US', 'Signed Documents Sent To U.S. Government', false),
    DOCUMENT_RECEIVED_FROM_US('DOCUMENT_RECEIVED_FROM_US', 'Documents Received From U.S. Government', false)

    final String displayName
    final String panelName
    final Boolean hasApprovalRequired

    DocumentType(String displayName, String panelName, Boolean hasApprovalRequired) {
        this.displayName = displayName;
        this.panelName = panelName;
        this.hasApprovalRequired = hasApprovalRequired;
    }
}
