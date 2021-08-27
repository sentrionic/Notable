CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "users"
(
    "id"         uuid PRIMARY KEY        DEFAULT uuid_generate_v4(),
    "email"      varchar UNIQUE NOT NULL,
    "password"   varchar        NOT NULL,
    "created_at" timestamptz    NOT NULL DEFAULT now()
);

CREATE TABLE "notes"
(
    "id"         uuid PRIMARY KEY     DEFAULT uuid_generate_v4(),
    "title"      varchar     NOT NULL,
    "body"       text        NOT NULL DEFAULT '',
    "user_id"    uuid        NOT NULL,
    "created_at" timestamptz NOT NULL DEFAULT now(),
    "updated_at" timestamptz NOT NULL DEFAULT now(),
    "deleted_at" timestamptz
);

ALTER TABLE "notes"
    ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

CREATE INDEX ON "users" ("email");

CREATE INDEX ON "notes" ("title");

CREATE INDEX ON "notes" ("body");

CREATE INDEX ON "notes" ("user_id");