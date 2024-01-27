DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(50),
email varchar(50) UNIQUE );

CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
description varchar(1000),
creation_date timestamp,
user_id BIGINT,
CONSTRAINT fk_request_to_user FOREIGN KEY(user_id) REFERENCES users(id) );

CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name varchar(100),
description varchar(1000),
available boolean,
user_id BIGINT,
request_id BIGINT,
CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id),
CONSTRAINT fk_items_to_request FOREIGN KEY(request_id) REFERENCES requests(id) );

CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
start_date timestamp,
end_date timestamp,
item_id BIGINT,
user_id BIGINT,
status varchar,
CONSTRAINT fk_booking_to_item FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT fk_booking_to_user FOREIGN KEY(user_id) REFERENCES users(id) );

CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
text varchar,
item_id BIGINT,
user_id BIGINT,
created timestamp,
CONSTRAINT fk_comment_to_item FOREIGN KEY(item_id) REFERENCES items(id),
CONSTRAINT fk_comment_to_user FOREIGN KEY(user_id) REFERENCES users(id) );
