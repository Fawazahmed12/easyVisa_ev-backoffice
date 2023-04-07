ALTER TABLE package ADD COLUMN transferred_by_id bigint NULL;
ALTER TABLE package ADD COLUMN transferred_on timestamp without time zone NULL;
ALTER TABLE package ADD COLUMN transferred_to_id bigint NULL;
ALTER TABLE package ADD COLUMN transferred_attorney_to_id bigint NULL;
ALTER TABLE package DROP CONSTRAINT package_easy_visa_id_key;

ALTER TABLE ONLY package
    ADD CONSTRAINT package_transferred_by_to_profile FOREIGN KEY (transferred_by_id) REFERENCES profile(id);
ALTER TABLE ONLY package
    ADD CONSTRAINT package_transferred_attorney_to_to_legal_rep FOREIGN KEY (transferred_attorney_to_id) REFERENCES legal_representative(id);
ALTER TABLE ONLY package
    ADD CONSTRAINT package_transferred_package_to_package FOREIGN KEY (transferred_to_id) REFERENCES package(id);
