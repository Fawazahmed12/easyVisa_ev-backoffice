ALTER TABLE legal_representative ADD COLUMN public_no_of_reviews bigint NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN public_avg_review_rating numeric(19,2) NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN public_no_of_approved_articles bigint NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN public_max_years_licensed integer NULL DEFAULT 0;

CREATE INDEX idx_employee_profile ON employee(profile_id);
CREATE INDEX idx_profile_user ON profile(user_id);
CREATE INDEX idx_profile_address ON profile(address_id);
CREATE INDEX idx_employee_spoken_languages_employee ON employee_spoken_languages(employee_id);
CREATE INDEX idx_employee_spoken_languages_language ON employee_spoken_languages(language);
CREATE INDEX idx_address_state ON address(state);
CREATE INDEX idx_address_country ON address(country);
