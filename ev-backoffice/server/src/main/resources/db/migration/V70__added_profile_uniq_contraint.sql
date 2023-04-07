CREATE EXTENSION IF NOT EXISTS citext;
ALTER TABLE profile ALTER COLUMN email TYPE citext;
CREATE UNIQUE INDEX email_unique_idx ON profile (email) WHERE (email = '') IS NOT TRUE;

