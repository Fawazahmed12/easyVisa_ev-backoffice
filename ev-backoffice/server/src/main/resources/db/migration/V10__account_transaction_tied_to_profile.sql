ALTER TABLE account_transaction ADD COLUMN profile_id bigint NULL;
UPDATE account_transaction atr SET profile_id = (SELECT id FROM profile p WHERE p.user_id = atr.user_id);
ALTER TABLE account_transaction ALTER COLUMN profile_id SET NOT NULL;
ALTER TABLE account_transaction ADD CONSTRAINT account_transaction_profile_fk FOREIGN KEY (profile_id) REFERENCES profile(id);
ALTER TABLE account_transaction DROP column user_id;
ALTER TABLE account_transaction DROP CONSTRAINT fk2kxbic2vd66taljs2b7mi2lx0;
ALTER TABLE account_transaction ADD CONSTRAINT account_transaction_referral_profile_fk FOREIGN KEY (referral_id) REFERENCES profile(id);
