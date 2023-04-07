--neo4j last updates
INSERT INTO app_config (id, "version", "type", value, date_created, last_updated)
SELECT 1, 0, 'NEO4J_LAST_UPDATE', '2020-11-16T00:00:00', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM app_config WHERE "type" = 'NEO4J_LAST_UPDATE');

--roles creation
INSERT INTO role (id, version, authority)
SELECT 1, 0, 'ROLE_EV'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE authority = 'ROLE_EV');
INSERT INTO role (id, version, authority)
SELECT 2, 0, 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE authority = 'ROLE_USER');
INSERT INTO role (id, version, authority)
SELECT 3, 0, 'ROLE_ATTORNEY'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE authority = 'ROLE_ATTORNEY');
INSERT INTO role (id, version, authority)
SELECT 4, 0, 'ROLE_EMPLOYEE'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE authority = 'ROLE_EMPLOYEE');
INSERT INTO role (id, version, authority)
SELECT 5, 0, 'ROLE_OWNER'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE authority = 'ROLE_OWNER');

--admin settings
INSERT INTO admin_config (id, "version", i601, i485_14, i601a, signup_discount, i360, referral_bonus, n400, membership_reactivation_fee, support_email, article_bonus, i130, i751, cloud_storage_fee, i600_600a, i129f, signup_fee, contact_phone, i485, maintenance_fee, i765, n600_n600k, biometric_service_fee)
SELECT 1, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '', 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0, '', 0.00, 0.00, 0.00, 0.00, 0.00
WHERE NOT EXISTS (SELECT 1 FROM admin_config);
INSERT INTO admin_settings (id, "version", ev_employee_sequence_no, representative_sequence_no, client_sequence_no, owner_sequence_no, client_employee_sequence_no, organization_sequence_no, admin_config_id, package_sequence_no)
SELECT 1, 0, 10000, 10000, 10000, 10000, 10000, 10000, id, 10000 FROM admin_config
WHERE NOT EXISTS (SELECT 1 FROM admin_settings);
INSERT INTO profile(id, "version", date_created, first_name, last_updated, easy_visa_id, last_name)
SELECT 1, 0, now(), 'Fee Schedule', now(), 'A0000000001', 'Attorney'
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO employee (id, "version", profile_id)
SELECT 1, 0, 1
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO legal_representative(id, registration_status, credit_balance, representative_type, top_contributor_score, recent_contributor_score, base_contributor_score, random_score)
SELECT 1, 'NEW', 0.00, 'ATTORNEY', 0, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'IR1', 0
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'IR2', 1
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'IR5', 2
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F1_A', 3
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F2_A', 4
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F3_A', 5
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F4_A', 6
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F1_B', 7
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F2_B', 8
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F3_B', 9
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'F4_B', 10
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'K1K3', 11
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'K2K4', 12
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'NATURALIZATION', 13
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'DISABILITY', 14
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'LPRSPOUSE', 15
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'LPRCHILD', 16
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'SIX01', 17
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'SIX01A', 18
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'EAD', 19
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
INSERT INTO fee (id, "version", amount, representative_id, date_created, last_updated, benefit_category, fee_schedule_idx)
SELECT nextval('hibernate_sequence'), 0, 0, 1, now(), now(), 'REMOVECOND', 20
WHERE NOT EXISTS (SELECT 1 FROM admin_config WHERE attorney_id is not null);
UPDATE admin_config SET attorney_id = e.id FROM employee e JOIN profile p ON e.profile_id = p.id WHERE p.first_name = 'Fee Schedule';

--questionnaire versions
INSERT INTO questionnaire_version (id, "version", quest_version, start_date, date_created, last_updated)
SELECT 1, 0, 'quest_version_1', '2020-04-01 00:00:00', '2020-04-01 00:00:00', '2020-04-01 00:00:00'
WHERE NOT EXISTS (SELECT 1 FROM questionnaire_version WHERE quest_version = 'quest_version_1');
INSERT INTO questionnaire_version(id, "version", quest_version, start_date, date_created, last_updated)
SELECT 2, 0, 'quest_version_2', now(), now(), now()
WHERE NOT EXISTS (SELECT 1 FROM questionnaire_version WHERE quest_version = 'quest_version_2');

--poverty guideline
DELETE FROM immigration_benefit_asset_value;
INSERT INTO immigration_benefit_asset_value (id,version,easy_visa_id, benefit_category, asset_value) VALUES
(1,1,'BC_F1_B', 'F1 Unmarried (age 21 and over) Sons and Daughters of Citizens and Their Minor Children', 5),
(2,1,'BC_F2_B', 'F2 Spouse, Minor Children, and Unmarried Sons and Daughters (age 21 and over) of LPRs', 5),
(3,1,'BC_F3_B', 'F3 Married Sons and Daughters of Citizens, and Their Spouses and Their Minor Children', 5),
(4,1,'BC_F4_B', 'F4 Brothers & Sisters of Citizens (age 21 and over), & Their Spouses & Minor Children', 5),
(5,1,'BC_IR1', 'IR-1 Spouse of Citizen', 3),
(6,1,'BC_IR2', 'IR-2 Unmarried Children (under 21) of a Nat. Citizen', 3),
(7,1,'BC_IR3', 'IR-3 Orphan Adopted Abroad by a Citizen', 1),
(8,1,'BC_IR4', 'IR-4 Orphan to be Adopted in U.S. by a Citizen', 1),
(9,1,'BC_IR5', 'IR-5 Parent of Citizen (age 21 and over)', 5),
(10,1,'BC_K1K3', 'K-1/K-3	K1/K3 Fiancé(e)', 5),
(11,1,'BC_K2K4', 'K-2/K-4	K2/K4 Fiancé(e) Children', 5);

DELETE FROM poverty_guideline;
INSERT INTO poverty_guideline (id, version,year, state, base_price, add_on_price) VALUES
(1,1,2017, 'HAWAII', 13860, 4810),
(2,1,2017, 'ALASKA', 15060, 5230),
(3,1,2017, 'ALABAMA', 12060, 4180),
(4,1,2017, 'ARIZONA', 12060, 4180),
(5,1,2017, 'ARKANSAS', 12060, 4180),
(6,1,2017, 'CALIFORNIA', 12060, 4180),
(7,1,2017, 'COLORADO', 12060, 4180),
(8,1,2017, 'CONNECTICUT', 12060, 4180),
(9,1,2017, 'DELAWARE', 12060, 4180),
(10,1,2017, 'FLORIDA', 12060, 4180),
(11,1,2017, 'GEORGIA', 12060, 4180),
(12,1,2017, 'IDAHO', 12060, 4180),
(13,1,2017, 'ILLINOIS', 12060, 4180),
(14,1,2017, 'INDIANA', 12060, 4180),
(15,1,2017, 'IOWA', 12060, 4180),
(16,1,2017, 'KANSAS', 12060, 4180),
(17,1,2017, 'KENTUCKY', 12060, 4180),
(18,1,2017, 'LOUISIANA', 12060, 4180),
(19,1,2017, 'MAINE', 12060, 4180),
(20,1,2017, 'MARYLAND', 12060, 4180),
(21,1,2017, 'MASSACHUSETTS', 12060, 4180),
(22,1,2017, 'MICHIGAN', 12060, 4180),
(23,1,2017, 'MINNESOTA', 12060, 4180),
(24,1,2017, 'MISSISSIPPI', 12060, 4180),
(25,1,2017, 'MISSOURI', 12060, 4180),
(26,1,2017, 'MONTANA', 12060, 4180),
(27,1,2017, 'NEBRASKA', 12060, 4180),
(28,1,2017, 'NEVADA', 12060, 4180),
(29,1,2017, 'NEW_HAMPSHIRE', 12060, 4180),
(30,1,2017, 'NEW_JERSEY', 12060, 4180),
(31,1,2017, 'NEW_MEXICO', 12060, 4180),
(32,1,2017, 'NEW_YORK', 12060, 4180),
(33,1,2017, 'NORTH_CAROLINA', 12060, 4180),
(34,1,2017, 'NORTH_DAKOTA', 12060, 4180),
(35,1,2017, 'OHIO', 12060, 4180),
(36,1,2017, 'OKLAHOMA', 12060, 4180),
(37,1,2017, 'OREGON', 12060, 4180),
(38,1,2017, 'PENNSYLVANIA', 12060, 4180),
(39,1,2017, 'RHODE_ISLAND', 12060, 4180),
(40,1,2017, 'SOUTH_CAROLINA', 12060, 4180),
(41,1,2017, 'SOUTH_DAKOTA', 12060, 4180),
(42,1,2017, 'TENNESSEE', 12060, 4180),
(43,1,2017, 'TEXAS', 12060, 4180),
(44,1,2017, 'UTAH', 12060, 4180),
(45,1,2017, 'VERMONT', 12060, 4180),
(46,1,2017, 'VIRGINIA', 12060, 4180),
(47,1,2017, 'WASHINGTON', 12060, 4180),
(48,1,2017, 'WEST_VIRGINIA', 12060, 4180),
(49,1,2017, 'WISCONSIN', 12060, 4180),
(50,1,2017, 'WYOMING', 12060, 4180);

---attorney warnings
DROP TABLE IF EXISTS attorney_warning_template;

---petitioner beneficiary mapping
DELETE FROM petitioner_beneficiary_mapping  WHERE quest_version='quest_version_1';
INSERT INTO petitioner_beneficiary_mapping (id,version,quest_version,petitioner_section_nodeid,petitioner_subsection_nodeid,petitioner_question_nodeid,petitioner_repeatingquestiongroup,beneficiary_section_nodeid,beneficiary_subsection_nodeid,beneficiary_question_nodeid,beneficiary_repeatingquestiongroup) VALUES
(1,1,'quest_version_1','Sec_2','SubSec_5','Q_32',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1901',FALSE),
(2,1,'quest_version_1','Sec_2','SubSec_5','Q_33',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1902',FALSE),
(3,1,'quest_version_1','Sec_2','SubSec_5','Q_34',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1903',FALSE),
(4,1,'quest_version_1','Sec_2','SubSec_6','Q_35',FALSE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1906',FALSE),
(5,1,'quest_version_1','Sec_2','SubSec_6','Q_37',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1908',TRUE),
(6,1,'quest_version_1','Sec_2','SubSec_6','Q_39',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1910',TRUE),
(7,1,'quest_version_1','Sec_2','SubSec_6','Q_41',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1912',TRUE),
(8,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_42',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2002',FALSE),
(9,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_43',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2003',FALSE),
(10,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_44',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2004',FALSE),
(11,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_45',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2005',FALSE),
(12,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_46',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2006',FALSE),
(13,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_47',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2007',FALSE),
(14,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_48',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2008',FALSE),
(15,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_49',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2009',FALSE),
(16,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_50',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2010',FALSE),
(17,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_51',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2011',FALSE),
(18,1,'quest_version_1','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_52',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2012',FALSE),
(19,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_68',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2017',FALSE),
(20,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_69',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2015',FALSE),
(21,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_70',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2016',FALSE),
(22,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_71',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2018',FALSE),
(23,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_72',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2019',FALSE),
(24,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_73',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2020',FALSE),
(25,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_74',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2021',FALSE),
(26,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_75',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2022',FALSE),
(27,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_76',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2023',FALSE),
(28,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_77',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2024',FALSE),
(29,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_78',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2025',FALSE),
(30,1,'quest_version_1','Sec_addressHistory','SubSec_currentMailingAddress','Q_79',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2026',FALSE),
(31,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_54',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2040',TRUE),
(32,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_55',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2041',TRUE),
(33,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_56',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2042',TRUE),
(34,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_57',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2043',TRUE),
(35,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_58',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2044',TRUE),
(36,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_59',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2045',TRUE),
(37,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_60',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2046',TRUE),
(38,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_61',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2047',TRUE),
(39,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_62',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2048',TRUE),
(40,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_63',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2049',TRUE),
(41,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_64',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2050',TRUE),
(42,1,'quest_version_1','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_65',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2052',TRUE),
(43,1,'quest_version_1','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_80',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2151',FALSE),
(44,1,'quest_version_1','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_81',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2152',FALSE),
(45,1,'quest_version_1','Sec_contactInformation','SubSec_officePhoneNumber','Q_84',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2156',FALSE),
(46,1,'quest_version_1','Sec_contactInformation','SubSec_officePhoneNumber','Q_85',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2157',FALSE),
(47,1,'quest_version_1','Sec_contactInformation','SubSec_email','Q_86',FALSE,'Sec_contactInformationForBeneficiary','SubSec_emailForBeneficiary','Q_2158',FALSE),
(48,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_87',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2201',FALSE),
(49,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_88',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2202',FALSE),
(50,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_89',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2203',FALSE),
(51,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_90',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2205',FALSE),
(52,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_92',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2204',FALSE),
(53,1,'quest_version_1','Sec_birthInformation','SubSec_birthInformation','Q_94',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2206',FALSE),
(54,1,'quest_version_1','Sec_biographicInformation','SubSec_ethnicity','Q_95',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_ethnicityForBeneficiary','Q_2301',FALSE),
(55,1,'quest_version_1','Sec_biographicInformation','SubSec_race','Q_96',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2303',FALSE),
(56,1,'quest_version_1','Sec_biographicInformation','SubSec_race','Q_97',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2304',FALSE),
(57,1,'quest_version_1','Sec_biographicInformation','SubSec_race','Q_98',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2305',FALSE),
(58,1,'quest_version_1','Sec_biographicInformation','SubSec_race','Q_99',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2306',FALSE),
(59,1,'quest_version_1','Sec_biographicInformation','SubSec_race','Q_100',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2307',FALSE),
(60,1,'quest_version_1','Sec_biographicInformation','SubSec_height','Q_101',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2309',FALSE),
(61,1,'quest_version_1','Sec_biographicInformation','SubSec_height','Q_102',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2312',FALSE),
(62,1,'quest_version_1','Sec_biographicInformation','SubSec_height','Q_103',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2310',FALSE),
(63,1,'quest_version_1','Sec_biographicInformation','SubSec_height','Q_104',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2311',FALSE),
(64,1,'quest_version_1','Sec_biographicInformation','SubSec_weight','Q_105',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2314',FALSE),
(65,1,'quest_version_1','Sec_biographicInformation','SubSec_weight','Q_106',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2315',FALSE),
(66,1,'quest_version_1','Sec_biographicInformation','SubSec_weight','Q_6009',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_6010',FALSE),
(67,1,'quest_version_1','Sec_biographicInformation','SubSec_eyeColor','Q_107',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_eyeColorForBeneficiary','Q_2317',FALSE),
(68,1,'quest_version_1','Sec_biographicInformation','SubSec_hairColor','Q_108',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_hairColorForBeneficiary','Q_2319',FALSE),
(69,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1008',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2608',TRUE),
(70,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1009',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2609',TRUE),
(71,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1010',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2610',TRUE),
(72,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1011',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2611',TRUE),
(73,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1012',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2612',TRUE),
(74,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1013',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2613',TRUE),
(75,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1014',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2614',TRUE),
(76,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1015',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2615',TRUE),
(77,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1016',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2616',TRUE),
(78,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1017',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2617',TRUE),
(79,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1018',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2618',TRUE),
(80,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1019',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2619',TRUE),
(81,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1020',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2620',TRUE),
(82,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1021',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2621',TRUE),
(83,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1022',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2622',TRUE),
(84,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1023',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2623',TRUE),
(85,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1024',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2624',TRUE),
(86,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1025',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2625',TRUE),
(87,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1026',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2626',TRUE),
(88,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1027',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2627',TRUE),
(89,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1028',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2628',TRUE),
(90,1,'quest_version_1','Sec_employmentHistory','SubSec_employmentStatus','Q_1029',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2629',TRUE),
(91,1,'quest_version_1','Sec_familyInformation','SubSec_maritalStatus','Q_1201',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2778',FALSE),
(92,1,'quest_version_1','Sec_familyInformation','SubSec_maritalStatus','Q_1202',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2779',FALSE),
(93,1,'quest_version_1','Sec_familyInformation','SubSec_maritalStatus','Q_1203',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2780',FALSE),
(94,1,'quest_version_1','Sec_familyInformation','SubSec_maritalStatus','Q_1204',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2781',FALSE),
(95,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1206',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2789',FALSE),
(96,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1207',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2790',FALSE),
(97,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1208',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2791',FALSE),
(98,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1209',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2801',FALSE),
(99,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1210',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2804',FALSE),
(100,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1215',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2805',FALSE),
(101,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1216',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2806',FALSE),
(102,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1217',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2807',FALSE),
(103,1,'quest_version_1','Sec_familyInformation','SubSec_currentSpouse','Q_1220',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2783',FALSE),
(104,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1222',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2840',TRUE),
(105,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1223',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2841',TRUE),
(106,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1224',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2842',TRUE),
(107,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1225',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2844',TRUE),
(108,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1226',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2849',TRUE),
(109,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1227',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2850',TRUE),
(110,1,'quest_version_1','Sec_familyInformation','SubSec_priorSpouses','Q_1228',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2851',TRUE),
(111,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1230',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2883',FALSE),
(112,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1231',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2884',FALSE),
(113,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1232',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2885',FALSE),
(114,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1233',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2889',FALSE),
(115,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1234',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2890',FALSE),
(116,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1235',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2891',FALSE),
(117,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1236',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2892',FALSE),
(118,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1237',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2893',FALSE),
(119,1,'quest_version_1','Sec_familyInformation','SubSec_parent1','Q_1238',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2894',FALSE),
(120,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1240',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2901',FALSE),
(121,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1241',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2902',FALSE),
(122,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1242',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2903',FALSE),
(123,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1243',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2907',FALSE),
(124,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1244',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2908',FALSE),
(125,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1245',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2909',FALSE),
(126,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1246',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2910',FALSE),
(127,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1247',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2911',FALSE),
(128,1,'quest_version_1','Sec_familyInformation','SubSec_parent2','Q_1248',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2912',FALSE),
(129,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1250',FALSE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2741',FALSE),
(130,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1251',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2743',TRUE),
(131,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1252',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2746',TRUE),
(132,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1253',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2749',TRUE),
(133,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1254',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2752',TRUE),
(134,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1255',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2753',TRUE),
(135,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1256',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2754',TRUE),
(136,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1257',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2764',TRUE),
(137,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1258',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2765',TRUE),
(138,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1259',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2766',TRUE),
(139,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1260',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2767',TRUE),
(140,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1261',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2768',TRUE),
(141,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1262',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2769',TRUE),
(142,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1263',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2770',TRUE),
(143,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1264',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2771',TRUE),
(144,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1265',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2772',TRUE),
(145,1,'quest_version_1','Sec_familyInformation','SubSec_childrenInformation','Q_1266',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2773',TRUE),
(146,1,'quest_version_1','Sec_incomeHistory','SubSec_incomeHistory','Q_134',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2501',FALSE),
(147,1,'quest_version_1','Sec_incomeHistory','SubSec_incomeHistory','Q_138',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2502',FALSE),
(148,1,'quest_version_1','Sec_incomeHistory','SubSec_incomeHistory','Q_142',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2503',FALSE);

DELETE FROM petitioner_beneficiary_mapping  WHERE quest_version='quest_version_test';
INSERT INTO petitioner_beneficiary_mapping (id,version,quest_version,petitioner_section_nodeid,petitioner_subsection_nodeid,petitioner_question_nodeid,petitioner_repeatingquestiongroup,beneficiary_section_nodeid,beneficiary_subsection_nodeid,beneficiary_question_nodeid,beneficiary_repeatingquestiongroup) VALUES
(149,1,'quest_version_test','Sec_2','SubSec_5','Q_32',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1901',FALSE),
(150,1,'quest_version_test','Sec_2','SubSec_5','Q_33',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1902',FALSE),
(151,1,'quest_version_test','Sec_2','SubSec_5','Q_34',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1903',FALSE),
(152,1,'quest_version_test','Sec_2','SubSec_6','Q_35',FALSE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1906',FALSE),
(153,1,'quest_version_test','Sec_2','SubSec_6','Q_37',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1908',TRUE),
(154,1,'quest_version_test','Sec_2','SubSec_6','Q_39',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1910',TRUE),
(155,1,'quest_version_test','Sec_2','SubSec_6','Q_41',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1912',TRUE),
(156,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_42',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2002',FALSE),
(157,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_43',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2003',FALSE),
(158,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_44',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2004',FALSE),
(159,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_45',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2005',FALSE),
(160,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_46',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2006',FALSE),
(161,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_47',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2007',FALSE),
(162,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_48',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2008',FALSE),
(163,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_49',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2009',FALSE),
(164,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_50',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2010',FALSE),
(165,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_51',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2011',FALSE),
(166,1,'quest_version_test','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_52',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2012',FALSE),
(167,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_68',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2017',FALSE),
(168,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_69',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2015',FALSE),
(169,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_70',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2016',FALSE),
(170,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_71',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2018',FALSE),
(171,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_72',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2019',FALSE),
(172,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_73',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2020',FALSE),
(173,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_74',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2021',FALSE),
(174,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_75',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2022',FALSE),
(175,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_76',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2023',FALSE),
(176,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_77',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2024',FALSE),
(177,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_78',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2025',FALSE),
(178,1,'quest_version_test','Sec_addressHistory','SubSec_currentMailingAddress','Q_79',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2026',FALSE),
(179,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_54',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2040',TRUE),
(180,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_55',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2041',TRUE),
(181,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_56',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2042',TRUE),
(182,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_57',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2043',TRUE),
(183,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_58',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2044',TRUE),
(184,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_59',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2045',TRUE),
(185,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_60',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2046',TRUE),
(186,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_61',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2047',TRUE),
(187,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_62',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2048',TRUE),
(188,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_63',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2049',TRUE),
(189,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_64',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2050',TRUE),
(190,1,'quest_version_test','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_65',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2052',TRUE),
(191,1,'quest_version_test','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_80',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2151',FALSE),
(192,1,'quest_version_test','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_81',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2152',FALSE),
(193,1,'quest_version_test','Sec_contactInformation','SubSec_officePhoneNumber','Q_84',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2156',FALSE),
(194,1,'quest_version_test','Sec_contactInformation','SubSec_officePhoneNumber','Q_85',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2157',FALSE),
(195,1,'quest_version_test','Sec_contactInformation','SubSec_email','Q_86',FALSE,'Sec_contactInformationForBeneficiary','SubSec_emailForBeneficiary','Q_2158',FALSE),
(196,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_87',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2201',FALSE),
(197,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_88',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2202',FALSE),
(198,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_89',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2203',FALSE),
(199,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_90',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2205',FALSE),
(200,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_92',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2204',FALSE),
(201,1,'quest_version_test','Sec_birthInformation','SubSec_birthInformation','Q_94',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2206',FALSE),
(202,1,'quest_version_test','Sec_biographicInformation','SubSec_ethnicity','Q_95',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_ethnicityForBeneficiary','Q_2301',FALSE),
(203,1,'quest_version_test','Sec_biographicInformation','SubSec_race','Q_96',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2303',FALSE),
(204,1,'quest_version_test','Sec_biographicInformation','SubSec_race','Q_97',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2304',FALSE),
(205,1,'quest_version_test','Sec_biographicInformation','SubSec_race','Q_98',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2305',FALSE),
(206,1,'quest_version_test','Sec_biographicInformation','SubSec_race','Q_99',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2306',FALSE),
(207,1,'quest_version_test','Sec_biographicInformation','SubSec_race','Q_100',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2307',FALSE),
(208,1,'quest_version_test','Sec_biographicInformation','SubSec_height','Q_101',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2309',FALSE),
(209,1,'quest_version_test','Sec_biographicInformation','SubSec_height','Q_102',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2312',FALSE),
(210,1,'quest_version_test','Sec_biographicInformation','SubSec_height','Q_103',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2310',FALSE),
(211,1,'quest_version_test','Sec_biographicInformation','SubSec_height','Q_104',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2311',FALSE),
(212,1,'quest_version_test','Sec_biographicInformation','SubSec_weight','Q_105',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2314',FALSE),
(213,1,'quest_version_test','Sec_biographicInformation','SubSec_weight','Q_106',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2315',FALSE),
(214,1,'quest_version_test','Sec_biographicInformation','SubSec_weight','Q_6009',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_6010',FALSE),
(215,1,'quest_version_test','Sec_biographicInformation','SubSec_eyeColor','Q_107',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_eyeColorForBeneficiary','Q_2317',FALSE),
(216,1,'quest_version_test','Sec_biographicInformation','SubSec_hairColor','Q_108',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_hairColorForBeneficiary','Q_2319',FALSE),
(217,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1008',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2608',TRUE),
(218,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1009',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2609',TRUE),
(219,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1010',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2610',TRUE),
(220,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1011',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2611',TRUE),
(221,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1012',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2612',TRUE),
(222,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1013',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2613',TRUE),
(223,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1014',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2614',TRUE),
(224,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1015',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2615',TRUE),
(225,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1016',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2616',TRUE),
(226,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1017',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2617',TRUE),
(227,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1018',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2618',TRUE),
(228,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1019',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2619',TRUE),
(229,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1020',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2620',TRUE),
(230,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1021',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2621',TRUE),
(231,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1022',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2622',TRUE),
(232,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1023',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2623',TRUE),
(233,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1024',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2624',TRUE),
(234,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1025',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2625',TRUE),
(235,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1026',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2626',TRUE),
(236,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1027',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2627',TRUE),
(237,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1028',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2628',TRUE),
(238,1,'quest_version_test','Sec_employmentHistory','SubSec_employmentStatus','Q_1029',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2629',TRUE),
(239,1,'quest_version_test','Sec_familyInformation','SubSec_maritalStatus','Q_1201',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2778',FALSE),
(240,1,'quest_version_test','Sec_familyInformation','SubSec_maritalStatus','Q_1202',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2779',FALSE),
(241,1,'quest_version_test','Sec_familyInformation','SubSec_maritalStatus','Q_1203',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2780',FALSE),
(242,1,'quest_version_test','Sec_familyInformation','SubSec_maritalStatus','Q_1204',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2781',FALSE),
(243,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1206',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2789',FALSE),
(244,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1207',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2790',FALSE),
(245,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1208',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2791',FALSE),
(246,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1209',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2801',FALSE),
(247,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1210',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2804',FALSE),
(248,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1215',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2805',FALSE),
(249,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1216',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2806',FALSE),
(250,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1217',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2807',FALSE),
(251,1,'quest_version_test','Sec_familyInformation','SubSec_currentSpouse','Q_1220',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2783',FALSE),
(252,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1222',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2840',TRUE),
(253,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1223',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2841',TRUE),
(254,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1224',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2842',TRUE),
(255,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1225',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2844',TRUE),
(256,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1226',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2849',TRUE),
(257,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1227',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2850',TRUE),
(258,1,'quest_version_test','Sec_familyInformation','SubSec_priorSpouses','Q_1228',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2851',TRUE),
(259,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1230',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2883',FALSE),
(260,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1231',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2884',FALSE),
(261,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1232',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2885',FALSE),
(262,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1233',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2889',FALSE),
(263,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1234',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2890',FALSE),
(264,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1235',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2891',FALSE),
(265,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1236',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2892',FALSE),
(266,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1237',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2893',FALSE),
(267,1,'quest_version_test','Sec_familyInformation','SubSec_parent1','Q_1238',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2894',FALSE),
(268,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1240',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2901',FALSE),
(269,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1241',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2902',FALSE),
(270,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1242',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2903',FALSE),
(271,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1243',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2907',FALSE),
(272,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1244',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2908',FALSE),
(273,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1245',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2909',FALSE),
(274,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1246',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2910',FALSE),
(275,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1247',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2911',FALSE),
(276,1,'quest_version_test','Sec_familyInformation','SubSec_parent2','Q_1248',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2912',FALSE),
(277,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1250',FALSE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2741',FALSE),
(278,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1251',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2743',TRUE),
(279,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1252',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2746',TRUE),
(280,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1253',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2749',TRUE),
(281,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1254',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2752',TRUE),
(282,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1255',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2753',TRUE),
(283,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1256',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2754',TRUE),
(284,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1257',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2764',TRUE),
(285,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1258',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2765',TRUE),
(286,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1259',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2766',TRUE),
(287,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1260',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2767',TRUE),
(288,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1261',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2768',TRUE),
(289,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1262',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2769',TRUE),
(290,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1263',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2770',TRUE),
(291,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1264',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2771',TRUE),
(292,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1265',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2772',TRUE),
(293,1,'quest_version_test','Sec_familyInformation','SubSec_childrenInformation','Q_1266',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2773',TRUE),
(294,1,'quest_version_test','Sec_incomeHistory','SubSec_incomeHistory','Q_134',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2501',FALSE),
(295,1,'quest_version_test','Sec_incomeHistory','SubSec_incomeHistory','Q_138',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2502',FALSE),
(296,1,'quest_version_test','Sec_incomeHistory','SubSec_incomeHistory','Q_142',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2503',FALSE);

DELETE FROM petitioner_beneficiary_mapping  WHERE quest_version='quest_version_2';
INSERT INTO petitioner_beneficiary_mapping (id,version,quest_version,petitioner_section_nodeid,petitioner_subsection_nodeid,petitioner_question_nodeid,petitioner_repeatingquestiongroup,beneficiary_section_nodeid,beneficiary_subsection_nodeid,beneficiary_question_nodeid,beneficiary_repeatingquestiongroup) VALUES
(297,1,'quest_version_2','Sec_2','SubSec_5','Q_32',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1901',FALSE),
(298,1,'quest_version_2','Sec_2','SubSec_5','Q_33',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1902',FALSE),
(299,1,'quest_version_2','Sec_2','SubSec_5','Q_34',FALSE,'Sec_nameForBeneficiary','SubSec_currentLegalNameForBeneficiary','Q_1903',FALSE),
(300,1,'quest_version_2','Sec_2','SubSec_6','Q_35',FALSE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1906',FALSE),
(301,1,'quest_version_2','Sec_2','SubSec_6','Q_37',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1908',TRUE),
(302,1,'quest_version_2','Sec_2','SubSec_6','Q_39',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1910',TRUE),
(303,1,'quest_version_2','Sec_2','SubSec_6','Q_41',TRUE,'Sec_nameForBeneficiary','SubSec_otherNamesUsedForBeneficiary','Q_1912',TRUE),
(304,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_42',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2002',FALSE),
(305,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_43',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2003',FALSE),
(306,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_44',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2004',FALSE),
(307,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_45',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2005',FALSE),
(308,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_46',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2006',FALSE),
(309,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_47',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2007',FALSE),
(310,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_48',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2008',FALSE),
(311,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_49',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2009',FALSE),
(312,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_50',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2010',FALSE),
(313,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_51',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2011',FALSE),
(314,1,'quest_version_2','Sec_addressHistory','SubSec_currentPhysicalAddress','Q_52',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentPhysicalAddressForBeneficiary','Q_2012',FALSE),
(315,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_68',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2017',FALSE),
(316,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_69',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2015',FALSE),
(317,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_70',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2016',FALSE),
(318,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_71',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2018',FALSE),
(319,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_72',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2019',FALSE),
(320,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_73',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2020',FALSE),
(321,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_74',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2021',FALSE),
(322,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_75',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2022',FALSE),
(323,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_76',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2023',FALSE),
(324,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_77',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2024',FALSE),
(325,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_78',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2025',FALSE),
(326,1,'quest_version_2','Sec_addressHistory','SubSec_currentMailingAddress','Q_79',FALSE,'Sec_addressHistoryForBeneficiary','SubSec_currentMailingAddressForBeneficiary','Q_2026',FALSE),
(327,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_54',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2040',TRUE),
(328,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_55',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2041',TRUE),
(329,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_56',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2042',TRUE),
(330,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_57',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2043',TRUE),
(331,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_58',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2044',TRUE),
(332,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_59',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2045',TRUE),
(333,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_60',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2046',TRUE),
(334,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_61',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2047',TRUE),
(335,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_62',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2048',TRUE),
(336,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_63',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2049',TRUE),
(337,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_64',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2050',TRUE),
(338,1,'quest_version_2','Sec_addressHistory','SubSec_previousPhysicalAddress','Q_65',TRUE,'Sec_addressHistoryForBeneficiary','SubSec_previousPhysicalAddressForBeneficiary','Q_2052',TRUE),
(339,1,'quest_version_2','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_80',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2151',FALSE),
(340,1,'quest_version_2','Sec_contactInformation','SubSec_mobilePhoneNumber','Q_81',FALSE,'Sec_contactInformationForBeneficiary','SubSec_mobilePhoneNumberForBeneficiary','Q_2152',FALSE),
(341,1,'quest_version_2','Sec_contactInformation','SubSec_officePhoneNumber','Q_84',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2156',FALSE),
(342,1,'quest_version_2','Sec_contactInformation','SubSec_officePhoneNumber','Q_85',FALSE,'Sec_contactInformationForBeneficiary','SubSec_officePhoneNumberForBeneficiary','Q_2157',FALSE),
(343,1,'quest_version_2','Sec_contactInformation','SubSec_email','Q_86',FALSE,'Sec_contactInformationForBeneficiary','SubSec_emailForBeneficiary','Q_2158',FALSE),
(344,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_87',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2201',FALSE),
(345,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_88',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2202',FALSE),
(346,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_89',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2203',FALSE),
(347,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_90',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2205',FALSE),
(348,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_92',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2204',FALSE),
(349,1,'quest_version_2','Sec_birthInformation','SubSec_birthInformation','Q_94',FALSE,'Sec_birthInformationForBeneficiary','SubSec_birthInformationForBeneficiary','Q_2206',FALSE),
(350,1,'quest_version_2','Sec_biographicInformation','SubSec_ethnicity','Q_95',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_ethnicityForBeneficiary','Q_2301',FALSE),
(351,1,'quest_version_2','Sec_biographicInformation','SubSec_race','Q_96',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2303',FALSE),
(352,1,'quest_version_2','Sec_biographicInformation','SubSec_race','Q_97',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2304',FALSE),
(353,1,'quest_version_2','Sec_biographicInformation','SubSec_race','Q_98',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2305',FALSE),
(354,1,'quest_version_2','Sec_biographicInformation','SubSec_race','Q_99',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2306',FALSE),
(355,1,'quest_version_2','Sec_biographicInformation','SubSec_race','Q_100',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_raceForBeneficiary','Q_2307',FALSE),
(356,1,'quest_version_2','Sec_biographicInformation','SubSec_height','Q_101',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2309',FALSE),
(357,1,'quest_version_2','Sec_biographicInformation','SubSec_height','Q_102',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2312',FALSE),
(358,1,'quest_version_2','Sec_biographicInformation','SubSec_height','Q_103',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2310',FALSE),
(359,1,'quest_version_2','Sec_biographicInformation','SubSec_height','Q_104',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_heightForBeneficiary','Q_2311',FALSE),
(360,1,'quest_version_2','Sec_biographicInformation','SubSec_weight','Q_105',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2314',FALSE),
(361,1,'quest_version_2','Sec_biographicInformation','SubSec_weight','Q_106',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_2315',FALSE),
(362,1,'quest_version_2','Sec_biographicInformation','SubSec_weight','Q_6009',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_weightForBeneficiary','Q_6010',FALSE),
(363,1,'quest_version_2','Sec_biographicInformation','SubSec_eyeColor','Q_107',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_eyeColorForBeneficiary','Q_2317',FALSE),
(364,1,'quest_version_2','Sec_biographicInformation','SubSec_hairColor','Q_108',FALSE,'Sec_biographicInformationForBeneficiary','SubSec_hairColorForBeneficiary','Q_2319',FALSE),
(365,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1008',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2608',TRUE),
(366,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1009',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2609',TRUE),
(367,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1010',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2610',TRUE),
(368,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1011',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2611',TRUE),
(369,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1012',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2612',TRUE),
(370,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1013',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2613',TRUE),
(371,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1014',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2614',TRUE),
(372,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1015',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2615',TRUE),
(373,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1016',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2616',TRUE),
(374,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1017',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2617',TRUE),
(375,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1018',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2618',TRUE),
(376,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1019',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2619',TRUE),
(377,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1020',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2620',TRUE),
(378,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1021',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2621',TRUE),
(379,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1022',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2622',TRUE),
(380,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1023',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2623',TRUE),
(381,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1024',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2624',TRUE),
(382,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1025',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2625',TRUE),
(383,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1026',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2626',TRUE),
(384,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1027',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2627',TRUE),
(385,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1028',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2628',TRUE),
(386,1,'quest_version_2','Sec_employmentHistory','SubSec_employmentStatus','Q_1029',TRUE,'Sec_employmentHistoryForBeneficiary','SubSec_employmentStatusForBeneficiary','Q_2629',TRUE),
(387,1,'quest_version_2','Sec_familyInformation','SubSec_maritalStatus','Q_1201',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2778',FALSE),
(388,1,'quest_version_2','Sec_familyInformation','SubSec_maritalStatus','Q_1202',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2779',FALSE),
(389,1,'quest_version_2','Sec_familyInformation','SubSec_maritalStatus','Q_1203',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2780',FALSE),
(390,1,'quest_version_2','Sec_familyInformation','SubSec_maritalStatus','Q_1204',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2781',FALSE),
(391,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1206',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2789',FALSE),
(392,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1207',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2790',FALSE),
(393,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1208',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2791',FALSE),
(394,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1209',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2801',FALSE),
(395,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1210',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2804',FALSE),
(396,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1215',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2805',FALSE),
(397,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1216',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2806',FALSE),
(398,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1217',FALSE,'Sec_familyInformationForBeneficiary','SubSec_currentSpouseForBeneficiary','Q_2807',FALSE),
(399,1,'quest_version_2','Sec_familyInformation','SubSec_currentSpouse','Q_1220',FALSE,'Sec_familyInformationForBeneficiary','SubSec_maritalStatusForBeneficiary','Q_2783',FALSE),
(400,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1222',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2840',TRUE),
(401,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1223',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2841',TRUE),
(402,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1224',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2842',TRUE),
(403,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1225',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2844',TRUE),
(404,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1226',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2849',TRUE),
(405,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1227',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2850',TRUE),
(406,1,'quest_version_2','Sec_familyInformation','SubSec_priorSpouses','Q_1228',TRUE,'Sec_familyInformationForBeneficiary','SubSec_priorSpousesForBeneficiary','Q_2851',TRUE),
(407,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1230',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2883',FALSE),
(408,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1231',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2884',FALSE),
(409,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1232',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2885',FALSE),
(410,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1233',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2889',FALSE),
(411,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1234',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2890',FALSE),
(412,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1235',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2891',FALSE),
(413,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1236',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2892',FALSE),
(414,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1237',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2893',FALSE),
(415,1,'quest_version_2','Sec_familyInformation','SubSec_parent1','Q_1238',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent1ForBeneficiary','Q_2894',FALSE),
(416,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1240',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2901',FALSE),
(417,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1241',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2902',FALSE),
(418,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1242',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2903',FALSE),
(419,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1243',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2907',FALSE),
(420,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1244',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2908',FALSE),
(421,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1245',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2909',FALSE),
(422,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1246',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2910',FALSE),
(423,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1247',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2911',FALSE),
(424,1,'quest_version_2','Sec_familyInformation','SubSec_parent2','Q_1248',FALSE,'Sec_familyInformationForBeneficiary','SubSec_parent2ForBeneficiary','Q_2912',FALSE),
(425,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1250',FALSE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2741',FALSE),
(426,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1251',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2743',TRUE),
(427,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1252',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2746',TRUE),
(428,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1253',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2749',TRUE),
(429,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1254',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2752',TRUE),
(430,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1255',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2753',TRUE),
(431,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1256',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2754',TRUE),
(432,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1257',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2764',TRUE),
(433,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1258',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2765',TRUE),
(434,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1259',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2766',TRUE),
(435,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1260',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2767',TRUE),
(436,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1261',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2768',TRUE),
(437,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1262',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2769',TRUE),
(438,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1263',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2770',TRUE),
(439,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1264',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2771',TRUE),
(440,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1265',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2772',TRUE),
(441,1,'quest_version_2','Sec_familyInformation','SubSec_childrenInformation','Q_1266',TRUE,'Sec_familyInformationForBeneficiary','SubSec_childrenInformationForBeneficiary','Q_2773',TRUE),
(442,1,'quest_version_2','Sec_incomeHistory','SubSec_incomeHistory','Q_134',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2501',FALSE),
(443,1,'quest_version_2','Sec_incomeHistory','SubSec_incomeHistory','Q_138',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2502',FALSE),
(444,1,'quest_version_2','Sec_incomeHistory','SubSec_incomeHistory','Q_142',FALSE,'Sec_incomeHistoryAndFeesPaid','SubSec_incomeHistoryForBeneficiary','Q_2503',FALSE);
