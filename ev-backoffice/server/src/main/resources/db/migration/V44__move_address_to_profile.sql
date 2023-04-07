ALTER TABLE profile ADD COLUMN address_id bigint;
ALTER TABLE profile ADD CONSTRAINT fk_profile_to_address FOREIGN KEY (address_id) REFERENCES address(id);

UPDATE profile SET address_id = l.office_address_id FROM legal_representative l WHERE profile.id = (SELECT e.profile_id FROM employee e WHERE e.id = l.id) ;

UPDATE profile SET address_id = a.home_id FROM applicant a WHERE profile.id = a.profile_id;

ALTER TABLE legal_representative DROP column office_address_id;
ALTER TABLE applicant DROP column home_id;