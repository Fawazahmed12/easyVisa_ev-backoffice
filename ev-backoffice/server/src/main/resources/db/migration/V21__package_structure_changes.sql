ALTER TABLE petitioner RENAME COLUMN petitioner_status TO citizenship_status;
ALTER TABLE immigration_benefit ADD citizenship_status varchar(255) NULL;
ALTER TABLE package ALTER COLUMN petitioner_id DROP NOT NULL;
