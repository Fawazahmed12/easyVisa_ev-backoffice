CREATE TABLE app_config (
    id int8 NOT NULL,
    "version" int8 NOT NULL,
    type varchar(255) NOT NULL,
    value varchar(255) NOT NULL,
    date_created timestamp NOT NULL,
    last_updated timestamp NOT NULL,
    CONSTRAINT app_config_pkey PRIMARY KEY (id),
    CONSTRAINT uniq_type UNIQUE (type)
);