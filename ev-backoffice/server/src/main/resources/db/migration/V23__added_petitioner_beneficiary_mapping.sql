CREATE TABLE petitioner_beneficiary_mapping (
    id bigint NOT NULL,
    version bigint NOT NULL,
    petitioner_section_nodeid character varying(255) NOT NULL,
    petitioner_subsection_nodeid character varying(255) NOT NULL,
    petitioner_question_nodeid character varying(255) NOT NULL,
    petitioner_repeatingquestiongroup boolean NOT NULL,
    beneficiary_section_nodeid character varying(255) NOT NULL,
    beneficiary_subsection_nodeid character varying(255) NOT NULL,
    beneficiary_question_nodeid character varying(255) NOT NULL,
    beneficiary_repeatingquestiongroup boolean NOT NULL,
    CONSTRAINT petitioner_beneficiary_mapping_pkey PRIMARY KEY (id)
);
