CREATE TABLE document_completion_status (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_package_id bigint NOT NULL,
    document_type character varying(255) NOT NULL,
    completed_percentage float default 0,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT document_completion_pkey PRIMARY KEY (id),
    CONSTRAINT fk_document_completion_package_id FOREIGN KEY (a_package_id) REFERENCES package(id),
    CONSTRAINT uniq_package_document_completion unique (a_package_id, document_type)
);

