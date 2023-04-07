ALTER TABLE package ADD easy_visa_id varchar(255) NULL;
ALTER TABLE admin_settings ADD package_sequence_no bigint NOT NULL DEFAULT 0;
ALTER table admin_settings ALTER column package_sequence_no DROP DEFAULT;
ALTER TABLE organization RENAME COLUMN organization_id TO easy_visa_id;
