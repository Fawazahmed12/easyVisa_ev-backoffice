package com.easyvisa

import org.springframework.http.HttpMethod

class UrlMappings {

    @SuppressWarnings('DuplicateStringLiteral')
    static mappings = {
        //Users
        '/api/users/me'(controller: 'user', action: 'me', method: HttpMethod.GET)
        '/api/users/me'(controller: 'user', action: 'manageUserMembership', method: HttpMethod.PATCH)
        '/api/users/me'(controller: 'user', action: 'deleteUser', method: HttpMethod.DELETE)
        "/api/users/${id}/profile-picture"(controller: 'user', action: 'uploadProfilePicture', method: HttpMethod.POST)
        "/api/users/${id}/payment-method"(controller: 'user', action: 'getPaymentMethod', method: HttpMethod.GET)
        "/api/users/${id}/payment-method"(controller: 'user', action: 'savePaymentMethod', method: HttpMethod.PUT)
        "/api/users/ev-id/${id}/id"(controller: 'user', action: 'getUserId', method: HttpMethod.GET)
        '/api/users/change-password'(controller: 'user', action: 'changePassword', method: HttpMethod.PUT)
        "/api/users/${id}/notifications"(controller: 'user', action: 'getNotifications', method: HttpMethod.GET)
        "/api/users/${id}/notifications"(controller: 'user', action: 'updateNotifications', method: HttpMethod.PUT)

        //Profile
        "/api/profile"(controller: 'user', action: 'getProfile', method: HttpMethod.GET)
        "/api/profile"(controller: 'user', action: 'editProfile', method: HttpMethod.PUT)
        '/api/profile/email'(controller: 'user', action: 'editEmail', method: HttpMethod.PUT)

        //Account Transaction
        "/api/account-transactions/user/${id}/balance"(controller: 'accountTransaction',
                action: 'getBalance', method: HttpMethod.GET)
        "/api/account-transactions/user/${id}/payment"(controller: 'accountTransaction',
                action: 'payBalance', method: HttpMethod.POST)
        "/api/account-transactions/user/${id}"(controller: 'accountTransaction',
                action: 'getAccountTransactions', method: HttpMethod.GET)
        "/api/account-transactions/user/${id}"(controller: 'accountTransaction',
                action: 'addAccountTransaction', method: HttpMethod.POST)

        //Legal Representatives
        "/api/attorneys/$id"(controller: 'user', action: 'updateAttorney', method: HttpMethod.PATCH)
        "/api/attorneys/complete-payment"(controller: 'attorney', action: 'completePayment', method: HttpMethod.POST)
        "/api/attorneys/validate"(controller: 'attorney', action: 'validate', method: HttpMethod.POST)
        "/api/employees/isadmin"(controller: 'attorney', action: 'isAdmin', method: HttpMethod.POST)
        "/api/attorneys/create-organization-invite"(controller: 'attorney', action: 'createOrganizationInvite', method: HttpMethod.POST)
        "/api/attorneys/$id/fee-schedule"(controller: 'attorney', action: 'feeSchedule', method: HttpMethod.GET)
        "/api/attorneys/$representativeId/marketing"(controller: 'attorney', action: 'marketingDashboardData', method: HttpMethod.GET)
        "/api/attorneys/$representativeId/financial"(controller: 'attorney', action: 'financialDashboardData', method: HttpMethod.GET)
        "/api/attorneys/$id/notifications"(controller: 'attorney', action: 'getNotifications', method: HttpMethod.GET)
        "/api/attorneys/$id/notifications"(controller: 'attorney', action: 'updateNotifications', method: HttpMethod.PATCH)
        "/api/attorneys/notifications/types"(controller: 'attorney', action: 'notificationTypes', method: HttpMethod.GET)
        '/api/attorneys/uscis-edition-dates'(controller: 'attorney', action: 'updateUSCISEditionDates', method: HttpMethod.PUT)
        '/api/attorneys/uscis-edition-dates'(controller: 'attorney', action: 'getUSCISEditionDates', method: HttpMethod.GET)
        '/api/attorneys/referral'(controller: 'attorney', action: 'referral', method: HttpMethod.POST)
        '/api/attorneys/invite-colleagues'(controller: 'attorney', action: 'inviteColleagues', method: HttpMethod.POST)

        //EmailTemplates
        '/api/email-templates'(controller: 'email', action: 'getEmailTemplates', method: HttpMethod.GET)
        '/api/email-templates'(controller: 'email', action: 'createTemplate', method: HttpMethod.PUT)
        "/api/email-templates/$emailTemplateType"(controller: 'email', action: 'getEmailTemplate', method: HttpMethod.GET)
        "/api/email-templates/$emailTemplateType/preview"(controller: 'email', action: 'previewTemplate', method: HttpMethod.POST)

        //Email
        '/api/email'(controller: 'email', action: 'create', method: HttpMethod.POST)
        "/api/email/$id"(controller: 'email', action: 'update', method: HttpMethod.PUT)
        "/api/email/$id"(controller: 'email', action: 'read', method: HttpMethod.GET)
        "/api/email/$id/preview"(controller: 'email', action: 'preview', method: HttpMethod.GET)
        "/api/emails/find"(controller: 'email', action: 'find', method: HttpMethod.GET)

        //Public
        '/api/public/admin-config'(controller: 'public', action: 'showAdminConfig', method: HttpMethod.GET)
        '/api/public/admin-config/government-fees'(controller: 'public', action: 'showGovFee', method: HttpMethod.GET)
        '/api/public/admin-config/fee-schedule'(controller: 'public', action: 'showFeeSchedule', method: HttpMethod.GET)
        '/api/public/verify-registration'(controller: 'public', action: 'verify', method: HttpMethod.POST)
        '/api/public/validate-token'(controller: 'public', action: 'validateToken', method: HttpMethod.POST)
        '/api/public/register-user'(controller: 'public', action: 'register', method: HttpMethod.POST)
        '/api/public/reset-password'(controller: 'public', action: 'resetPassword', method: HttpMethod.POST)
        '/api/public/forgot-username'(controller: 'public', action: 'forgotUsername', method: HttpMethod.POST)
        '/api/public/show-username'(controller: 'public', action: 'showUsername', method: HttpMethod.POST)
        '/api/public/attorneys'(controller: 'public', action: 'save', method: HttpMethod.POST)
        '/api/public/validate-username'(controller: 'public', action: 'checkUsername', method: HttpMethod.POST)
        '/api/public/validate-email'(controller: 'public', action: 'validateEmail', method: HttpMethod.POST)
        '/api/public/forgot-password'(controller: 'public', action: 'forgotPassword', method: HttpMethod.POST)
        '/api/public/marketing-config'(controller: 'public', action: 'attorneySearchParams', method: HttpMethod.GET)
        '/api/public/attorneys/search'(controller: 'public', action: 'find', method: HttpMethod.GET)
        "/api/public/attorneys/${id}/contact-info"(controller: 'public', action: 'contactInfo', method: HttpMethod.GET)
        "/api/public/attorneys/${id}/ratings"(controller: 'public', action: 'ratings', method: HttpMethod.GET)
        "/api/public/attorneys/${id}/reviews"(controller: 'public', action: 'reviews', method: HttpMethod.GET)
        "/api/public/attorneys/$id"(controller: 'public', action: 'attorney', method: HttpMethod.GET)
        "/api/public/attorneys/articles"(controller: 'public', action: 'articles', method: HttpMethod.GET)
        "/api/public/users/${id}/profile-picture/${ignore}"(controller: 'public', action: 'userProfilePicture', method: HttpMethod.GET)
        "/api/public/users/${id}/profile-picture"(controller: 'public', action: 'userProfilePicture', method: HttpMethod.GET)
        "/api/public/organizations/${id}/profile-picture"(controller: 'public', action: 'orgProfilePicture', method: HttpMethod.GET)
        "/api/public/organizations/${id}/profile-picture/${ignore}"(controller: 'public', action: 'orgProfilePicture', method: HttpMethod.GET)
        "/api/public/organizations/$id"(controller: 'public', action: 'organization', method: HttpMethod.GET)
        "/api/public/articles/$id"(controller: 'public', action: 'article', method: HttpMethod.GET)
        "/api/public/register-redir/$token"(controller: 'public', action: 'registerRedir', method: HttpMethod.GET)
        "/api/public/generate-xsrf"(controller: 'public', action: 'generateXSRF', method: HttpMethod.GET)

        //Packages
        '/api/packages'(controller: 'package', action: 'create', method: HttpMethod.POST)
        '/api/packages/find'(controller: 'package', action: 'find', method: HttpMethod.GET)
        '/api/packages/transfer'(controller: 'package', action: 'transfer', method: HttpMethod.POST)
        '/api/packages'(controller: 'package', action: 'deleteLeads', method: HttpMethod.DELETE)
        "/api/packages/${id}"(controller: 'package', action: 'get', method: HttpMethod.GET)
        "/api/packages/${id}"(controller: 'package', action: 'edit', method: HttpMethod.PUT)
        "/api/packages/${id}/owed-amount"(controller: 'package', action: 'updateAmountOwed', method: HttpMethod.PATCH)
        "/api/packages/${id}/send-welcome-email"(controller: 'package', action: 'sendWelcomeEmail', method: HttpMethod.POST)
        "/api/packages/${id}/send-applicant-invite"(controller: 'package', action: 'sendApplicantInvite', method: HttpMethod.POST)
        "/api/packages/${id}/retainer"(controller: 'package', action: 'uploadRetainer', method: HttpMethod.POST)
        "/api/packages/${id}/retainer"(controller: 'package', action: 'deleteRetainer', method: HttpMethod.DELETE)
        "/api/packages/${id}/change-status"(controller: 'package', action: 'changeStatus', method: HttpMethod.POST)
        "/api/packages/${id}/send-bill"(controller: 'package', action: 'sendBill', method: HttpMethod.POST)
        '/api/packages/leads'(controller: 'package', action: 'deleteSelectedLeads', method: HttpMethod.DELETE)
        '/api/packages/transferred'(controller: 'package', action: 'deleteTransferred', method: HttpMethod.DELETE)

        //Admin
        '/api/admin-config'(controller: 'admin', action: 'updateAdminConfig', method: HttpMethod.POST)
        '/api/admin-config/government-fees'(controller: 'admin', action: 'updateGovFee', method: HttpMethod.POST)
        '/api/admin-config/fee-schedule'(controller: 'admin', action: 'updateFeeSchedule', method: HttpMethod.POST)
        '/api/admin/alerts'(controller: 'admin', action: 'sendAlerts', method: HttpMethod.POST)
        '/api/admin-config/batch-jobs'(controller: 'admin', action: 'getBatchJobsStatus', method: HttpMethod.GET)
        '/api/admin-config/batch-jobs'(controller: 'admin', action: 'setBatchJobsStatus', method: HttpMethod.PATCH)

        //Applicants
        '/api/applicants/find'(controller: 'applicant', action: 'find', method: HttpMethod.GET)
//        "/api/applicants/$id"(controller: 'applicant', action: 'deleteData', method: HttpMethod.DELETE)
        '/api/applicants/package/transfer'(controller: 'applicant', action: 'transferPackage', method: HttpMethod.POST)

        //Employees
        "/api/organizations/$id/employees"(controller: 'employee', action: 'create', method: HttpMethod.POST)
        "/api/organizations/$id/employees/$employeeId"(controller: 'employee', action: 'update', method: HttpMethod.PUT)
        "/api/organizations/$id/employees/$employeeId"(controller: 'employee', action: 'show', method: HttpMethod.GET)
        '/api/employees/convert-to-attorney'(controller: 'employee', action: 'convertToAttorney', method: HttpMethod.POST)

        //Organizations
        "/api/organizations/$id"(controller: 'public', action: 'organization', method: HttpMethod.GET)
        "/api/organizations/$id"(controller: 'organization', action: 'edit', method: HttpMethod.PUT)
        //Below API is used when a legal rep sends a request to join an organization.
        "/api/organizations/$evId/join-request"(controller: 'organization', action: 'joinRequest', method: HttpMethod.PUT)
        //Below API is used to delete an existing request to join an organization
        "/api/organizations/$evId/join-request/$requestId"(controller: 'organization', action: 'deleteJoinRequest', method: HttpMethod.DELETE)
        //Below API is used when a legal rep invites another legal rep to join an organization.
        "/api/organizations/$id/invitation"(controller: 'organization', action: 'inviteMember', method: HttpMethod.PUT)
        "/api/organizations/$id/validate-invite-member"(controller: 'organization', action: 'validateInviteMember', method: HttpMethod.POST)
        "/api/organizations/$id/leave"(controller: 'organization', action: 'leave', method: HttpMethod.POST)
        //Below endpoint seems not needed because we have /api/organizations/$id/employee-permissions and confirmed with Anton too, so marked as deprecated
        "/api/organization/$id/employees"(controller: 'organization', action: 'employees', method: HttpMethod.GET)
        "/api/organizations/$id/representatives"(controller: 'organization', action: 'representatives', method: HttpMethod.GET)
        "/api/organizations/${id}/profile-picture"(controller: 'organization', action: 'uploadProfilePicture', method: HttpMethod.POST)
        "/api/organizations/$id/employee-permissions"(controller: 'organization', action: 'organizationEmployees', method: HttpMethod.GET)
        //Below endpoint for creating a new legal practice invite
        "/api/legal-practice-invitee/ev-id/$evId/email/$email"(controller: 'organization', action: 'inviteLegalRep', method: HttpMethod.PUT)
        "/api/organizations/"(controller: 'organization', action: 'list', method: HttpMethod.GET)
        "/api/legal-practice-invite"(controller: 'organization', action: 'deleteLegalRepInvite', method: HttpMethod.DELETE)
        "/api/organizations/$id/employee/$employeeId"(controller: 'organization', action: 'withdrawInvitation', method: HttpMethod.DELETE)

        //PDF
        '/api/pdf'(controller: 'pdf', action: 'generate', method: HttpMethod.GET)

        //Alerts
        "/api/alerts"(controller: 'alert', action: 'userAlerts', method: HttpMethod.GET)
        "/api/alerts"(controller: 'alert', action: 'deleteAlerts', method: HttpMethod.DELETE)
        "/api/alerts/$id"(controller: 'alert', action: 'get', method: HttpMethod.GET)
        "/api/alerts/$id"(controller: 'alert', action: 'edit', method: HttpMethod.PUT)
        "/api/alerts/$id/reply"(controller: 'alert', action: 'reply', method: HttpMethod.PUT)
        "/api/unread/count"(controller: 'alert', action: 'count', method: HttpMethod.GET)

        //Warnings
        "/api/warnings"(controller: 'warning', action: 'userWarnings', method: HttpMethod.GET)
        "/api/warnings"(controller: 'warning', action: 'deleteWarnings', method: HttpMethod.DELETE)
        "/api/warnings/$id"(controller: 'warning', action: 'get', method: HttpMethod.GET)
        "/api/warnings/$id"(controller: 'warning', action: 'edit', method: HttpMethod.PUT)

        //Articles
        "/api/articles"(controller: 'article', action: 'create', method: HttpMethod.POST)


        delete "/$controller/$id(.$format)?"(action: 'delete')
        get "/$controller(.$format)?"(action: 'index')
        get "/$controller/$id(.$format)?"(action: 'show')
        post "/$controller(.$format)?"(action: 'save')
        put "/$controller/$id(.$format)?"(action: 'update')
        patch "/$controller/$id(.$format)?"(action: 'patch')

        '/'(controller: 'application', action: 'index')
        '500'(controller: 'errors', action: 'serverError')
        '404'(view: '/notFound')
        '403'(view: '/notPermitted')
        '401'(view: '/notAuthorized')

        '/api/questionnaire/questions'(controller: 'questionnaire',
                action: 'fetchQuestionnaire',
                method: HttpMethod.POST)

        "/api/questionnaire/packages/$packageId/applicants/$applicantId/sections/$sectionId"(controller: 'questionnaire',
                action: 'fetchQuestionnaireAnswers',
                method: HttpMethod.GET)

        "/api/questionnaire/sync-answers"(controller: 'questionnaire',
                action: 'syncAndCopyQuestionnaireAnswers',
                method: HttpMethod.POST)

        '/api/answer'(controller: 'answer',
                action: 'save',
                method: HttpMethod.POST)

        '/api/repeatinggroup'(controller: 'answer',
                action: 'createRepeatingGroupInstance',
                method: HttpMethod.POST)

        '/api/repeatinggroup/remove'(controller: 'answer',
                action: 'removeRepeatingGroupInstance',
                method: HttpMethod.POST)

        '/api/answer/validate'(controller: 'answer',
                action: 'validateAnswer',
                method: HttpMethod.POST)

        "/api/package/$packageId/sections"(controller: 'questionnaire',
                action: 'fetchPackageSections',
                method: HttpMethod.GET)

        "/api/package/$packageId/questionnaireforms"(controller: 'questionnaire',
                action: 'fetchQuestionnaireForms',
                method: HttpMethod.GET)

        "/api/questionnaire/completionwarning"(controller: 'questionnaire',
                action: 'fetchQuestionnaireCompletionWarning', method: HttpMethod.POST)
        "/api/questionnaire/progress/package/${id}"(controller: 'questionnaire',
                action: 'progress', method: HttpMethod.GET)
        "/api/questionnaire/access/package/${id}"(controller: 'questionnaire',
                action: 'fetchQuestionnaireAccessState', method: HttpMethod.GET)

        //Review
        '/api/review'(controller: 'review', action: 'create', method: HttpMethod.POST)
        "/api/review/$id"(controller: 'review', action: 'get', method: HttpMethod.GET)
        "/api/review/$id"(controller: 'review', action: 'update', method: HttpMethod.PUT)
        "/api/package/$packageId/attorney/$representativeId/review"(controller: 'review', action: 'reviewByPackageAndRepresentative', method: HttpMethod.GET)
        "/api/attorneys/review/$id"(controller: 'review', action: 'updateReadReply', method: HttpMethod.PATCH)
        "/api/attorneys/reviews"(controller: 'review', action: 'reviews', method: HttpMethod.GET)

        //API Documentation, (Not available in production environment)
        '/doc'(controller: 'util', ac1ion: 'apiDoc', method: HttpMethod.GET)

        //Documents
        "/api/document/progress/package/${id}"(controller: 'document', action: 'progress', method: HttpMethod.GET)
        "/api/document/package/$packageId/requireddocuments"(controller: 'document', action: 'fetchPackageRequiredDocuments', method: HttpMethod.GET)
        "/api/document/attachments"(controller: 'document', action: 'downloadDocumentAttachments', method: HttpMethod.GET)
        "/api/document/attachments"(controller: 'document', action: 'deleteDocumentAttachments', method: HttpMethod.DELETE)
        "/api/document/attachments/all"(controller: 'document', action: 'downloadAllDocumentAttachments', method: HttpMethod.GET)
        "/api/document/attachment"(controller: 'document', action: 'uploadDocumentAttachment', method: HttpMethod.POST)
        "/api/document/attachment"(controller: 'document', action: 'findDocumentAttachment', method: HttpMethod.GET)
        "/api/document/attachment/thumbnail"(controller: 'document', action: 'findDocumentAttachmentThumbnail', method: HttpMethod.GET)
        "/api/document/notes"(controller: 'document', action: 'saveDocumentNote', method: HttpMethod.POST)
        "/api/document/notes"(controller: 'document', action: 'findDocumentNotes', method: HttpMethod.GET)
        "/api/document/notes"(controller: 'document', action: 'removeDocumentNote', method: HttpMethod.DELETE)
        "/api/document/uscis"(controller: 'document', action: 'fetchPackageUSCISData', method: HttpMethod.GET)
        '/api/document/uscis/form'(controller: 'document', action: 'findUSCISForm', method: HttpMethod.GET)
        '/api/document/uscis/forms'(controller: 'document', action: 'downloadUSCISForms', method: HttpMethod.GET)
        '/api/document/uscis/blankform'(controller: 'document', action: 'findBlankUSCISForm', method: HttpMethod.GET)
        '/api/document/uscis/blankforms'(controller: 'document', action: 'downloadBlankUSCISForms', method: HttpMethod.GET)
        '/api/document/forms'(controller: 'document', action: 'fetchAllUSCISForms', method: HttpMethod.GET)
        "/api/document/package/$packageId/sentdocuments"(controller: 'document', action: 'fetchPackageSentDocuments', method: HttpMethod.GET)
        "/api/document/milestone"(controller: 'document', action: 'saveDocumentMilestone', method: HttpMethod.POST)
        "/api/document/milestone"(controller: 'document', action: 'findDocumentMilestones', method: HttpMethod.GET)
        "/api/document/actiondate"(controller: 'document', action: 'saveDocumentActionDate', method: HttpMethod.POST)
        "/api/document/approve"(controller: 'document', action: 'saveDocumentApproval', method: HttpMethod.PATCH)
        "/api/document/package/$packageId/receiveddocuments"(controller: 'document', action: 'fetchPackageReceivedDocuments', method: HttpMethod.GET)
        "/api/document/access/package/${id}"(controller: 'document',
                action: 'fetchDocumentsAccessState', method: HttpMethod.GET)

        //Dispositions
        "/api/dispositions"(controller: 'disposition', action: 'userDispositions', method: HttpMethod.GET)
        "/api/dispositions/$id"(controller: 'disposition', action: 'edit', method: HttpMethod.PUT)
        "/api/dispositions/$id"(controller: 'disposition', action: 'get', method: HttpMethod.GET)

        //Dictionaries
        '/api/benefits'(controller: 'dictionary', action: 'benefits', method: HttpMethod.GET)
        '/api/email-template-variables'(controller: 'dictionary', action: 'emailTemplateVariables', method: HttpMethod.GET)

        //Taxes
        '/api/taxes'(controller: 'tax', action: 'estimate', method: HttpMethod.POST)

        //SuperAdmin
        '/api/admin/questionnaire-version'(controller: 'superAdmin', action: 'getQuestionnaireVersion', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion"(controller: 'superAdmin', action: 'getBenefitCategory', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion/benefit_category/$benefitCategoryId"(controller: 'superAdmin', action: 'getForm', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion/benefit_category/$benefitCategoryId/form/$formId"(controller: 'superAdmin', action: 'getSection', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion/form/$formId/section/$sectionId"(controller: 'superAdmin', action: 'getSubSection', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion/form/$formId/sub_section/$subsectionId"(controller: 'superAdmin', action: 'getQuestion', method: HttpMethod.GET)
        "/api/admin/questionnaire-version/$questVersion/question/$questionId"(controller: 'superAdmin', action: 'getMetadata', method: HttpMethod.GET)
    }

}

