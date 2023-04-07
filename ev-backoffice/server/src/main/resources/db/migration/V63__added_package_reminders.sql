CREATE TABLE package_reminder (
    id bigint NOT NULL,
    version bigint NOT NULL,
    notification_type varchar(255) NOT NULL,
    last_sent timestamp without time zone NOT NULL,
    stopped boolean NOT NULL,
    a_package_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    CONSTRAINT package_reminder_pkey PRIMARY KEY (id),
    CONSTRAINT package_reminder_package_fkey FOREIGN KEY (a_package_id) REFERENCES package(id),
    CONSTRAINT uniq_package_notif_type UNIQUE (a_package_id, notification_type)
);
