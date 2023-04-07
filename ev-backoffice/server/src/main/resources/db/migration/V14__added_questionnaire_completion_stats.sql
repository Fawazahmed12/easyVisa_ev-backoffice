CREATE TABLE questionnaire_completion_stats (
    id bigint NOT NULL,
    version bigint NOT NULL,
    benefit_category_id character varying(255) NOT NULL,
    section_id character varying(255) NOT NULL,
    applicant_type character varying(255) NOT NULL,
    section_questions_count integer NOT NULL,
    benefit_category_questions_count integer NOT NULL,
    weightage_value double precision NOT NULL,
    CONSTRAINT questionnaire_completion_stats_pkey PRIMARY KEY (id)
);
