ALTER TABLE immigration_benefit DROP COLUMN opt_in;
ALTER TABLE immigration_benefit ADD opt_in VARCHAR(255) NOT NULL DEFAULT 'PENDING';
ALTER TABLE process_request ADD immigration_benefit_id bigint NULL;
ALTER TABLE process_request ADD petitioner_id bigint NULL;
ALTER TABLE process_request ADD a_package_id bigint NULL;