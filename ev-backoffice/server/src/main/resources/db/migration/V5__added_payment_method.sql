DROP TABLE payment;

ALTER TABLE ev_user DROP COLUMN payment_token;

CREATE TABLE payment_method (
    id bigint NOT NULL,
    version bigint NOT NULL,
    user_id bigint NOT NULL,
    fm_payment_method_id varchar(255) NOT NULL,
    card_holder varchar(255) NULL,
    card_type varchar(255) NULL,
    card_last_four varchar(4) NULL,
    card_expiration varchar(6) NULL,
    address1 varchar(255) NULL,
    address2 varchar(255) NULL,
    address_city varchar(255) NULL,
    address_state varchar(255) NULL,
    address_country varchar(255) NULL,
    address_zip varchar(255) NULL,
    CONSTRAINT payment_method_pkey PRIMARY KEY (id),
    CONSTRAINT payment_method_user_fkey FOREIGN KEY (user_id) REFERENCES ev_user(id)
);
