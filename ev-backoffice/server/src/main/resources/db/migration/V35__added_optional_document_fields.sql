ALTER TABLE document_attachment ALTER COLUMN is_approved DROP NOT NULL;
ALTER TABLE sent_document ALTER COLUMN sent_date SET DEFAULT NULL;