ALTER TABLE email_template ADD COLUMN preference_id bigint NULL;
ALTER TABLE email_preference ADD COLUMN repeat_interval integer NULL;
