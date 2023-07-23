-- TABLE auth_users
create table auth_users
(
    id                   bigint auto_increment
        primary key,
    creationdatetime     datetime(6)  null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime(6)  null,
    modificationuser     varchar(255) null,
    version              int          null,
    apple_user           bit          not null,
    email                varchar(255) null,
    facebook_user        bit          not null,
    first_login          bit          null,
    google_user          bit          not null,
    password             varchar(255) null,
    username             varchar(255) not null,
    constraint UK_auth_users_email
        unique (email),
    constraint UK_auth_users_username
        unique (username)
);

-- TABLE auth_roles
create table auth_roles
(
    id                   bigint auto_increment
        primary key,
    creationdatetime     datetime(6)  null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime(6)  null,
    modificationuser     varchar(255) null,
    version              int          null,
    role_name            varchar(255) null,
    constraint UK_auth_roles_rolename
        unique (role_name)
);
insert into auth_roles (id, role_name, creationdatetime, creationuser, deleted, modificationdatetime, modificationuser, version)
values (default, 'ROLE_USER', current_timestamp(), 'admin', false, current_timestamp(), 'admin', 0);

insert into auth_roles (id, role_name, creationdatetime, creationuser, deleted, modificationdatetime, modificationuser, version)
values (default, 'ROLE_ADMIN', current_timestamp(), 'admin', false, current_timestamp(), 'admin', 0);

insert into auth_roles (id, role_name, creationdatetime, creationuser, deleted, modificationdatetime, modificationuser, version)
values (default, 'ROLE_SUPERADMIN', current_timestamp(), 'admin', false, current_timestamp(), 'admin', 0);

-- TABLE auth_user_role
create table auth_user_role
(
    id                   bigint auto_increment
        primary key,
    creationdatetime     datetime(6)  null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime(6)  null,
    modificationuser     varchar(255) null,
    version              int          null,
    role_id              bigint       null,
    user_id              bigint       null,
    constraint FK_auth_users
        foreign key (user_id) references auth_users (id),
    constraint FK_auth_role
        foreign key (role_id) references auth_roles (id)
);

-- TABLE auth_refresh_tokens
create table auth_refresh_tokens
(
    id          bigint auto_increment
        primary key,
    expiry_date datetime(6)  not null,
    token       varchar(255) not null,
    user_id     bigint       null,
    constraint UK_token
        unique (token),
    constraint FK_auth_user
        foreign key (user_id) references auth_users (id)
);

-- TABLE auth_password_reset
create table auth_password_reset
(
    id                   bigint auto_increment
        primary key,
    creationdatetime     datetime(6)  null,
    creationuser         varchar(255) null,
    deleted              bit          null,
    modificationdatetime datetime(6)  null,
    modificationuser     varchar(255) null,
    version              int          null,
    code               varchar(255) not null,
    expiry_date          datetime(6)  null,
    user_id              bigint       null,
    constraint UK_code
        unique (code),
    constraint FK_auth_user1
        foreign key (user_id) references auth_users (id)
);

