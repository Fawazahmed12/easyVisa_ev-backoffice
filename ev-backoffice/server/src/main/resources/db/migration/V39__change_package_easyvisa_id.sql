ALTER table package ALTER column easy_visa_id set NOT NULL;
ALTER TABLE package ADD UNIQUE (easy_visa_id);
