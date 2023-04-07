CREATE TABLE ev_system_message (
  id int8 NOT NULL,
  version int8 NOT NULL,
  is_starred bool NOT NULL,
  is_read bool NOT NULL,
  date_created timestamp NOT NULL,
  last_updated timestamp NOT NULL,
  body text NULL,
  message_type varchar(255) NULL,
  subject varchar(255) NOT NULL,
  source varchar(255) NULL,
  CONSTRAINT ev_system_message_pkey PRIMARY KEY (id)
);


INSERT INTO ev_system_message (id, version, is_starred, is_read, date_created, last_updated, body, message_type, subject, source)
SELECT id, version, is_starred, is_read, date_created, last_updated, body, alert_type, subject, source FROM alert;

ALTER TABLE warning DROP COLUMN representative_id;
ALTER TABLE alert DROP COLUMN version;
ALTER TABLE alert DROP COLUMN is_starred;
ALTER TABLE alert DROP COLUMN is_read;
ALTER TABLE alert DROP COLUMN date_created;
ALTER TABLE alert DROP COLUMN last_updated;
ALTER TABLE alert DROP COLUMN body;
ALTER TABLE alert DROP COLUMN alert_type;
ALTER TABLE alert DROP COLUMN subject;
ALTER TABLE alert DROP COLUMN source;
