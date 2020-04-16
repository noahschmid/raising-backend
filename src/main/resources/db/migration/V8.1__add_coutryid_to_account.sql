CREATE TABLE IF NOT EXISTS interaction(id bigserial PRIMARY KEY, 
startupid bigint REFERENCES startup(accountid), 
investorid bigint REFERENCES investor(accountid), 
interaction varchar(20), 
startupState varchar(8), 
investorState varchar(8));


ALTER TABLE account ADD COLUMN IF NOT EXISTS countryid bigint REFERENCES country(id) ON DELETE RESTRICT;
ALTER TABLE account ADD COLUMN IF NOT EXISTS website varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS lastname varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS firstname varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS profilepictureid bigint REFERENCES profilepicture(id) ON DELETE SET NULL;

ALTER TABLE startup ADD COLUMN IF NOT EXISTS videoid bigint REFERENCES video(id) ON DELETE SET NULL;
ALTER TABLE startup DROP COLUMN IF EXISTS website;

ALTER TABLE investor DROP COLUMN IF EXISTS firstname;
ALTER TABLE investor DROP COLUMN IF EXISTS lastname;

ALTER TABLE profilepicture ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';
ALTER TABLE gallery ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';
ALTER TABLE video ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';