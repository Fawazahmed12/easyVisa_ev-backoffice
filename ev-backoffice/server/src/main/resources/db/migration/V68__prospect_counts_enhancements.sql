ALTER TABLE prospect_counts ADD COLUMN prospect_type character varying(255) NOT NULL;
ALTER TABLE prospect_counts ADD COLUMN date_created timestamp NOT NULL;
ALTER TABLE prospect_counts ADD COLUMN last_updated timestamp NOT NULL;
ALTER TABLE prospect_counts ALTER COLUMN search_date TYPE timestamp;
ALTER TABLE prospect_counts DROP COLUMN profile_views;
ALTER TABLE prospect_counts DROP COLUMN contact_info_views;
