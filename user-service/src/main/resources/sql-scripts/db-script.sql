-- TABLE user_users_profiles
create table user_users_profiles
(
    id                   bigint auto_increment primary key,
    creationdatetime     datetime(6)  null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime(6)  null,
    modificationuser     varchar(255) null,
    version              int          null,
    active_profile       bit          null,
    biography            varchar(255) null,
    birth_date           datetime(6)  not null,
    email                varchar(255) null,
    first_name           varchar(255) null,
    last_name            varchar(255) null,
    personal_status      varchar(255) null,
    phone_number         varchar(255) null,
    profile_photo        varchar(255) null,
    studies              varchar(255) null,
    user_id              bigint       not null,
    verified_profile     bit          not null
);

-- TABLE user_friend_requests
create table user_friend_requests
(
    id                   bigint auto_increment primary key,
    creationdatetime     datetime     null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime     null,
    modificationuser     varchar(255) null,
    version              int          null,
    from_email           varchar(255) null,
    status               bit          null,
    to_email             varchar(255) null,
    from_user_id         bigint       null,
    to_user_id           bigint       null,
    constraint FK_user_users_profiles
        foreign key (to_user_id) references user_users_profiles (id),
    constraint FK_user_users_profiles_1
        foreign key (from_user_id) references user_users_profiles (id)
);

-- TABLE user_locations
create table user_locations
(
    id                   bigint auto_increment primary key,
    creationdatetime     datetime     null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime     null,
    modificationuser     varchar(255) null,
    version              int          null,
    address              varchar(255) null,
    city                 varchar(255) null,
    coordinate           point        not null,
    country              varchar(255) null,
    latitude             double       not null,
    longitude            double       not null,
    postal_code          varchar(255) null,
    state                varchar(255) null,
    user_profile_id      bigint       null,
    constraint FK_user_users_profiles
        foreign key (user_profile_id) references user_users_profiles (id)
);

-- SRID
ALTER TABLE user_locations
MODIFY coordinate POINT SRID 4326;

-- INDEX
CREATE SPATIAL INDEX idx_coordinate ON user_locations(coordinate);

