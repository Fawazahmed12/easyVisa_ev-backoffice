ALTER TABLE account_transaction ADD immigration_benefit_id bigint NULL;
ALTER TABLE account_transaction ADD applicant_transactions_idx int NULL;

ALTER TABLE immigration_benefit ADD paid boolean NULL;
ALTER TABLE immigration_benefit ADD per_applicant_fee numeric(19,2) NULL;

UPDATE immigration_benefit ib SET paid = true, per_applicant_fee = (SELECT amount FROM account_transaction act WHERE act.id = ib.applicant_transaction_id)
WHERE ib.applicant_transaction_id is not null;

UPDATE account_transaction act SET applicant_transactions_idx = 0, immigration_benefit_id = (SELECT ib.id FROM immigration_benefit ib WHERE ib.applicant_transaction_id = act.id)
WHERE act.id in (SELECT ib.applicant_transaction_id FROM immigration_benefit ib WHERE ib.applicant_transaction_id is not null);

UPDATE immigration_benefit SET paid = false
WHERE paid is null;

ALTER TABLE immigration_benefit DROP COLUMN applicant_transaction_id;
ALTER TABLE immigration_benefit ALTER COLUMN paid SET NOT NULL;
