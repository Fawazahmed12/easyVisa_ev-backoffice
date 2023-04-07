ALTER TABLE admin_config DROP COLUMN applicant_fee;
ALTER TABLE admin_config RENAME COLUMN biometric_serivce_fee TO biometric_service_fee;
ALTER TABLE admin_config ADD attorney_id bigint NULL;
