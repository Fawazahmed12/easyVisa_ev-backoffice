CREATE TABLE document_milestone (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_package_id bigint NOT NULL,
    milestone_date timestamp without time zone,
    document_milestone_type character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT document_milestone_pkey PRIMARY KEY (id),
    CONSTRAINT fk_document_milestone_package_id FOREIGN KEY (a_package_id) REFERENCES package(id),
    CONSTRAINT uniq_package_milestonetype_path unique (a_package_id, document_milestone_type)
);