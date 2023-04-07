
CREATE TABLE uscis_edition_date (
    id bigint NOT NULL,
    version bigint NOT NULL,
    form_id character varying(255) NOT NULL,
    edition_date timestamp without time zone NOT NULL,
    expiration_date timestamp without time zone NOT NULL,
    organization_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    updated_by_id bigint,
    created_by_id bigint,
    CONSTRAINT uscis_edition_date_pkey PRIMARY KEY (id),
    CONSTRAINT uniq_uscis_edition_date UNIQUE (form_id),
    CONSTRAINT fk_uscis_edition_date_organization_id FOREIGN KEY (organization_id) REFERENCES organization(id)
);
