INSERT INTO questionnaire_version (id, version, quest_version, start_date, date_created, last_updated)
select nextval('questionnaire_version_id_seq'),0,'quest_version_3','2022-04-25 00:00:00', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM questionnaire_version WHERE quest_version = 'quest_version_3');
