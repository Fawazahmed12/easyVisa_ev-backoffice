UPDATE email_preference SET type = 'APPLICANT_DOCUMENT' WHERE type = 'PETITIONER_DOCUMENT';

DELETE FROM profile_email_preference WHERE email_preference_id in (SELECT id FROM email_preference WHERE type = 'BENEFICIARY_DOCUMENT');
DELETE FROM email_preference WHERE type = 'BENEFICIARY_DOCUMENT';