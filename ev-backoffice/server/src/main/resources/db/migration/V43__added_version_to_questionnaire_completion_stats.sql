ALTER TABLE questionnaire_completion_stats ADD COLUMN questionnaire_version_id bigint;
ALTER TABLE questionnaire_completion_stats ADD CONSTRAINT fk_completion_stats_version_id FOREIGN KEY (questionnaire_version_id) REFERENCES questionnaire_version(id);




