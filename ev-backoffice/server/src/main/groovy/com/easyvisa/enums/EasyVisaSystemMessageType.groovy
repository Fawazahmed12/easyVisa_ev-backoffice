package com.easyvisa.enums

import groovy.transform.CompileStatic

@CompileStatic
enum EasyVisaSystemMessageType {

    //EasyVisa types
    EASYVISA_ALERT('EasyVisa message'), //this just a stub value for checking email preferences

    //Package transfer by an attorney
    PACKAGE_TRANSFER_REQUEST('A legal representative on EasyVisa has requested to transfer package(s)/case(s) to you!!!', '/alert/packageTransferRequest'),
    PACKAGE_TRANSFER_OVERRIDDEN('Package(s) transfer overridden details', '/alert/packagesTransferOverriddenDetails'),
    PACKAGE_TRANSFER_REQUEST_OWNER('Package transfer request', '/alert/packageTransferRequestOwner'),
    PACKAGE_TRANSFER_REQUEST_ADMIN('Package transfer request', '/alert/packageTransferRequestAdmin'),
    PACKAGE_TRANSFER_ACCEPTED_RECIPIENT('Package transfer complete', '/alert/packageTransferAcceptedRecipient', 'Files removed from your caseload'),
    PACKAGE_TRANSFER_ACCEPTED_RECIPIENT_SAME_ORG('Some Packages Were Added to Your Caseload', '/alert/packageTransferAcceptedRecipientSameOrg', 'Files added to your caseload'),
    PACKAGE_TRANSFER_ACCEPTED_OWNER('Package transfer is accepted', '/alert/packageTransferAcceptedOwner'),
    PACKAGE_TRANSFER_ACCEPTED_OWNER_SAME_ORG('Some Packages Were Removed From Your Caseload', '/alert/packageTransferAcceptedOwnerSameOrg', 'Files removed from your caseload'),
    PACKAGE_TRANSFER_REJECTED_RECIPIENT('Package transfer Rejected', '/alert/packageTransferRejectedRecipient'),
    PACKAGE_TRANSFER_REJECTED_OWNER('Package transfer Rejected', '/alert/packageTransferRejectedOwner'),

    //Package transfer by an applicant
    APPLICANT_PACKAGE_TRANSFER_REQUEST('A client on EasyVisa has requested that you become their legal representative', '/alert/packageApplicantTransferRequest'),
    APPLICANT_PACKAGE_TRANSFER_REQUEST_ACCEPTED('Package transfer is accepted', '/alert/packageApplicantTransferAcceptedRequester'),
    APPLICANT_PACKAGE_TRANSFER_REQUEST_DENIED('Package transfer Rejected', '/alert/packageApplicantTransferRejectedRequester'),
    APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP('Applicant Package transfer Request', '/alert/packageApplicantTransferOldAttorney', NotificationType.WARNING),
    APPLICANT_PACKAGE_TRANSFER_REQUEST_OLD_LEGAL_REP_ALERT('Your client, %s, has requested to transfer their package to another representative', '/alert/packageApplicantTransferOldAttorneyAlert'),

    //Packages
    PACKAGE_APPLICANT_DELETION('Client %s Data Deletion', '/alert/packageClientDataDeletion', NotificationType.WARNING),
    PACKAGE_ATTORNEY_CHARGE_FAILED('Per applicant charge was failed', '/alert/packageApplicantChargeFailed', NotificationType.WARNING),
    PACKAGE_ATTORNEY_CHARGE_FAILED_ALERT('Per applicant charge was failed', '/alert/packageApplicantChargeFailed', NotificationType.ALERT),
    PACKAGE_ATTORNEY_LEFT('%s cancelled EasyVisa membership', '/alert/packageAttorneyLeft'),
    PACKAGE_ADDITIONAL_FEE('Package additional fees were sent', '/alert/packageAdditionalFee'),
    PACKAGE_APPLICANT_REGISTERED('Registration Completed', '/alert/applicantRegistered', NotificationType.APPLICANT_REGISTRATION),
    PACKAGE_APPLICANT_DISPOSITION('Document Dispositions', '/alert/applicantPendingDisposition', NotificationType.APPLICANT_DOCUMENT),
    PACKAGE_QUESTIONNAIRE_INACTIVE('Questionnaire Inactivity Time', NotificationType.QUESTIONNAIRE_INACTIVITY),
    PACKAGE_DOCUMENT_PORTAL_INACTIVE('Document Portal Inactivity Time', NotificationType.DOCUMENT_PORTAL_INACTIVITY),
    PACKAGE_PAYMENT('Package payment', NotificationType.PAYMENT),
    PACKAGE_NOT_SUPPORTED_IMMIGRATION_PROCESS('Not supported immigration process', NotificationType.NOT_SUPPORTED_IMMIGRATION_PROCESS),

    //Notification & Reminders
    PERMANENT_RESIDENCE_CARD('Apply for Permanent Residence Card', NotificationType.PERMANENT_RESIDENCE_CARD),
    MARRIAGE_IN_90_DAYS_OF_ARRIVAL('Get Married within 90 days of Arrival', NotificationType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL),
    REMOVAL_CONDITIONS_RESIDENCE('Removal of Conditions on Residence', NotificationType.REMOVAL_CONDITIONS_RESIDENCE),

    MARRIAGE_CERTIFICATE('Marriage Certificate', NotificationType.MARRIAGE_CERTIFICATE),
    WORK_AUTHORIZATION('Work Authorization', NotificationType.WORK_AUTHORIZATION),


    //Request to Join Existing organization
    JOIN_ORGANIZATION_REQUEST('Request to join %s', '/alert/joinOrganizationRequest'),
    JOIN_ORGANIZATION_REQUEST_ACCEPTED_OWNER('Organization Join invite', '/alert/joinOrganizationRequestAcceptedOwner'),
    JOIN_ORGANIZATION_REQUEST_DENIED_OWNER('Organization Join invite', '/alert/joinOrganizationRequestDeniedOwner'),
    JOIN_ORGANIZATION_REQUEST_WITHDRAWN('Organization Join Request - Withdrawn', '/alert/joinOrganizationRequestWithdrawn'),
    JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_ADMIN('Organization Join Request - Not Admin', '/alert/joinOrganizationRequestNotAdmin'),
    JOIN_ORGANIZATION_REQUEST_ADMIN_NOT_FOUND_REQUESTER('Organization Join Request - Admin Not Found', '/alert/joinOrganizationRequestAdminNotFound'),
    JOIN_ORGANIZATION_REQUEST_REQUESTER_ALREADY_IN_LAW_FIRM('Organization Join Request - Already In Law Firm', '/alert/joinOrganizationRequestRequesterAlreadyInLawFirm'),
    JOIN_ORGANIZATION_REQUEST_ACCEPTED('Organization Join Request - Accepted', '/alert/joinOrganizationRequestAccepted'),
    JOIN_ORGANIZATION_REQUEST_DENIED('Organization Join Request - Denied', '/alert/joinOrganizationRequestDenied'),

    //Invite Attorney to create a new Legal practice
    INVITE_ATTORNEY_TO_CREATE_ORGANIZATION('Invitation to create a new law practice on EasyVisa with %s', '/alert/inviteToCreateOrganizationRequest'),
    INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_CANCELLED('Invitation to create a new law firm on EasyVisa Withdrawn', '/alert/inviteToCreateOrganizationRequestWithdrawn'),
    INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_ACCEPTED('Invitation to create a new law firm on EasyVisa Accepted', '/alert/inviteToCreateOrganizationRequestAccepted'),
    INVITE_ATTORNEY_TO_CREATE_ORGANIZATION_DENIED('Invitation to create a new law firm on EasyVisa Denied', '/alert/inviteToCreateOrganizationRequestDenied'),

    //Questionnaire related
    QUESTIONNAIRE_WARNING('Questionnaire warning', NotificationType.WARNING),
    NATIVE_ALPHABET_FORM_WARNING('Questionnaire Completed - Native Alphabet Form Warning','/alert/nativeAlphabetWarningAfterQuestionnaireCompletion', NotificationType.WARNING),
    QUESTIONNAIRE_COMPLETED('Questionnaire Completed','/alert/questionnaireCompleted', NotificationType.QUESTIONNAIRE_COMPLETE),
    QUESTIONNAIRE_COMPLETED_TO_ATTORNEY('Questionnaire Completed','/alert/questionnaireCompletedToAttorney', NotificationType.QUESTIONNAIRE_COMPLETE),
    DOCUMENT_PORTAL_COMPLETED('Document Portal Completed','/alert/documentPortalCompleted', NotificationType.DOCUMENTATION_COMPLETE),
    DOCUMENT_PORTAL_COMPLETED_TO_ATTORNEY('Document Portal Completed','/alert/documentPortalCompletedToAttorney', NotificationType.DOCUMENTATION_COMPLETE),

    DOCUMENT_SUB_PANEL_COMPLETION('Sub Panel Completed'),
    REJECTED_DOCUMENT_DISPOSITION('Document Rejection Notification email'),

    INVITE_TO_ORGANIZATION('Invitation to join the %s', '/alert/inviteToOrganizationRequest'),
    INVITE_TO_ORGANIZATION_ACCEPTED('Organization Join invite Accepted', '/alert/inviteToOrganizationRequestAccepted'),
    INVITE_TO_ORGANIZATION_DENIED('Organization Join invite Denied', '/alert/inviteToOrganizationRequestDenied'),
    INVITE_TO_ORGANIZATION_WITHDRAWN('Organization Join invite - Withdrawn', '/alert/inviteToOrganizationRequestWithdrawn'),
    INVITE_TO_ORGANIZATION_ALREADY_WITHDRAWN('Organization Join invite - Already Withdrawn', '/alert/inviteToOrganizationRequestWithdrawn'),
    INVITE_TO_ORGANIZATION_ATTORNEY_ALREADY_ACTIVE('Organization Join invite - AlreadymonthlyPaymentFailed Active In Law Firm', '/alert/inviteToOrganizationRequestAttorneyAlreadyActive'),
    INVITE_TO_ORGANIZATION_EMPLOYEE_ALREADY_ACTIVE('Organization Join invite - Already Active In Organization', '/alert/inviteToOrganizationRequestEmployeeAlreadyActive'),
    LEAVE_PARTNER_FROM_ORG('Organization Retirement', '/alert/organizationRetirement'),
    INCOMPLETE_EMPLOYMENTHISTORY_WARNING('Incomplete Employment History', NotificationType.WARNING),
    INCOMPLETE_DERIVATIVE_FAMILYINFORMATION_WARNING('Incomplete Derivative Family Information', NotificationType.WARNING),

    //Request to optIn to Package
    PACKAGE_OPTIN_REQUEST('Someone Wants to Add You to a Package in EasyVisa', '/alert/packageOptIn'),
    PACKAGE_OPTIN_REQUEST_ACCEPTED('Permission Granted to Add %s to a Package', '/alert/packageOptInAccepted'),
    PACKAGE_OPTIN_REQUEST_DENIED('Permission DENIED to Add %s to a Package', '/alert/packageOptInDenied'),

    //payments
    MONTHLY_PAYMENT_FAILED_GRACE('Problem with your EasyVisa payment', '/alert/monthlyPaymentFailedGrace'),
    MONTHLY_PAYMENT_FAILED('Account Suspension Due to Nonpayment', '/alert/monthlyPaymentFailed'),

    //profile/user
    USER_PASSWORD_CHANGED('Password has been changed', '/alert/userPasswordChanged'),
    PROFILE_EMAIL_CHANGED('Email has been changed', '/alert/profileEmailChanged'),

    //articles
    ARTICLE_APPROVED('Article You Submitted: %s', '/alert/articleAccepted'),
    ARTICLE_REJECTED('Article You Submitted: %s', '/alert/articleRejected'),

    //permissions
    CHANGED_PERMISSIONS('Your Permissions Have Changed','/alert/changedPermissions')

    final String subject
    final String templatePath
    final String title
    final NotificationType notificationType

    EasyVisaSystemMessageType(String subject, NotificationType notificationType = NotificationType.ALERT) {
        this.templatePath = null
        this.subject = subject
        this.title = subject
        this.notificationType = notificationType
    }

    EasyVisaSystemMessageType(String subject, String templatePath,
                              NotificationType notificationType = NotificationType.ALERT) {
        this.templatePath = templatePath
        this.subject = subject
        this.title = subject
        this.notificationType = notificationType
    }

    EasyVisaSystemMessageType(String subject, String templatePath, String title,
                              NotificationType notificationType = NotificationType.ALERT) {
        this.templatePath = templatePath
        this.subject = subject
        this.title = title
        this.notificationType = notificationType
    }

    static List<EasyVisaSystemMessageType> getProcessRequestAlertTypes() {
        [PACKAGE_TRANSFER_REQUEST,
         PACKAGE_TRANSFER_REQUEST_OWNER,
         PACKAGE_TRANSFER_REQUEST_ADMIN,
         APPLICANT_PACKAGE_TRANSFER_REQUEST,
         INVITE_ATTORNEY_TO_CREATE_ORGANIZATION,
         JOIN_ORGANIZATION_REQUEST,
         INVITE_TO_ORGANIZATION,
         PACKAGE_OPTIN_REQUEST]
    }

    static EasyVisaSystemMessageType findByNotificationType(NotificationType notificationType) {
        values().find { it.notificationType == notificationType }
    }

}
