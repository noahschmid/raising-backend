ALTER TABLE share DROP COLUMN IF EXISTS availableuntil;
ALTER TABLE share ADD COLUMN IF NOT EXISTS email varchar;
ALTER TABLE interaction RENAME COLUMN acceptedtimestamp TO acceptedat;