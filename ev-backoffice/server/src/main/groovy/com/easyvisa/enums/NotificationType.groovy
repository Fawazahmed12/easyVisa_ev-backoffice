package com.easyvisa.enums

enum NotificationType {

    APPLICANT_DOCUMENT,
    ALERT,
    WARNING,
    APPLICANT_REGISTRATION,
    QUESTIONNAIRE_COMPLETE,
    DOCUMENTATION_COMPLETE,
    QUESTIONNAIRE_INACTIVITY('Questionnaire Inactivity Time'),
    DOCUMENT_PORTAL_INACTIVITY('Document Portal Inactivity Time'),

    PERMANENT_RESIDENCE_CARD('Apply for Permanent Residence Card (K Visa Applicants Only)'),
    MARRIAGE_IN_90_DAYS_OF_ARRIVAL('Get Married within 90 days of Arrival (Fiancé Visa Only)'),
    REMOVAL_CONDITIONS_RESIDENCE('Removal of Conditions on Residence'),

    MARRIAGE_CERTIFICATE('Marriage Certificate (K-1 Only)'),
    WORK_AUTHORIZATION('Work Authorization'),

    APPLY_SOCIAL_SECURITY_CARD('Apply for Social Security Card'),
    RENEW_WORK_AUTHORIZATION('Renew Work Authorization'),
    RENEW_RESIDENCE_CARD('Renew Residence Card'),
    NOTICE_ACTION_LETTERS('Notice of Action Letters'),
    VISA_PAGE_PASSPORT('Visa Page in Passport'),
    ADVANCE_PAROLE('Advance Parole'),
    SOCIAL_SECURITY_CARD('Social Security Card'),
    PAYMENT('Blocked for Non-payment'),
    NOT_SUPPORTED_IMMIGRATION_PROCESS('Blocked for Non-EasyVisa supported immigration process'),

    final String displayName

    NotificationType() {
        this.displayName = null
    }

    NotificationType(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }

    static List<NotificationType> getTaskQueueTypes() {
        [APPLICANT_DOCUMENT, ALERT, WARNING]
    }

    static List<NotificationType> getClientProgressTypes() {
        [APPLICANT_REGISTRATION, QUESTIONNAIRE_COMPLETE, DOCUMENTATION_COMPLETE]
    }

    static List<NotificationType> getClientInactivity() {
        [QUESTIONNAIRE_INACTIVITY, DOCUMENT_PORTAL_INACTIVITY]
    }

    static List<NotificationType> getDeadline() {
        [PERMANENT_RESIDENCE_CARD, MARRIAGE_IN_90_DAYS_OF_ARRIVAL, REMOVAL_CONDITIONS_RESIDENCE]
    }

    static List<NotificationType> getImportantDocuments() {
        [MARRIAGE_CERTIFICATE, WORK_AUTHORIZATION]
    }

    static List<NotificationType> getBlocked() {
        [NOT_SUPPORTED_IMMIGRATION_PROCESS]
    }

}
