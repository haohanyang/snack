CREATE TABLE IF NOT EXISTS app.users
(
    id               varchar(64) PRIMARY KEY,
    username         varchar(30) NOT NULL,
    email            varchar(50) NOT NULL,
    full_name        varchar(50),
    avatar           varchar(300),
    background_image varchar(300),
    bio              varchar(200)
);

CREATE TABLE IF NOT EXISTS app.user_channels
(
    id         SERIAL PRIMARY KEY,
    user1_id   varchar(64) NOT NULL,
    user2_id   varchar(64) NOT NULL,
    created_at timestamp   NOT NULL,
    FOREIGN KEY (user1_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (user2_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app.group_channels
(
    id          SERIAL PRIMARY KEY,
    group_name  varchar(30) NOT NULL,
    avatar      varchar(300),
    description varchar(200),
    created_at  timestamp   NOT NULL
);

CREATE TABLE IF NOT EXISTS app.group_channel_memberships
(
    id         SERIAL PRIMARY KEY,
    member_id  varchar(64) NOT NULL,
    channel_id int         NOT NULL,
    is_creator bool        NOT NULL,
    created_at timestamp,
    FOREIGN KEY (member_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (channel_id)
        REFERENCES app.group_channels (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app.user_assets
(
    id           SERIAL PRIMARY KEY,
    object_key   varchar(36) UNIQUE NOT NULL,
    uploader_id  varchar(64)        NOT NULL,
    file_name    varchar(80)        NOT NULL,
    size         bigint             NOT NULL,
    bucket       varchar(30)        NOT NULL,
    content_type varchar(30)        NOT NULL,
    created_at   timestamp          NOT NULL,
    FOREIGN KEY (uploader_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app.group_channel_attachments
(
    id           SERIAL PRIMARY KEY,
    object_key   varchar(36) UNIQUE NOT NULL,
    uploader_id  varchar(64)        NOT NULL,
    file_name    varchar(80)        NOT NULL,
    size         bigint             NOT NULL,
    bucket       varchar(30)        NOT NULL,
    content_type varchar(30)        NOT NULL,
    channel_id   int                NOT NULL,
    created_at   timestamp          NOT NULL,
    FOREIGN KEY (channel_id)
        REFERENCES app.group_channels (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (uploader_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app.user_channel_attachments
(
    id           SERIAL PRIMARY KEY,
    object_key   varchar(36) UNIQUE NOT NULL,
    uploader_id  varchar(64)        NOT NULL,
    file_name    varchar(80)        NOT NULL,
    size         bigint             NOT NULL,
    bucket       varchar(30)        NOT NULL,
    content_type varchar(30)        NOT NULL,
    channel_id   int                NOT NULL,
    created_at   timestamp          NOT NULL,
    FOREIGN KEY (channel_id)
        REFERENCES app.user_channels (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (uploader_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS app.group_messages
(
    id            SERIAL PRIMARY KEY,
    author_id     varchar(64) NOT NULL,
    channel_id    int         NOT NULL,
    attachment_id int,
    created_at    timestamp,
    content       varchar(500),
    FOREIGN KEY (author_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (channel_id)
        REFERENCES app.group_channels (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (attachment_id)
        REFERENCES app.group_channel_attachments (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS app.user_messages
(
    id            SERIAL PRIMARY KEY,
    author_id     varchar(64) NOT NULL,
    channel_id    int         NOT NULL,
    attachment_id int,
    created_at    timestamp,
    content       varchar(500),
    FOREIGN KEY (channel_id)
        REFERENCES app.user_channels (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (author_id)
        REFERENCES app.users (id)
        ON DELETE NO ACTION ON UPDATE CASCADE,
    FOREIGN KEY (attachment_id)
        REFERENCES app.user_channel_attachments (id)
        ON DELETE NO ACTION ON UPDATE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS app.group_channel_seq;
CREATE SEQUENCE IF NOT EXISTS app.group_channel_membership_seq;
CREATE SEQUENCE IF NOT EXISTS app.user_channel_seq;
CREATE SEQUENCE IF NOT EXISTS app.group_message_seq;
CREATE SEQUENCE IF NOT EXISTS app.user_message_seq;

CREATE SEQUENCE IF NOT EXISTS app.group_channel_attachment_seq;
CREATE SEQUENCE IF NOT EXISTS app.user_asset_seq;
CREATE SEQUENCE IF NOT EXISTS app.user_channel_attachment_seq;
