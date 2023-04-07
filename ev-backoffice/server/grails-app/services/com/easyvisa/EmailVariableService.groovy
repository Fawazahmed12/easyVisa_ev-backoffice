package com.easyvisa

import com.easyvisa.questionnaire.util.DateUtil
import org.springframework.beans.factory.annotation.Value

/***
 * Methods in this service are called dynamically in the
 * method evMailService.resolveEmailTemplateVariable(). Therefore
 * their usage is not detected in IntellijIdea.
 */
class EmailVariableService {

    private static final String EMPTY_VAL = ' '
    @Value('${frontEndAppURL}')
    String frontEndAppURL
    @Value('${nonLegalRegistrationLink}')
    String nonLegalRegistrationLink
    @Value('${loginUrl}')
    String appLoginUrl
    @Value('${serverURL}')
    String serverURL
    @Value('${registerRedir}')
    String registerRedir

    final String PACKAGE_KEY = 'packageObj'
    final String LEGAL_REPRESENTATIVE_KEY = 'legalRepresentative'
    final String ORGANIZATION_KEY = 'organization'
    final String APPLICANT_KEY = 'applicant'
    final String APPLICANT_REGISTRATION_CODE_KEY = 'applicantRegistrationCode'
    final String EMPLOYEE_REGISTRATION_CODE_KEY = 'applicantRegistrationCode'
    final String PREVIEW_MODE_KEY = 'isPreviewMode'
    final String EMPLOYEE_KEY = 'employee'
    final String ADMIN_KEY = 'admin'
    final String RECIPIENT_PROFILE_KEY = 'recipientProfile'
    final String DOCUMENT_FILE_KEY = 'documentFile'
    final String DOCUMENT_PANEL_KEY = 'documentPanelName'
    final String DOCUMENT_SUB_PANEL_KEY = 'documentSubPanelName'
    final String QUESTIONNAIRE_INACTIVE_INTERVAL_KEY = 'questionnaireInactiveInterval'
    final String DOCUMENTS_INACTIVE_INTERVAL_KEY = 'documentsInactiveInterval'
    final String MISSING_DOCUMENTS_KEY = 'missingDocuments'
    final String MARRIAGE_EXPIRATION_KEY = 'marriageExpiration'
    final String ADMIN_CONFIG_KEY = 'adminConfig'

    Map safeAddKey(Map map, def k, def v) {
        map = map ?: [:]
        map[(k)] = v
        map
    }

    Map addPackage(Map map, Package aPackage) {
        safeAddKey(map, PACKAGE_KEY, aPackage)
    }

    Map addLegalRepresentative(Map map, LegalRepresentative representative) {
        safeAddKey(map, LEGAL_REPRESENTATIVE_KEY, representative)
    }

    Map addAdminConfig(Map map, AdminConfig adminConfig) {
        safeAddKey(map, ADMIN_CONFIG_KEY, adminConfig)
    }

    Map addApplicant(Map map, Applicant applicant) {
        map = safeAddKey(map, APPLICANT_KEY, applicant)
        safeAddKey(map, RECIPIENT_PROFILE_KEY, applicant?.profile)
    }

    Map addOrganization(Map map, Organization organization) {
        safeAddKey(map, ORGANIZATION_KEY, organization)
    }

    Map addApplicantRegistrationCode(Map map, RegistrationCode registrationCode) {
        safeAddKey(map, APPLICANT_REGISTRATION_CODE_KEY, registrationCode)
    }

    Map addEmployeeRegistrationCode(Map map, RegistrationCode registrationCode) {
        safeAddKey(map, EMPLOYEE_REGISTRATION_CODE_KEY, registrationCode)
    }

    Map addRecipientProfile(Map map, Profile profile) {
        safeAddKey(map, RECIPIENT_PROFILE_KEY, profile)
    }

    Map addEmployee(Map map, Employee employee) {
        safeAddKey(map, EMPLOYEE_KEY, employee)
    }

    Map addAdmin(Map map, Employee admin) {
        safeAddKey(map, ADMIN_KEY, admin)
    }

    Map addDocumentFile(Map map, EasyVisaFile easyVisaFile) {
        safeAddKey(map, DOCUMENT_FILE_KEY, easyVisaFile)
    }

    Map addDocumentPanelName(Map map, String panelName) {
        safeAddKey(map, DOCUMENT_PANEL_KEY, panelName)
    }

    Map addDocumentSubPanelName(Map map, String subPanelName) {
        safeAddKey(map, DOCUMENT_SUB_PANEL_KEY, subPanelName)
    }

    Map addQuestionnaireInactiveInterval(Map map, Integer interval) {
        safeAddKey(map, QUESTIONNAIRE_INACTIVE_INTERVAL_KEY, interval)
    }

    Map addDocumentsInactiveInterval(Map map, Integer interval) {
        safeAddKey(map, DOCUMENTS_INACTIVE_INTERVAL_KEY, interval)
    }

    Map addMissingDocuments(Map map, Map documents) {
        safeAddKey(map, MISSING_DOCUMENTS_KEY, documents)
    }

    Map addMarriageExpiration(Map map, Date date) {
        String val = EMPTY_VAL
        if (date) {
            val = DateUtil.pdfFormDate(date)
        }
        safeAddKey(map, MARRIAGE_EXPIRATION_KEY, val)
    }

    Package getEvPackage(Map params) {
        params[PACKAGE_KEY]
    }

    LegalRepresentative getLegalRepresentative(Map params) {
        params[LEGAL_REPRESENTATIVE_KEY]
    }

    Organization getOrganization(Map params) {
        params[ORGANIZATION_KEY]
    }

    Applicant getApplicant(Map params) {
        params[APPLICANT_KEY]
    }

    RegistrationCode getApplicantRegistrationCode(Map params) {
        params[APPLICANT_REGISTRATION_CODE_KEY]
    }

    RegistrationCode getEmployeeRegistrationCode(Map params) {
        params[EMPLOYEE_REGISTRATION_CODE_KEY]
    }

    Employee getEmployee(Map params) {
        params[EMPLOYEE_KEY]
    }

    Employee getAdmin(Map params) {
        params[ADMIN_KEY]
    }

    Profile getRecipientProfile(Map params) {
        params[RECIPIENT_PROFILE_KEY]
    }

    EasyVisaFile getDocumentFile(Map params) {
        params[DOCUMENT_FILE_KEY]
    }

    AdminConfig getAdminConfig(Map params) {
        params[ADMIN_CONFIG_KEY]
    }

    String documentFileName(Map params) {
        getDocumentFile(params).originalName
    }

    String documentUploaderName(Map params) {
        getDocumentFile(params).uploader.firstName
    }

    String documentUploaderFullName(Map params) {
        getDocumentFile(params).uploader.fullName
    }

    String documentUploadDate(Map params) {
        DateUtil.pdfFormDate(getDocumentFile(params).dateCreated)
    }

    String documentPanelName(Map params) {
        params[DOCUMENT_PANEL_KEY]
    }

    String documentSubPanelName(Map params) {
        params[DOCUMENT_SUB_PANEL_KEY]
    }

    String petitionerName(Map params) {
        Package aPackage = getEvPackage(params)
        Applicant petitioner = aPackage.client
        petitioner?.profile?.firstName
    }

    String packageName(Map params) {
        Package aPackage = getEvPackage(params)
        aPackage?.title
    }

    String petitionerFullName(Map params) {
        Package aPackage = getEvPackage(params)
        Applicant petitioner = aPackage.client
        petitioner?.profile?.fullName
    }

    String beneficiaryName(Map params) {
        Package aPackage = getEvPackage(params)
        Applicant beneficiary = aPackage.principalBeneficiary
        beneficiary?.profile?.firstName
    }

    String beneficiaryFullName(Map params) {
        Package aPackage = getEvPackage(params)
        Applicant beneficiary = aPackage.principalBeneficiary
        beneficiary?.profile?.fullName
    }

    String recipientEvId(Map params) {
        getRecipientProfile(params)?.easyVisaId
    }

    String loginUrl(Map params) {
        "$frontEndAppURL$appLoginUrl"
    }

    String legalRepEmail(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.profile.email
    }

    String legalRepPhone(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.officePhone
    }

    String legalRepMobile(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.mobilePhone
    }

    String legalRepFax(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.faxNumber
    }

    String legalRepName(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.profile.firstName
    }

    String representativeType(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.attorneyType.displayName
    }

    String legalRepFullName(Map params) {
        LegalRepresentative representative = getLegalRepresentative(params)
        representative.profile.fullName
    }

    String applicantName(Map params) {
        Applicant applicant = getApplicant(params)
        applicant.profile.firstName
    }

    String applicantFullName(Map params) {
        Applicant applicant = getApplicant(params)
        applicant.profile.fullName
    }

    String orgName(Map params) {
        Organization organization = getOrganization(params)
        organization.name
    }

    String registerApplicantLink(Map params) {
        RegistrationCode registrationCode = getApplicantRegistrationCode(params)
        "$serverURL$registerRedir/${registrationCode.token}"
    }

    String employeeRegistrationLink(Map params) {
        RegistrationCode registrationCode = getEmployeeRegistrationCode(params)
        "$frontEndAppURL$nonLegalRegistrationLink?token=${registrationCode.token}"
    }

    String adminName(Map params) {
        Employee admin = getAdmin(params)
        admin.profile.firstName
    }

    String adminFullName(Map params) {
        Employee admin = getAdmin(params)
        admin.profile.fullName
    }

    String employeeEasyvisaId(Map params) {
        Employee employee = getEmployee(params)
        employee.profile.easyVisaId
    }

    String referralDiscount(Map params) {
        AdminConfig adminConfig = getAdminConfig(params)
        adminConfig?.signupDiscount
    }

    String questionnaireInactiveInterval(Map params) {
        params[QUESTIONNAIRE_INACTIVE_INTERVAL_KEY]
    }

    String documentsInactiveInterval(Map params) {
        params[DOCUMENTS_INACTIVE_INTERVAL_KEY]
    }

    String packageClientOwed(Map params) {
        getEvPackage(params).owed ?: EMPTY_VAL
    }

    String packageId(Map params) {
        getEvPackage(params).easyVisaId
    }

    String beneficiaries(Map params) {
        getEvPackage(params).orderedBenefits*.applicant*.name.join(', ')
    }

    String marriageExpiration(Map params) {
        params[MARRIAGE_EXPIRATION_KEY]
    }

    Boolean getPreviewMode(Map params) {
        params[PREVIEW_MODE_KEY]
    }

    Map setPreviewMode(Map params) {
        safeAddKey(params, PREVIEW_MODE_KEY, true)
    }
}
