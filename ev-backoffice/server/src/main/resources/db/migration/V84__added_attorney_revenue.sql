CREATE TABLE legal_representative_revenue (
    id bigint NOT NULL,
    version bigint NOT NULL,
    revenue numeric(19, 2) NOT NULL,
    memo character varying(255) NOT NULL,
    a_package_id bigint,
    organization_id bigint NOT NULL,
    attorney_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    CONSTRAINT attorney_revenue_pkey PRIMARY KEY (id),
    CONSTRAINT attorney_revenue_package_fk FOREIGN KEY (a_package_id) REFERENCES package(id),
    CONSTRAINT attorney_revenue_organization_fk FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT attorney_revenue_legal_rep_fk FOREIGN KEY (attorney_id) REFERENCES legal_representative(id)
);

CREATE SEQUENCE legal_representative_revenue_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
