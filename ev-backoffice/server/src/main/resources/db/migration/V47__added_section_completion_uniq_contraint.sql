ALTER TABLE section_completion_status add constraint uniq_package_applicant_section unique (package_id, applicant_id, section_id);

