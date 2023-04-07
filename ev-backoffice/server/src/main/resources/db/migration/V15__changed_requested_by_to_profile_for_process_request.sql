alter table process_request drop constraint fkstaoi3d5w8sg1oypkjl3u55qt;
update process_request pr set requested_by_id = (select p.id from profile p where p.user_id = pr.requested_by_id);
alter table process_request add constraint process_request_profile_fk foreign key(requested_by_id) references profile(id) ;