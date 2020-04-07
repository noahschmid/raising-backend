ALTER TABLE account ADD COLUMN IF NOT EXISTS countryid bigint REFERENCES country(id) ON DELETE RESTRICT;
ALTER TABLE account ADD COLUMN website varchar;

ALTER TABLE startup DROP COLUMN IF EXISTS website;

