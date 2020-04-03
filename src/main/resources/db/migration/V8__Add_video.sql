CREATE TABLE video(id bigserial PRIMARY KEY, accountid bigint REFERENCES startup(accountid) ON DELETE CASCADE, media bytea);

ALTER TABLE startup DROP COLUMN turnover;

ALTER TABLE investor ADD COLUMN firstName varchar;
ALTER TABLE investor ADD COLUMN lastName varchar;

ALTER TABLE account RENAME COLUMN company TO companyName;
ALTER TABLE account DROP COLUMN name;

ALTER TABLE profilepicture RENAME COLUMN image TO media;

ALTER TABLE gallery RENAME COLUMN image TO media;

DROP TABLE contact;
