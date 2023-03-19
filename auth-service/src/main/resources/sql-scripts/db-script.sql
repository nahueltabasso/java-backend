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