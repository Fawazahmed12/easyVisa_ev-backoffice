ALTER TABLE article RENAME COLUMN category TO category_name;
ALTER TABLE article ADD category_id varchar(512) NULL;
ALTER TABLE article ADD status varchar(255) NOT NULL DEFAULT 'SUBMITTED';
ALTER TABLE article ADD date_created timestamp NULL;
ALTER TABLE article ADD last_updated timestamp NULL;
ALTER TABLE article ADD rejected_message text NULL;

UPDATE article SET category_id = 1, date_created = date_submitted, last_updated = date_submitted;

ALTER TABLE article ALTER COLUMN category_id SET NOT NULL;
ALTER TABLE article ALTER COLUMN date_created SET NOT NULL;
ALTER TABLE article ALTER COLUMN last_updated SET NOT NULL;
ALTER TABLE article ALTER COLUMN date_submitted DROP NOT NULL;
