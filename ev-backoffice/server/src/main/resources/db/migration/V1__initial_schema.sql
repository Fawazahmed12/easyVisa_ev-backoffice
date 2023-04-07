CREATE TABLE additional_fee (
    id bigint NOT NULL,
    version bigint NOT NULL,
    fee numeric(19,2) NOT NULL,
    quantity integer NOT NULL,
    a_package_id bigint NOT NULL,
    description character varying(255) NOT NULL
);

CREATE TABLE address (
    id bigint NOT NULL,
    version bigint NOT NULL,
    line2 character varying(255),
    province character varying(255),
    date_created timestamp without time zone NOT NULL,
    postal_code character varying(255),
    line1 character varying(255),
    last_updated timestamp without time zone NOT NULL,
    city character varying(255),
    country character varying(255),
    zip_code character varying(255),
    state character varying(255)
);

CREATE TABLE admin_config (
    id bigint NOT NULL,
    version bigint NOT NULL,
    i601 numeric(19,2),
    i485_14 numeric(19,2),
    i601a numeric(19,2),
    signup_discount numeric(19,2) NOT NULL,
    i360 numeric(19,2),
    referral_bonus numeric(19,2) NOT NULL,
    n400 numeric(19,2),
    membership_reactivation_fee numeric(19,2) NOT NULL,
    support_email character varying(255),
    article_bonus numeric(19,2) NOT NULL,
    i130 numeric(19,2),
    applicant_fee numeric(19,2) NOT NULL,
    i751 numeric(19,2),
    cloud_storage_fee numeric(19,2) NOT NULL,
    i600_600a numeric(19,2),
    i129f numeric(19,2),
    signup_fee numeric(19,2) NOT NULL,
    contact_phone character varying(255),
    i485 numeric(19,2),
    maintenance_fee numeric(19,2) NOT NULL,
    i765 numeric(19,2),
    n600_n600k numeric(19,2),
    biometric_serivce_fee numeric(19,2)
);

CREATE TABLE admin_settings (
    id bigint NOT NULL,
    version bigint NOT NULL,
    ev_employee_sequence_no bigint NOT NULL,
    representative_sequence_no bigint NOT NULL,
    client_sequence_no bigint NOT NULL,
    owner_sequence_no bigint NOT NULL,
    client_employee_sequence_no bigint NOT NULL,
    organization_sequence_no bigint NOT NULL,
    admin_config_id bigint
);

CREATE TABLE alert (
    id bigint NOT NULL,
    version bigint NOT NULL,
    is_starred boolean NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    body text,
    process_request_id bigint,
    source character varying(255),
    is_read boolean NOT NULL,
    alert_type character varying(255),
    recipient_id bigint NOT NULL,
    subject character varying(255) NOT NULL
);

CREATE TABLE answer (
    id bigint NOT NULL,
    version bigint NOT NULL,
    section_id character varying(255) NOT NULL,
    date_created timestamp without time zone,
    last_updated timestamp without time zone,
    path character varying(255),
    updated_by_id bigint,
    applicant_id bigint NOT NULL,
    index integer,
    question_id character varying(255) NOT NULL,
    value text,
    package_id bigint NOT NULL,
    created_by_id bigint,
    subsection_id character varying(255) NOT NULL
);

CREATE TABLE applicant (
    id bigint NOT NULL,
    version bigint NOT NULL,
    a_number character varying(255),
    profile_id bigint NOT NULL,
    home_id bigint,
    invite_applicant boolean NOT NULL,
    date_of_birth date,
    home_number character varying(255),
    mobile_number character varying(255),
    work_number character varying(255)
);

CREATE SEQUENCE applicant_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE article (
    id bigint NOT NULL,
    version bigint NOT NULL,
    body text NOT NULL,
    organization_id bigint NOT NULL,
    date_submitted timestamp without time zone NOT NULL,
    is_approved boolean,
    author_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    url character varying(255),
    words_count bigint NOT NULL,
    article_id character varying(255),
    category character varying(255) NOT NULL,
    views bigint NOT NULL,
    dispositioned timestamp without time zone
);


CREATE TABLE attorney_warning_template (
    id bigint NOT NULL,
    version bigint NOT NULL,
    easy_visa_id character varying(255) NOT NULL,
    warning_message character varying NOT NULL
);

CREATE TABLE audit_log (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    username character varying(255) NOT NULL,
    new_value text NOT NULL,
    old_value text NOT NULL,
    event character varying(255) NOT NULL
);


CREATE TABLE authentication_token (
    id bigint NOT NULL,
    version bigint NOT NULL,
    token_value text NOT NULL,
    username character varying(255) NOT NULL,
    last_used timestamp without time zone
);


CREATE TABLE charge (
    id bigint NOT NULL,
    version bigint NOT NULL,
    representative_id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    memo character varying(255) NOT NULL,
    amount numeric(19,2) NOT NULL,
    source character varying(255)
);


CREATE TABLE document (
    id bigint NOT NULL,
    version bigint NOT NULL,
    file_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    applicant_id bigint NOT NULL,
    a_package_id bigint NOT NULL,
    document_type character varying(255) NOT NULL,
    is_approved boolean
);

CREATE TABLE document_action (
    id bigint NOT NULL,
    version bigint NOT NULL,
    petitioner_contact_email character varying(255),
    message character varying,
    date_created timestamp without time zone NOT NULL,
    easy_visa_id character varying(255) NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    value character varying(255),
    package_id bigint NOT NULL,
    applicant_id bigint NOT NULL
);


CREATE TABLE easy_visa_file (
    id bigint NOT NULL,
    version bigint NOT NULL,
    uploader_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    path character varying(255) NOT NULL,
    approved boolean NOT NULL,
    s3key character varying(255),
    file_type character varying(255) NOT NULL,
    original_name character varying(255) NOT NULL
);


CREATE TABLE education (
    id bigint NOT NULL,
    version bigint NOT NULL,
    degree character varying(255),
    school character varying(255),
    honors character varying(255),
    year integer
);

CREATE TABLE email (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    attorney_id bigint,
    last_updated timestamp without time zone NOT NULL,
    a_package_id bigint,
    content text NOT NULL,
    template_type character varying(255) NOT NULL,
    subject character varying(255) NOT NULL
);

CREATE TABLE email_template (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    attorney_id bigint,
    is_fragment boolean NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    organization_id bigint,
    content character varying(255) NOT NULL,
    template_type character varying(255) NOT NULL,
    subject character varying(255)
);


CREATE TABLE employee (
    id bigint NOT NULL,
    version bigint NOT NULL,
    profile_id bigint NOT NULL,
    mobile_phone character varying(255),
    active_organization_id bigint,
    office_phone character varying(255),
    office_email character varying(255),
    fax_number character varying(255)
);

CREATE TABLE employee_organization (
    employee_linked_organizations_id bigint NOT NULL,
    organization_id bigint
);

CREATE TABLE employee_spoken_languages (
    employee_id bigint NOT NULL,
    language character varying(255) NOT NULL,
    spoken_languages_idx integer
);

CREATE TABLE ev_user (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    password_expired boolean NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    account_expired boolean NOT NULL,
    language character varying(255),
    is_email_verified boolean NOT NULL,
    username character varying(64) NOT NULL,
    account_locked boolean NOT NULL,
    password character varying(255) NOT NULL,
    enabled boolean NOT NULL,
    last_login timestamp without time zone
);

CREATE TABLE fee_schedule (
    id bigint NOT NULL,
    version bigint NOT NULL,
    fee numeric(19,2),
    representative_id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    organization_id bigint,
    benefit_category character varying(255) NOT NULL,
    fee_schedule_idx integer
);

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE immigration_benefit (
    id bigint NOT NULL,
    version bigint NOT NULL,
    fee numeric(19,2),
    date_created timestamp without time zone NOT NULL,
    direct boolean,
    last_updated timestamp without time zone NOT NULL,
    applicant_id bigint NOT NULL,
    sort_position bigint,
    category character varying(255) NOT NULL
);

CREATE TABLE immigration_benefit_asset_value (
    id bigint NOT NULL,
    version bigint NOT NULL,
    asset_value integer NOT NULL,
    easy_visa_id character varying(255) NOT NULL,
    benefit_category character varying(255) NOT NULL
);

CREATE TABLE legal_representative (
    id bigint NOT NULL,
    linkedin_url character varying(255),
    paid boolean NOT NULL,
    awards character varying(255),
    active_member_ship boolean NOT NULL,
    office_address_id bigint,
    facebook_url character varying(255),
    referrer_id bigint,
    website_url character varying(255),
    experience character varying(255),
    registration_status character varying(255) NOT NULL,
    twitter_url character varying(255),
    youtube_url character varying(255),
    credit_balance numeric(19,2),
    profile_summary character varying(255),
    uscis_online_account_no character varying(255),
    state_bar_number character varying(255),
    attorney_type character varying(255),
    representative_type character varying(255) NOT NULL
);

CREATE TABLE legal_representative_education (
    legal_representative_degrees_id bigint NOT NULL,
    education_id bigint
);

CREATE TABLE legal_representative_licensed_region (
    legal_representative_licensed_regions_id bigint NOT NULL,
    licensed_region_id bigint
);


CREATE TABLE legal_representative_practice_areas (
    legal_representative_id bigint NOT NULL,
    practice_area character varying(255)
);

CREATE TABLE legal_representative_working_hour (
    legal_representative_working_hours_id bigint NOT NULL,
    working_hour_id bigint
);

CREATE TABLE licensed_region (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_licensed date NOT NULL,
    bar_number character varying(255) NOT NULL,
    state character varying(255) NOT NULL
);

CREATE TABLE organization (
    id bigint NOT NULL,
    version bigint NOT NULL,
    linkedin_url character varying(255),
    awards character varying(255),
    mobile_phone character varying(255),
    facebook_url character varying(255),
    website_url character varying(255),
    year_founded bigint,
    experience character varying(255),
    twitter_url character varying(255),
    office_phone character varying(255),
    address_id bigint,
    youtube_url character varying(255),
    name character varying(255) NOT NULL,
    fax_number character varying(255),
    logo_file_id bigint,
    profile_summary character varying(255),
    organization_id character varying(255) NOT NULL,
    organization_type character varying(255) NOT NULL,
    email character varying(255)
);

CREATE TABLE organization_employee (
    id bigint NOT NULL,
    version bigint NOT NULL,
    inactive_date timestamp without time zone,
    "position" character varying(255) NOT NULL,
    active_date timestamp without time zone NOT NULL,
    is_admin boolean NOT NULL,
    organization_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    employee_id bigint NOT NULL
);

CREATE TABLE organization_practice_areas (
    organization_id bigint NOT NULL,
    practice_area character varying(255)
);

CREATE TABLE organization_roster_names (
    organization_id bigint NOT NULL,
    roster_names_string character varying(255)
);

CREATE TABLE organization_spoken_languages (
    organization_id bigint NOT NULL,
    language character varying(255)
);

CREATE TABLE organization_working_hour (
    organization_working_hours_id bigint NOT NULL,
    working_hour_id bigint
);

CREATE TABLE package (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    attorney_id bigint,
    last_updated timestamp without time zone NOT NULL,
    owed numeric(19,2),
    organization_id bigint,
    last_answered_on timestamp without time zone,
    opened timestamp without time zone,
    paid_by_legal_rep boolean,
    closed timestamp without time zone,
    title character varying(255),
    petitioner_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    welcome_email_sent_on timestamp without time zone,
    package_type character varying(255) NOT NULL,
    retainer_agreement_id bigint
);


CREATE TABLE package_applicant (
    package_applicants_id bigint NOT NULL,
    applicant_id bigint
);

CREATE TABLE package_assignee (
    id bigint NOT NULL,
    version bigint NOT NULL,
    start_date timestamp without time zone NOT NULL,
    representative_id bigint NOT NULL,
    organization_id bigint NOT NULL,
    a_package_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    end_date timestamp without time zone
);


CREATE TABLE package_immigration_benefit (
    package_benefits_id bigint NOT NULL,
    immigration_benefit_id bigint
);

CREATE SEQUENCE package_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE package_transfer_request_package (
    package_transfer_request_packages_id bigint,
    package_id bigint,
    packages_idx integer
);

CREATE TABLE payment (
    id bigint NOT NULL,
    version bigint NOT NULL,
    representative_id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    amount numeric(19,2) NOT NULL
);

CREATE TABLE petitioner (
    id bigint NOT NULL,
    petitioner_status character varying(255),
    elis_account_number character varying(255)
);

CREATE TABLE poverty_guideline (
    id bigint NOT NULL,
    version bigint NOT NULL,
    add_on_price double precision NOT NULL,
    base_price double precision NOT NULL,
    year integer NOT NULL,
    state character varying(255) NOT NULL
);

CREATE TABLE process_request (
    id bigint NOT NULL,
    version bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    requested_by_id bigint NOT NULL,
    state character varying(255) NOT NULL,
    class character varying(255) NOT NULL,
    organization_employee_id bigint,
    representative_id bigint,
    organization_id bigint,
    old_assignee_id bigint,
    representative_organization_id bigint,
    old_organization_id bigint
);

CREATE TABLE profile (
    id bigint NOT NULL,
    version bigint NOT NULL,
    active_package_id bigint,
    date_created timestamp without time zone NOT NULL,
    first_name character varying(255) NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    middle_name character varying(255),
    language character varying(255),
    profile_photo_id bigint,
    easy_visa_id character varying(255) NOT NULL,
    user_id bigint,
    last_name character varying(255) NOT NULL,
    email character varying(255)
);

CREATE TABLE prospect_counts (
    id bigint NOT NULL,
    version bigint NOT NULL,
    representative_id bigint NOT NULL,
    contact_info_views bigint NOT NULL,
    search_date date NOT NULL,
    profile_views bigint NOT NULL
);

CREATE TABLE registration_code (
    id bigint NOT NULL,
    date_created timestamp without time zone NOT NULL,
    easy_visa_id character varying(255),
    username character varying(255),
    token character varying(255) NOT NULL
);

CREATE TABLE review (
    id bigint NOT NULL,
    version bigint NOT NULL,
    title character varying(255) NOT NULL,
    reviewer_id bigint NOT NULL,
    representative_id bigint NOT NULL,
    rating integer NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    read boolean DEFAULT false NOT NULL,
    a_package_id bigint NOT NULL,
    reply text,
    review text NOT NULL
);

CREATE TABLE role (
    id bigint NOT NULL,
    version bigint NOT NULL,
    authority character varying(255) NOT NULL
);

CREATE TABLE section_completion_status (
    id bigint NOT NULL,
    version bigint NOT NULL,
    section_id character varying(255) NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    package_id bigint NOT NULL,
    applicant_id bigint NOT NULL,
    completion_state character varying(255) NOT NULL
);

CREATE TABLE user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);

CREATE TABLE warning (
    id bigint NOT NULL,
    applicant_id bigint NOT NULL,
    a_package_id bigint,
    answer_id bigint,
    representative_id bigint,
    question_id character varying(255)
);

CREATE TABLE working_hour (
    id bigint NOT NULL,
    version bigint NOT NULL,
    start_hour integer,
    date_created timestamp without time zone NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    start_minutes integer,
    end_hour integer,
    end_minutes integer,
    day_of_week character varying(255) NOT NULL
);

ALTER TABLE ONLY additional_fee
    ADD CONSTRAINT additional_fee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);

ALTER TABLE ONLY admin_config
    ADD CONSTRAINT admin_config_pkey PRIMARY KEY (id);

ALTER TABLE ONLY admin_settings
    ADD CONSTRAINT admin_settings_pkey PRIMARY KEY (id);

ALTER TABLE ONLY alert
    ADD CONSTRAINT alert_pkey PRIMARY KEY (id);

ALTER TABLE ONLY answer
    ADD CONSTRAINT answer_pkey PRIMARY KEY (id);

ALTER TABLE ONLY applicant
    ADD CONSTRAINT applicant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY article
    ADD CONSTRAINT article_pkey PRIMARY KEY (id);

ALTER TABLE ONLY attorney_warning_template
    ADD CONSTRAINT attorney_warning_template_pkey PRIMARY KEY (id);

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);

ALTER TABLE ONLY authentication_token
    ADD CONSTRAINT authentication_token_pkey PRIMARY KEY (id);

ALTER TABLE ONLY charge
    ADD CONSTRAINT charge_pkey PRIMARY KEY (id);

ALTER TABLE ONLY document_action
    ADD CONSTRAINT document_action_pkey PRIMARY KEY (id);

ALTER TABLE ONLY document
    ADD CONSTRAINT document_pkey PRIMARY KEY (id);

ALTER TABLE ONLY easy_visa_file
    ADD CONSTRAINT easy_visa_file_pkey PRIMARY KEY (id);

ALTER TABLE ONLY education
    ADD CONSTRAINT education_pkey PRIMARY KEY (id);

ALTER TABLE ONLY email
    ADD CONSTRAINT email_pkey PRIMARY KEY (id);

ALTER TABLE ONLY email_template
    ADD CONSTRAINT email_template_pkey PRIMARY KEY (id);

ALTER TABLE ONLY employee
    ADD CONSTRAINT employee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY ev_user
    ADD CONSTRAINT ev_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY fee_schedule
    ADD CONSTRAINT fee_schedule_pkey PRIMARY KEY (id);

ALTER TABLE ONLY immigration_benefit_asset_value
    ADD CONSTRAINT immigration_benefit_asset_value_pkey PRIMARY KEY (id);

ALTER TABLE ONLY immigration_benefit
    ADD CONSTRAINT immigration_benefit_pkey PRIMARY KEY (id);

ALTER TABLE ONLY legal_representative
    ADD CONSTRAINT legal_representative_pkey PRIMARY KEY (id);

ALTER TABLE ONLY licensed_region
    ADD CONSTRAINT licensed_region_pkey PRIMARY KEY (id);

ALTER TABLE ONLY organization_employee
    ADD CONSTRAINT organization_employee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);

ALTER TABLE ONLY package_assignee
    ADD CONSTRAINT package_assignee_pkey PRIMARY KEY (id);

ALTER TABLE ONLY package
    ADD CONSTRAINT package_pkey PRIMARY KEY (id);

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);

ALTER TABLE ONLY petitioner
    ADD CONSTRAINT petitioner_pkey PRIMARY KEY (id);

ALTER TABLE ONLY poverty_guideline
    ADD CONSTRAINT poverty_guideline_pkey PRIMARY KEY (id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT process_request_pkey PRIMARY KEY (id);

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id);

ALTER TABLE ONLY prospect_counts
    ADD CONSTRAINT prospect_counts_pkey PRIMARY KEY (id);

ALTER TABLE ONLY registration_code
    ADD CONSTRAINT registration_code_pkey PRIMARY KEY (id);

ALTER TABLE ONLY review
    ADD CONSTRAINT review_pkey PRIMARY KEY (id);

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);

ALTER TABLE ONLY section_completion_status
    ADD CONSTRAINT section_completion_status_pkey PRIMARY KEY (id);

ALTER TABLE ONLY profile
    ADD CONSTRAINT uk_c1dkiawnlj6uoe6fnlwd6j83j UNIQUE (user_id);

ALTER TABLE ONLY legal_representative
    ADD CONSTRAINT uk_dqfkfqkd9w5yfb70ckeor8mqy UNIQUE (state_bar_number);

ALTER TABLE ONLY role
    ADD CONSTRAINT uk_irsamgnera6angm0prq1kemt2 UNIQUE (authority);

ALTER TABLE ONLY ev_user
    ADD CONSTRAINT uk_l4atj0s8tnfm9d6v5ghg7j9r9 UNIQUE (username);

ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);

ALTER TABLE ONLY warning
    ADD CONSTRAINT warning_pkey PRIMARY KEY (id);

ALTER TABLE ONLY working_hour
    ADD CONSTRAINT working_hour_pkey PRIMARY KEY (id);

ALTER TABLE ONLY package_assignee
    ADD CONSTRAINT fk23gpwtci6jq02l4j60ev1nuxb FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY employee_organization
    ADD CONSTRAINT fk2519yacfjuosocfr1tcs1aq1g FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY organization_working_hour
    ADD CONSTRAINT fk2d2jjrm5o9ihwqx7f4bbqghex FOREIGN KEY (working_hour_id) REFERENCES working_hour(id);

ALTER TABLE ONLY legal_representative_working_hour
    ADD CONSTRAINT fk2ljs37cqwlfdkurpsup4ixgsw FOREIGN KEY (working_hour_id) REFERENCES working_hour(id);

ALTER TABLE ONLY package_applicant
    ADD CONSTRAINT fk2rayyqqq2f3v21bywprg12gyt FOREIGN KEY (package_applicants_id) REFERENCES package(id);

ALTER TABLE ONLY email
    ADD CONSTRAINT fk36f8w8eyj28tmsap09qogxitk FOREIGN KEY (attorney_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY alert
    ADD CONSTRAINT fk38uf55rrv2s86das0np30jesm FOREIGN KEY (recipient_id) REFERENCES ev_user(id);

ALTER TABLE ONLY review
    ADD CONSTRAINT fk3gurqu6rqgq4n7y420nssrt2q FOREIGN KEY (reviewer_id) REFERENCES applicant(id);

ALTER TABLE ONLY fee_schedule
    ADD CONSTRAINT fk3lfbp5s5ptd71wcmmbxjctprq FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY warning
    ADD CONSTRAINT fk3nk6kbdmvhbg28k4av28h6p9p FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY applicant
    ADD CONSTRAINT fk3r26tvma0aw79witq59pwfhmn FOREIGN KEY (home_id) REFERENCES address(id);

ALTER TABLE ONLY legal_representative_licensed_region
    ADD CONSTRAINT fk43td7ltc0gig8ey0masrcfwko FOREIGN KEY (legal_representative_licensed_regions_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY package_applicant
    ADD CONSTRAINT fk44gdpky68i5qkdra8jbxejtdb FOREIGN KEY (applicant_id) REFERENCES applicant(id);

ALTER TABLE ONLY prospect_counts
    ADD CONSTRAINT fk4lgvh99ohmypxquln0iur2u6v FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY email
    ADD CONSTRAINT fk4qpuhp0002icb3gn0pwog7ckj FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY package
    ADD CONSTRAINT fk52kb9e3scmsjj7kemknjw89ru FOREIGN KEY (petitioner_id) REFERENCES petitioner(id);

ALTER TABLE ONLY profile
    ADD CONSTRAINT fk5afc52wug7mow4kpjyftr0vu7 FOREIGN KEY (user_id) REFERENCES ev_user(id);

ALTER TABLE ONLY package_immigration_benefit
    ADD CONSTRAINT fk5c08f2nv6pgbbh8dcy5f9ol05 FOREIGN KEY (immigration_benefit_id) REFERENCES immigration_benefit(id);

ALTER TABLE ONLY organization_roster_names
    ADD CONSTRAINT fk69hn756robvq7f074nl63l5xd FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY document
    ADD CONSTRAINT fk6h3cha96rjpuubrqccg2di1oh FOREIGN KEY (file_id) REFERENCES easy_visa_file(id);

ALTER TABLE ONLY legal_representative_education
    ADD CONSTRAINT fk6l80vdor1ypgu9bvot9iuxqdk FOREIGN KEY (legal_representative_degrees_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY warning
    ADD CONSTRAINT fk6yng8db50tbd58fmweram5bo4 FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY email_template
    ADD CONSTRAINT fk7fck8puvhca5n4h14olnbbjoa FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY additional_fee
    ADD CONSTRAINT fk7n2s8qexr38x2vpkmon5hjigc FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY applicant
    ADD CONSTRAINT fk83br3j5eycxepcigmljng872d FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk97eigsvq2tsrd2bge4ox651wh FOREIGN KEY (address_id) REFERENCES address(id);

ALTER TABLE ONLY package_assignee
    ADD CONSTRAINT fk98a81ugf3nvn8kag0w2s1f5cp FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY payment
    ADD CONSTRAINT fk98ivlxw15a6kv5gukwvd9yihj FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY admin_settings
    ADD CONSTRAINT fk9a18d47t9q2m7gk3y50udwt4n FOREIGN KEY (admin_config_id) REFERENCES admin_config(id);

ALTER TABLE ONLY easy_visa_file
    ADD CONSTRAINT fk9hxyypcbq0amhvs9ucrf02siq FOREIGN KEY (uploader_id) REFERENCES profile(id);

ALTER TABLE ONLY profile
    ADD CONSTRAINT fk9svxbuexyqoe2i0mfqp8e1rqa FOREIGN KEY (profile_photo_id) REFERENCES easy_visa_file(id);

ALTER TABLE ONLY user_role
    ADD CONSTRAINT fka68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES role(id);

ALTER TABLE ONLY package
    ADD CONSTRAINT fkal7i0yw747thg5wnfjrby3bbo FOREIGN KEY (retainer_agreement_id) REFERENCES easy_visa_file(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fkaum0x7s6q7vsddnmcnvskwb4b FOREIGN KEY (old_assignee_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY review
    ADD CONSTRAINT fkaw553yyxjjtekgq3k03eqrhm0 FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY immigration_benefit
    ADD CONSTRAINT fkc1nh6g4oxbp03q6vj7tvda2mk FOREIGN KEY (applicant_id) REFERENCES applicant(id);

ALTER TABLE ONLY article
    ADD CONSTRAINT fkcgg5kkexxy1usb9vrbkeh7ybd FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY fee_schedule
    ADD CONSTRAINT fkcjxtxya8xq2r55qwhl70uyjo7 FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY alert
    ADD CONSTRAINT fkcscc8ea90q5v1r199po0jrltr FOREIGN KEY (process_request_id) REFERENCES process_request(id);

ALTER TABLE ONLY package_immigration_benefit
    ADD CONSTRAINT fkdcby1j16cpvpeupqtdv0dqn8y FOREIGN KEY (package_benefits_id) REFERENCES package(id);

ALTER TABLE ONLY organization_spoken_languages
    ADD CONSTRAINT fke2x5y6dwa6f8y6aaotp24gr42 FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY review
    ADD CONSTRAINT fkeag8lyoyhh1d9gywtmx07q6gl FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY organization_working_hour
    ADD CONSTRAINT fkf17f3kixmnuvkndw6d32xv2af FOREIGN KEY (organization_working_hours_id) REFERENCES organization(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fkf78ojwo2ovylk9syow5erj22o FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY legal_representative
    ADD CONSTRAINT fkfa49fwvxqs48cmv0emxkdc13f FOREIGN KEY (referrer_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY legal_representative_licensed_region
    ADD CONSTRAINT fkfipskkhcgh5u2bjtgooh4rh2d FOREIGN KEY (licensed_region_id) REFERENCES licensed_region(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fkflrb6541dof1wr1bh6307lier FOREIGN KEY (old_organization_id) REFERENCES organization(id);

ALTER TABLE ONLY profile
    ADD CONSTRAINT fkft7f508sshtshf5sm4jmn876d FOREIGN KEY (active_package_id) REFERENCES package(id);

ALTER TABLE ONLY warning
    ADD CONSTRAINT fkhwkuxupwcd44jbop93lol2kiq FOREIGN KEY (answer_id) REFERENCES answer(id);

ALTER TABLE ONLY organization_practice_areas
    ADD CONSTRAINT fkie00060u8rh0uv7dsi58qqo9o FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY employee
    ADD CONSTRAINT fkiudpmfdhc4gr9fsuo76g35ya9 FOREIGN KEY (active_organization_id) REFERENCES organization(id);

ALTER TABLE ONLY charge
    ADD CONSTRAINT fkj0mi8sdd9o50ml6j9ur2l9tjd FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY warning
    ADD CONSTRAINT fkjddtfpuo38nh9du0kgbn1d4ij FOREIGN KEY (applicant_id) REFERENCES applicant(id);

ALTER TABLE ONLY legal_representative_practice_areas
    ADD CONSTRAINT fkjejbqknv45m42x0x9vy4kyb9l FOREIGN KEY (legal_representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY organization_employee
    ADD CONSTRAINT fkjfnib0o9rttmvj2y980vuvk0t FOREIGN KEY (employee_id) REFERENCES employee(id);

ALTER TABLE ONLY legal_representative
    ADD CONSTRAINT fkjyjpailbq7n600fjevmyyqo73 FOREIGN KEY (office_address_id) REFERENCES address(id);

ALTER TABLE ONLY package_transfer_request_package
    ADD CONSTRAINT fkklysfurk6uwpjmlmqpk9h1ixf FOREIGN KEY (package_id) REFERENCES package(id);

ALTER TABLE ONLY employee
    ADD CONSTRAINT fkksy9iy059ueim17q74yx728ad FOREIGN KEY (profile_id) REFERENCES profile(id);

ALTER TABLE ONLY organization
    ADD CONSTRAINT fkktv61kbly9ng84t3bedcjb07b FOREIGN KEY (logo_file_id) REFERENCES easy_visa_file(id);

ALTER TABLE ONLY organization_employee
    ADD CONSTRAINT fkktytn9670289ikfombqt8ckmg FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY employee_organization
    ADD CONSTRAINT fkmaqkqhv9p47noc388guwxph77 FOREIGN KEY (employee_linked_organizations_id) REFERENCES employee(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fknnjrn4cl84yxxx1rs7e4rja0a FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fko2cn78stwghvle8te3dgt9thi FOREIGN KEY (representative_organization_id) REFERENCES organization(id);

ALTER TABLE ONLY user_role
    ADD CONSTRAINT fko4mkupn8lx2wcey5os3xtj926 FOREIGN KEY (user_id) REFERENCES ev_user(id);

ALTER TABLE ONLY document
    ADD CONSTRAINT fkoj9f66gn2nikemv68rw5dh14s FOREIGN KEY (applicant_id) REFERENCES applicant(id);

ALTER TABLE ONLY answer
    ADD CONSTRAINT fkomf6wo9ufnubkkwjtuagqu6qo FOREIGN KEY (updated_by_id) REFERENCES ev_user(id);

ALTER TABLE ONLY email_template
    ADD CONSTRAINT fkq8jacbs16yvvunw745g5ps96e FOREIGN KEY (attorney_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY article
    ADD CONSTRAINT fkqel8se8q36tncv3v3lwb9it3p FOREIGN KEY (author_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY package
    ADD CONSTRAINT fkqk43hadlav5ir9l4xltujnpn6 FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE ONLY legal_representative_working_hour
    ADD CONSTRAINT fkr3g1vr1ha2vee0118q5696cte FOREIGN KEY (legal_representative_working_hours_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY package
    ADD CONSTRAINT fkr745ayo6wgf52w0pod0vaj16f FOREIGN KEY (attorney_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY legal_representative_education
    ADD CONSTRAINT fkrjol3uk1lx6ecwwh28kxf2lag FOREIGN KEY (education_id) REFERENCES education(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fksbg5jgt87yovrpxoh8wt09wng FOREIGN KEY (organization_employee_id) REFERENCES organization_employee(id);

ALTER TABLE ONLY process_request
    ADD CONSTRAINT fkstaoi3d5w8sg1oypkjl3u55qt FOREIGN KEY (requested_by_id) REFERENCES ev_user(id);

ALTER TABLE ONLY document
    ADD CONSTRAINT fktcvyd4sg38fd0kcdokjtau74n FOREIGN KEY (a_package_id) REFERENCES package(id);

ALTER TABLE ONLY package_assignee
    ADD CONSTRAINT fktfm9dca4tau1qgk6mtvhrjw61 FOREIGN KEY (representative_id) REFERENCES legal_representative(id);

ALTER TABLE ONLY answer
    ADD CONSTRAINT fku1sftkjj1myk54pupqm964ds FOREIGN KEY (created_by_id) REFERENCES ev_user(id);
