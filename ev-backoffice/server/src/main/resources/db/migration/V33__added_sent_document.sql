CREATE TABLE sent_document (
    id bigint NOT NULL,
    form_id character varying(255) NOT NULL,
    sent_date timestamp without time zone,
    CONSTRAINT sent_document_pkey PRIMARY KEY (id)
);