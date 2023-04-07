CREATE TABLE ev_user_device (
    id bigint NOT NULL,
    version bigint NOT NULL,
    user_id bigint NOT NULL,
    user_agent character varying(255) NOT NULL,
    devices_idx int NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    CONSTRAINT ev_user_device_pkey PRIMARY KEY (id),
    CONSTRAINT user_device_to_user_fkey FOREIGN KEY (user_id) REFERENCES ev_user(id)
);

CREATE SEQUENCE ev_user_device_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
