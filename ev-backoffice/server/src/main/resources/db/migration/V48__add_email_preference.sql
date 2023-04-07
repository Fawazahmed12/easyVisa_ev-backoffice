CREATE TABLE email_preference (
    id bigint NOT NULL,
    version bigint NOT NULL,
    preference boolean NOT NULL,
    type varchar(255) NOT NULL,
    CONSTRAINT email_preference_pkey PRIMARY KEY (id)
);

CREATE TABLE profile_email_preference (
    profile_email_preferences_id bigint NOT NULL,
    email_preference_id bigint NULL,
    CONSTRAINT fk_to_email_preference FOREIGN KEY (email_preference_id) REFERENCES email_preference(id),
    CONSTRAINT fk_to_profile FOREIGN KEY (profile_email_preferences_id) REFERENCES profile(id)
);
