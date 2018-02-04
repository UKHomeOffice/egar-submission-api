create table GAR_SUBMISSION (
    submission_uuid uuid not null,
    gar_uuid uuid not null,
    submission_type varchar(2000),
    external_submission_ref varchar(200),
    external_submission_reason varchar(200),
    status varchar(255),
    user_uuid uuid not null,
    edit_date_time timestamp not null,
    primary key (submission_uuid)
);
