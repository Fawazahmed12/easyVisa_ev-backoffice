ALTER TABLE package ADD COLUMN questionnaire_sync_status character varying(255) NULL;

UPDATE package SET questionnaire_sync_status = 'COMPLETED' WHERE status <> 'LEAD';
