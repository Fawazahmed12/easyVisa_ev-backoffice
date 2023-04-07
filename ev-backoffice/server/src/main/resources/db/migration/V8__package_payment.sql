ALTER TABLE immigration_benefit ADD applicant_transaction_id bigint NULL;

ALTER TABLE immigration_benefit ADD CONSTRAINT fk_immigration_benefit_to_account_transaction FOREIGN KEY (applicant_transaction_id) REFERENCES account_transaction(id);
