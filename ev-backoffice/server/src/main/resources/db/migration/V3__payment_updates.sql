ALTER TABLE ev_user ADD paid boolean NOT null default 'true';
ALTER TABLE ev_user ALTER COLUMN paid DROP DEFAULT;
ALTER TABLE ev_user ADD payment_token varchar(255) NULL;

ALTER TABLE legal_representative DROP COLUMN paid;

DROP TABLE charge;

CREATE TABLE public.account_transaction (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date timestamp NOT NULL,
    referral_id bigint NULL,
    article_id bigint NULL,
    memo varchar(255) NOT NULL,
    user_id bigint NOT NULL,
    amount numeric(19,2) NOT NULL,
    fm_transaction_id varchar(255) NULL,
    source varchar(255) NOT NULL,
    CONSTRAINT account_transaction_pkey PRIMARY KEY (id),
    CONSTRAINT fk2kxbic2vd66taljs2b7mi2lx0 FOREIGN KEY (referral_id) REFERENCES ev_user(id),
    CONSTRAINT fke76wtmtfj9v2un6gdy3gmvt8y FOREIGN KEY (user_id) REFERENCES ev_user(id),
    CONSTRAINT fkobt4ewens9lb7u88mns2drued FOREIGN KEY (article_id) REFERENCES article(id)
);

