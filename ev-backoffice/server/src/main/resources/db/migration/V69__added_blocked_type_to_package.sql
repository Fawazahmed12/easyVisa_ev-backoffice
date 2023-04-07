ALTER TABLE package ADD COLUMN blocked_type character varying(255) NULL;
UPDATE package SET blocked_type = 'PAYMENT' WHERE status = 'BLOCKED';
