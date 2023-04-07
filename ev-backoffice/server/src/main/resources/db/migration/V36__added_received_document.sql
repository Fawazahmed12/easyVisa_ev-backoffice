CREATE TABLE received_document (
    id bigint NOT NULL,
    received_document_type character varying(255) NOT NULL,
    received_date timestamp without time zone null,
    is_approved boolean null,
    CONSTRAINT received_document_pkey PRIMARY KEY (id)
);