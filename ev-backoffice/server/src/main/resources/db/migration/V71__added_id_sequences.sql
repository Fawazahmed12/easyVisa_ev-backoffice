--AccountTransaction
CREATE SEQUENCE account_transaction_id_seq;
--first set up need to existing data, empty tables will have null that will be treated as 0 for current value
SELECT setval('account_transaction_id_seq', (SELECT max(id) FROM account_transaction));
--AdditionalFee
CREATE SEQUENCE additional_fee_id_seq;
SELECT setval('additional_fee_id_seq', (SELECT max(id) FROM additional_fee));
--Address
CREATE SEQUENCE address_id_seq;
SELECT setval('address_id_seq', (SELECT max(id) FROM address));
--AdminConfig
CREATE SEQUENCE admin_config_id_seq;
SELECT setval('admin_config_id_seq', (SELECT max(id) FROM admin_config));
--AdminSettings
CREATE SEQUENCE admin_settings_id_seq;
SELECT setval('admin_settings_id_seq', (SELECT max(id) FROM admin_settings));
--Answer
CREATE SEQUENCE answer_id_seq;
SELECT setval('answer_id_seq', (SELECT max(id) FROM answer));
--AppConfig
CREATE SEQUENCE app_config_id_seq;
SELECT setval('app_config_id_seq', (SELECT max(id) FROM app_config));
--Article
CREATE SEQUENCE article_id_seq;
SELECT setval('article_id_seq', (SELECT max(id) FROM article));
--AuditLog
CREATE SEQUENCE audit_log_id_seq;
SELECT setval('audit_log_id_seq', (SELECT max(id) FROM audit_log));
--AuthenticationToken
CREATE SEQUENCE authentication_token_id_seq;
SELECT setval('authentication_token_id_seq', (SELECT max(id) FROM authentication_token));
--BaseDocument + ReceivedDocument + RequiredDocument + SentDocument
CREATE SEQUENCE base_document_id_seq;
SELECT setval('base_document_id_seq', (SELECT max(id) FROM base_document));
--DocumentAttachment
CREATE SEQUENCE document_attachment_id_seq;
SELECT setval('document_attachment_id_seq', (SELECT max(id) FROM document_attachment));
--DocumentCompletionStatus
CREATE SEQUENCE document_completion_status_id_seq;
SELECT setval('document_completion_status_id_seq', (SELECT max(id) FROM document_completion_status));
--DocumentMilestone
CREATE SEQUENCE document_milestone_id_seq;
SELECT setval('document_milestone_id_seq', (SELECT max(id) FROM document_milestone));
--DocumentNote
CREATE SEQUENCE document_note_id_seq;
SELECT setval('document_note_id_seq', (SELECT max(id) FROM document_note));
--EasyVisaFile
CREATE SEQUENCE easy_visa_file_id_seq;
SELECT setval('easy_visa_file_id_seq', (SELECT max(id) FROM easy_visa_file));
--Education
CREATE SEQUENCE education_id_seq;
SELECT setval('education_id_seq', (SELECT max(id) FROM education));
--Email
CREATE SEQUENCE email_id_seq;
SELECT setval('email_id_seq', (SELECT max(id) FROM email));
--EmailPreference
CREATE SEQUENCE email_preference_id_seq;
SELECT setval('email_preference_id_seq', (SELECT max(id) FROM email_preference));
--EmailTemplate
CREATE SEQUENCE email_template_id_seq;
SELECT setval('email_template_id_seq', (SELECT max(id) FROM email_template));
--Employee + LegalRepresentative
CREATE SEQUENCE employee_id_seq;
SELECT setval('employee_id_seq', (select max(id) from employee));
--EvSystemMessage + Alert + Warning
CREATE SEQUENCE ev_system_message_id_seq;
SELECT setval('ev_system_message_id_seq', (select max(id) from ev_system_message));
--User
CREATE SEQUENCE ev_user_id_seq;
SELECT setval('ev_user_id_seq', (select max(id) from ev_user));
--Fee
CREATE SEQUENCE fee_id_seq;
SELECT setval('fee_id_seq', (select max(id) from fee));
--ImmigrationBenefit
CREATE SEQUENCE immigration_benefit_id_seq;
SELECT setval('immigration_benefit_id_seq', (select max(id) from immigration_benefit));
--ImmigrationBenefitAssetValue
CREATE SEQUENCE immigration_benefit_asset_value_id_seq;
SELECT setval('immigration_benefit_asset_value_id_seq', (select max(id) from immigration_benefit_asset_value));
--LicensedRegion
CREATE SEQUENCE licensed_region_id_seq;
SELECT setval('licensed_region_id_seq', (select max(id) from licensed_region));
--Organization
CREATE SEQUENCE organization_id_seq;
SELECT setval('organization_id_seq', (select max(id) from organization));
--OrganizationEmployee
CREATE SEQUENCE organization_employee_id_seq;
SELECT setval('organization_employee_id_seq', (select max(id) from organization_employee));
--PackageAssignee
CREATE SEQUENCE package_assignee_id_seq;
SELECT setval('package_assignee_id_seq', (select max(id) from package_assignee));
--PackageQuestionnaireVersion
CREATE SEQUENCE package_questionnaire_version_id_seq;
SELECT setval('package_questionnaire_version_id_seq', (select max(id) from package_questionnaire_version));
--PackageReminder
CREATE SEQUENCE package_reminder_id_seq;
SELECT setval('package_reminder_id_seq', (select max(id) from package_reminder));
--PaymentMethod
CREATE SEQUENCE payment_method_id_seq;
SELECT setval('payment_method_id_seq', (select max(id) from payment_method));
--Petitioner
CREATE SEQUENCE petitioner_id_seq;
SELECT setval('petitioner_id_seq', (select max(id) from petitioner));
--PetitionerBeneficiaryMapping
CREATE SEQUENCE petitioner_beneficiary_mapping_id_seq;
SELECT setval('petitioner_beneficiary_mapping_id_seq', (select max(id) from petitioner_beneficiary_mapping));
--PovertyGuideline
CREATE SEQUENCE poverty_guideline_id_seq;
SELECT setval('poverty_guideline_id_seq', (select max(id) from poverty_guideline));
--ProcessRequest
CREATE SEQUENCE process_request_id_seq;
SELECT setval('process_request_id_seq', (select max(id) from process_request));
--Profile
CREATE SEQUENCE profile_id_seq;
SELECT setval('profile_id_seq', (select max(id) from profile));
--ProspectCounts
CREATE SEQUENCE prospect_counts_id_seq;
SELECT setval('prospect_counts_id_seq', (select max(id) from prospect_counts));
--QuestionnaireCompletionStats
CREATE SEQUENCE questionnaire_completion_stats_id_seq;
SELECT setval('questionnaire_completion_stats_id_seq', (select max(id) from questionnaire_completion_stats));
--QuestionnaireVersion
CREATE SEQUENCE questionnaire_version_id_seq;
SELECT setval('questionnaire_version_id_seq', (select max(id) from questionnaire_version));
--RegistrationCode
CREATE SEQUENCE registration_code_id_seq;
SELECT setval('registration_code_id_seq', (select max(id) from registration_code));
--Review
CREATE SEQUENCE review_id_seq;
SELECT setval('review_id_seq', (select max(id) from review));
--Role
CREATE SEQUENCE role_id_seq;
SELECT setval('role_id_seq', (select max(id) from role));
--Tax
CREATE SEQUENCE tax_id_seq;
SELECT setval('tax_id_seq', (select max(id) from tax));
--SectionCompletionStatus
CREATE SEQUENCE section_completion_status_id_seq;
SELECT setval('section_completion_status_id_seq', (select max(id) from section_completion_status));
--UscisEditionDate
CREATE SEQUENCE uscis_edition_date_id_seq;
SELECT setval('uscis_edition_date_id_seq', (select max(id) from uscis_edition_date));
--WorkingHour
CREATE SEQUENCE working_hour_id_seq;
SELECT setval('working_hour_id_seq', (select max(id) from working_hour));

--EV ID sequences
CREATE SEQUENCE client_ev_id_seq INCREMENT 7;
SELECT setval('client_ev_id_seq', (select client_sequence_no from admin_settings));
CREATE SEQUENCE legal_representative_ev_id_seq INCREMENT 7;
SELECT setval('legal_representative_ev_id_seq', (select representative_sequence_no from admin_settings));
CREATE SEQUENCE employee_ev_id_seq INCREMENT 7;
SELECT setval('employee_ev_id_seq', (select client_employee_sequence_no from admin_settings));
CREATE SEQUENCE organization_ev_id_seq INCREMENT 13;
SELECT setval('organization_ev_id_seq', (select organization_sequence_no from admin_settings));
CREATE SEQUENCE package_ev_id_seq INCREMENT 7;
SELECT setval('package_ev_id_seq', (select package_sequence_no from admin_settings));
ALTER TABLE admin_settings DROP COLUMN owner_sequence_no;
ALTER TABLE admin_settings DROP COLUMN ev_employee_sequence_no;
ALTER TABLE admin_settings DROP COLUMN client_sequence_no;
ALTER TABLE admin_settings DROP COLUMN representative_sequence_no;
ALTER TABLE admin_settings DROP COLUMN client_employee_sequence_no;
ALTER TABLE admin_settings DROP COLUMN organization_sequence_no;
ALTER TABLE admin_settings DROP COLUMN package_sequence_no;

--Document
DROP TABLE document;
--DocumentAction
DROP TABLE document_action;
--QuestionnaireCompletionConfig
DROP TABLE IF EXISTS questionnaire_completion_config;
