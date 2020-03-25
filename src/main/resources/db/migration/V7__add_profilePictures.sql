ALTER TABLE startup ADD COLUMN IF NOT EXISTS company varchar;

CREATE TABLE profilepicture(id bigserial PRIMARY KEY, accountid bigint REFERENCES account(id), image bytea);