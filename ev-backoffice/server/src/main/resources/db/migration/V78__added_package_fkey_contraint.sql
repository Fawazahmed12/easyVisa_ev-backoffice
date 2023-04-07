ALTER TABLE answer ADD CONSTRAINT answer_packge_fkey FOREIGN KEY (package_id) REFERENCES package(id);
ALTER TABLE answer ADD CONSTRAINT answer_applicnt_fkey FOREIGN KEY (applicant_id) REFERENCES applicant(id);

ALTER TABLE section_completion_status ADD CONSTRAINT section_completion_status_packge_fkey FOREIGN KEY (package_id) REFERENCES package(id);
ALTER TABLE section_completion_status ADD CONSTRAINT section_completion_status_applicnt_fkey FOREIGN KEY (applicant_id) REFERENCES applicant(id);



