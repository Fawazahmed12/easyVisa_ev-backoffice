ALTER TABLE document_attachment ADD COLUMN is_read boolean NOT null default 'false';
ALTER TABLE document_attachment ADD COLUMN disposition_date timestamp without time zone DEFAULT NULL;
ALTER TABLE document_attachment ADD COLUMN disposition_by_id bigint DEFAULT NULL;
ALTER TABLE document_attachment ADD COLUMN rejection_mail_message text DEFAULT NULL;
ALTER TABLE document_attachment ADD CONSTRAINT fk_document_reference_id FOREIGN KEY (document_reference_id) REFERENCES base_document(id);
ALTER TABLE sent_document ALTER COLUMN sent_date TYPE date;
ALTER TABLE received_document ALTER COLUMN received_date TYPE date;
ALTER TABLE document_milestone ALTER COLUMN milestone_date TYPE date;