ALTER TABLE document_milestone DROP COLUMN document_milestone_type;
ALTER TABLE document_milestone ADD COLUMN milestone_type_id VARCHAR(255) NOT NULL;
