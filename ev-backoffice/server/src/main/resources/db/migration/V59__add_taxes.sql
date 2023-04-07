CREATE TABLE tax (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp NOT NULL,
    total numeric(19,2) NOT NULL,
    ava_tax_id varchar(255) NOT NULL,
    billing_address_id bigint NOT NULL,
    CONSTRAINT tax_pkey PRIMARY KEY (id),
    CONSTRAINT fk_tax_to_address FOREIGN KEY (billing_address_id) REFERENCES address(id)
);

ALTER TABLE account_transaction ADD tax_id bigint NULL;
ALTER TABLE account_transaction ADD CONSTRAINT fk_account_transaction_to_tax FOREIGN KEY (tax_id) REFERENCES tax(id);
