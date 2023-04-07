CREATE TABLE document_note (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_package_id bigint NOT NULL,
    creator_id bigint NOT NULL,
    document_note_type character varying(255) NOT NULL,
    subject text NOT NULL,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT document_note_pkey PRIMARY KEY (id),
    CONSTRAINT fk_document_note_creator_id FOREIGN KEY (creator_id) REFERENCES profile(id),
    CONSTRAINT fk_document_note_package_id FOREIGN KEY (a_package_id) REFERENCES package(id)
);


