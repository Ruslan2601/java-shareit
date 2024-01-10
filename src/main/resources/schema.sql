drop table IF EXISTS bookings;

drop table IF EXISTS status;

drop table IF EXISTS comments;

drop table IF EXISTS items;

drop table IF EXISTS requests;

drop table IF EXISTS users;


CREATE TABLE IF NOT EXISTS users
(
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(50)  NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255)                NOT NULL,
    requester_id INTEGER                     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    created      timestamp without time zone NOT NULL

);

CREATE TABLE IF NOT EXISTS items
(
    item_id      INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(200) NOT NULL,
    is_available BOOLEAN      NOT NULL,
    owner_id     INTEGER      NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    request_id   INTEGER REFERENCES requests (request_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS status
(
    status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name      VARCHAR UNIQUE
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date timestamp without time zone NOT NULL,
    end_date   timestamp without time zone NOT NULL,
    item_id    INTEGER                     NOT NULL REFERENCES items (item_id) ON DELETE CASCADE,
    booker_id  INTEGER                     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    status     INTEGER                     NOT NULL REFERENCES status (status_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text       VARCHAR(255)                NOT NULL,
    item_id    INTEGER                     NOT NULL REFERENCES items (item_id) ON DELETE CASCADE,
    author_id  INTEGER                     NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    created    timestamp without time zone NOT NULL
);
