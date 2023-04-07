CREATE TABLE questionnaire_version (
    id bigint NOT NULL,
    version bigint NOT NULL,
    quest_version character varying(255) NOT NULL,
    start_date timestamp without time zone,
    end_date timestamp without time zone DEFAULT NULL,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    CONSTRAINT ev_version_status_pkey PRIMARY KEY (id),
    CONSTRAINT uniq_questionnaire_version unique (quest_version)
);

CREATE TABLE package_questionnaire_version (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_package_id bigint NOT NULL,
    questionnaire_version_id bigint NOT NULL,
    latest boolean NOT null default 'true',
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    CONSTRAINT package_questionnaire_version_pkey PRIMARY KEY (id),
    CONSTRAINT fk_package_questionnaire_version_questionnaire_version_id FOREIGN KEY (questionnaire_version_id) REFERENCES questionnaire_version(id),
    CONSTRAINT fk_package_questionnaire_version_package_id FOREIGN KEY (a_package_id) REFERENCES package(id)
);


ALTER TABLE petitioner_beneficiary_mapping ADD quest_version character varying(255) NOT NULL DEFAULT 'quest_version_1';




