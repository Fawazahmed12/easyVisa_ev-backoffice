CREATE TABLE base_document (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_package_id bigint NOT NULL,
    applicant_id bigint NOT NULL,
    document_type character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT base_document_pkey PRIMARY KEY (id),
    CONSTRAINT fk_base_document_applicant_id FOREIGN KEY (applicant_id) REFERENCES applicant(id),
    CONSTRAINT fk_base_document_package_id FOREIGN KEY (a_package_id) REFERENCES package(id)
);

CREATE TABLE required_document (
    id bigint NOT NULL,
    document_id character varying(255) NOT NULL,
    is_approved boolean NOT null default 'false',
    CONSTRAINT required_document_pkey PRIMARY KEY (id)
);



CREATE TABLE document_attachment (
    id bigint NOT NULL,
    version bigint NOT NULL,
    document_reference_id bigint NOT NULL,
    document_type character varying(255) NOT NULL,
    file_id bigint NOT NULL,
    is_approved boolean,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT document_attachment_pkey PRIMARY KEY (id),
    CONSTRAINT fk_document_attachment_file_id FOREIGN KEY (file_id) REFERENCES easy_visa_file(id)
);



