package com.easyvisa.enums

import groovy.transform.CompileStatic

@CompileStatic
enum EmailTemplateType {

    //Email templates
    NEW_CLIENT('/email/welcomeEmail', false, 'Welcome to EasyVisa!'),
    UPDATED_CLIENT('/email/updatedClient', false, 'Here are the modifications to your USCIS package that we discussed...'),
    COVER_LETTER_NEW('/email/coverLetterNew', true, 'Package Cover letter'),
    COVER_LETTER_UPDTAED('/email/coverLetterUpdated', true, 'Updated package cover letter'),
    DOCUMENT_REJECTION_NOTIFICATION('/email/documentRejectedNotification', false, 'Document Rejected'),
    INVITE_COLLEAGUE_TO_EASYVISA('/email/inviteColleague', false, 'Check out this awesome platform for immigration attorneys! - |LEGAL_REP_FULL_NAME|'),

    INVITE_APPLICANT('/email/inviteApplicant', false, 'Invite Applicant(s)'),
    NEW_EMPLOYEE_REGISTRATION_INVITE('/email/newEmployeeRegistrationInvite', false, 'Registration Invitation (New Employee)'),
    PACKAGE_TRANSFER_SUCCESSFUL('/email/packageTransferSuccessful', false, 'Your immigration case on EasyVisa has been transferred to Another Legal Representative'),

    ADDITIONAL_FEES('/email/additionalFees', false, 'Additional Fees'),

    SUB_PANEL_COMPLETION('/email/subPanelCompletion', false, 'Sub Panel Completed'),

    //Attorney Reminders
    QUESTIONNAIRE_INACTIVITY('/email/questionnaireInactivity', false, 'Questionnaire Inactivity Time', NotificationType.QUESTIONNAIRE_INACTIVITY),
    DOCUMENT_PORTAL_INACTIVITY('/email/documentPortalInactivity', false, 'Document Portal Inactivity Time', NotificationType.DOCUMENT_PORTAL_INACTIVITY),
    MARRIAGE_IN_90_DAYS_OF_ARRIVAL('/email/marriageIn90DaysOfArrival', false,'We want to welcome you and your fiancé to America!', NotificationType.MARRIAGE_IN_90_DAYS_OF_ARRIVAL),
    APPLY_SOCIAL_SECURITY_CARD('/email/applySocialSecurityCard', false,'Apply for Social Security Card', NotificationType.APPLY_SOCIAL_SECURITY_CARD),
    RENEW_WORK_AUTHORIZATION('/email/renewWorkAuthorization', false,'Renew Work Authorization', NotificationType.RENEW_WORK_AUTHORIZATION),
    REMOVAL_CONDITIONS_RESIDENCE('/email/removalConditionsResidence', false,'Time to remove the Conditons on your Permanent Residence!', NotificationType.REMOVAL_CONDITIONS_RESIDENCE),
    RENEW_RESIDENCE_CARD('/email/renewResidenceCard', false,'Renew Residence Card', NotificationType.RENEW_RESIDENCE_CARD),
    NOTICE_ACTION_LETTERS('/email/noticeActionLetters', false,'Notice of Action Letters', NotificationType.NOTICE_ACTION_LETTERS),
    VISA_PAGE_PASSPORT('/email/visaPagePassport', false,'Visa Page in Passport', NotificationType.VISA_PAGE_PASSPORT),
    MARRIAGE_CERTIFICATE('/email/marriageCertificate', false,'Everyone here at EasyVisa wants to congratulate on your marriage!', NotificationType.MARRIAGE_CERTIFICATE),
    ADVANCE_PAROLE('/email/advanceParole', false,'Advance Parole', NotificationType.ADVANCE_PAROLE),
    WORK_AUTHORIZATION('/email/workAuthorization', false,'Let\'s get to work!', NotificationType.WORK_AUTHORIZATION),
    PERMANENT_RESIDENCE_CARD('/email/permanentResidenceCard', false,'Apply for Permanent Residence Card', NotificationType.PERMANENT_RESIDENCE_CARD),
    SOCIAL_SECURITY_CARD('/email/socialSecurityCard', false,'Social Security Card', NotificationType.SOCIAL_SECURITY_CARD),
    PAYMENT('/email/payment', false,'Your package has been blocked', NotificationType.PAYMENT),
    NOT_SUPPORTED_IMMIGRATION_PROCESS('/email/notSupportedImmigrationProcess', false,'Your package has been blocked', NotificationType.NOT_SUPPORTED_IMMIGRATION_PROCESS),

    //Fragments
    LEGAL_FEES_NEW('/email/legalFeesNew', true,),
    LEGAL_FEES_UPDATED('/email/legalFeesUpdated', true,),
    RETAINER_AGREEMENT_NEW('/email/retainerAgreementNew', true),
    RETAINER_AGREEMENT_UPDATED('/email/retainerAgreementUpdated', true),
    CLOSING_TEXT('/email/closingText', true),
    LIST_OF_BENEFICIARIES('/email/listOfBeneficiaries', true),
    LEGAL_REPRESENTATIVE_ADDRESS('/email/legalRepAddress', true),
    APPLICANT_LIST('/email/applicantList', true),
    FEE_TABLE('/email/feeTable', true),
    MISSING_DOCUMENTATION('/email/missingDocumentation', true),

    // USCIS Form Edition Date Update
    USCIS_FORM_EDITION_UPDATE_TO_APPLICANTS('/email/uscisFormEditionDateUpdateToApplicants', false, 'Your Questionnaire has been Updated (potentially with changes to some questions you have already answered)!'),
    USCIS_FORM_EDITION_UPDATE_TO_ORG_MEMBERS('/email/uscisFormEditionDateUpdateToOrgMembers', false, 'Important Alert!!! USCIS Form [Form Number & Form Name] has been updated in EasyVisa')

    final String path
    Boolean isFragment
    final String subject
    final NotificationType notificationType

    EmailTemplateType(String path, boolean isFragment, String subject = null, NotificationType notifType = null) {
        this.path = path
        this.isFragment = isFragment
        this.subject = subject
        this.notificationType = notifType
    }

    static List<EmailTemplateType> getAttorneyReminders() {
        [QUESTIONNAIRE_INACTIVITY, DOCUMENT_PORTAL_INACTIVITY,
         PERMANENT_RESIDENCE_CARD, MARRIAGE_IN_90_DAYS_OF_ARRIVAL, REMOVAL_CONDITIONS_RESIDENCE,
         MARRIAGE_CERTIFICATE, WORK_AUTHORIZATION, NOT_SUPPORTED_IMMIGRATION_PROCESS]
    }

    static EmailTemplateType findByNotificationType(NotificationType notificationType) {
        values().find { it.notificationType == notificationType }
    }
}
