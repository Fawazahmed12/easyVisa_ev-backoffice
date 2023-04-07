create index idx_answer_pack on answer(package_id);
create index idx_answer_pack_path on answer(package_id, path);
create index idx_answer_pack_applicnt on answer(package_id, applicant_id);
create index idx_answer_pack_applicnt_path on answer(package_id, applicant_id, path);
create index idx_answer_pack_applicnt_section on answer(package_id, applicant_id, section_id);
create index idx_answer_pack_applicnt_question_index on answer(package_id, applicant_id, question_id, index);
create index idx_answer_pack_applicnt_section_subsection_question_index on answer(package_id, applicant_id, section_id, subsection_id, question_id, index);


create index idx_section_completion_status_pack on section_completion_status(package_id);
create index idx_section_completion_status_pack_applicant on section_completion_status(package_id, applicant_id);
create index idx_section_completion_status_pack_applicant_section on section_completion_status(package_id, applicant_id, section_id);


create index idx_questionnaire_completion_stats_benefit_applicant on questionnaire_completion_stats(benefit_category_id, applicant_type);
create index idx_questionnaire_completion_stats_benefit_applicant_version on questionnaire_completion_stats(benefit_category_id, applicant_type, questionnaire_version_id);
