DROP TABLE IF EXISTS USERS cascade;
DROP TABLE IF EXISTS ITEMS cascade;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS comments;

CREATE TABLE IF NOT EXISTS users (
   id       BIGSERIAL PRIMARY KEY,
   name     VARCHAR(255) NOT NULL,
   email    VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    id              BIGSERIAL PRIMARY KEY,
    description     VARCHAR(512) NOT NULL,
    created         TIMESTAMP,
    requester_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(512) NOT NULL,
    available   BOOLEAN DEFAULT TRUE,
    owner_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id          BIGSERIAL PRIMARY KEY,
    start_date  TIMESTAMP,
    end_date    TIMESTAMP,
    item_id     BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    booker_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status      VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS comments (
    id          BIGSERIAL PRIMARY KEY,
    text        VARCHAR(512) NOT NULL,
    item_id     BIGINT REFERENCES items(id) ON DELETE CASCADE,
    author_id   BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created     TIMESTAMP
);