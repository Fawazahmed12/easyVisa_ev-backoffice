ALTER TABLE petitioner DROP COLUMN elis_account_number;
ALTER TABLE petitioner ADD opt_in VARCHAR(255) NOT NULL DEFAULT 'PENDING';
ALTER TABLE petitioner ADD applicant_id bigint NOT NULL DEFAULT 0;
UPDATE petitioner SET applicant_id = id;
ALTER TABLE petitioner ADD CONSTRAINT fk_petitioner_applicant_id FOREIGN KEY (applicant_id) REFERENCES applicant(id);
ALTER TABLE petitioner ADD version bigint NOT NULL DEFAULT 0;