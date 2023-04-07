ALTER TABLE answer ALTER COLUMN path SET NOT NULL;
ALTER TABLE answer add constraint uniq_package_applicant_path unique (package_id, applicant_id, path);

