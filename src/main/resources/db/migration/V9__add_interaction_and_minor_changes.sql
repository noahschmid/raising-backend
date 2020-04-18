CREATE TABLE IF NOT EXISTS interaction
(
   id bigserial PRIMARY KEY,
   startupid bigint REFERENCES startup (accountid),
   investorid bigint REFERENCES investor (accountid),
   interaction varchar (20),
   startupState varchar (8),
   investorState varchar (8)
);
CREATE TABLE IF NOT EXISTS businessplan
(
   id bigserial PRIMARY KEY,
   startupid bigint REFERENCES startup (accountid),
   lastchanged timestamp NOT NULL DEFAULT now (),
   media bytea
);
CREATE TABLE IF NOT EXISTS share
(
   id bigserial PRIMARY KEY,
   interactionid bigint REFERENCES interaction (id) ON DELETE CASCADE,
   accountid bigint REFERENCES account (id),
   firstname varchar,
   lastname varchar,
   phone int,
   businessplanid bigint REFERENCES businessplan (id) ON DELETE SET NULL,
   availableuntil timestamp NOT NULL DEFAULT now ()
);
ALTER TABLE account ADD COLUMN IF NOT EXISTS countryid bigint REFERENCES country (id) ON DELETE RESTRICT;
ALTER TABLE account ADD COLUMN IF NOT EXISTS website varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS lastname varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS firstname varchar;
ALTER TABLE account ADD COLUMN IF NOT EXISTS profilepictureid bigint REFERENCES profilepicture (id) ON DELETE SET NULL;
ALTER TABLE startup ADD COLUMN IF NOT EXISTS videoid bigint REFERENCES video (id) ON DELETE SET NULL;
ALTER TABLE startup DROP COLUMN IF EXISTS website;
ALTER TABLE investor DROP COLUMN IF EXISTS firstname;
ALTER TABLE investor DROP COLUMN IF EXISTS lastname;
ALTER TABLE profilepicture ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';
ALTER TABLE gallery ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';
ALTER TABLE video ADD COLUMN IF NOT EXISTS type varchar NOT NULL DEFAULT 'none';
ALTER TABLE interaction ADD COLUMN IF NOT EXISTS createdAt timestamp DEFAULT now ();
ALTER TABLE interaction ADD COLUMN IF NOT EXISTS acceptedTimestamp timestamp DEFAULT now ();
ALTER TABLE gallery ADD COLUMN IF NOT EXISTS lastchanged timestamp NOT NULL DEFAULT now ();
ALTER TABLE video ADD COLUMN IF NOT EXISTS lastchanged timestamp NOT NULL DEFAULT now ();
ALTER TABLE profilepicture ADD COLUMN IF NOT EXISTS lastchanged timestamp NOT NULL DEFAULT now ();
ALTER TABLE businessplan DROP COLUMN IF EXISTS startupid;
ALTER TABLE businessplan ADD COLUMN IF NOT EXISTS accountid bigserial REFERENCES account (id) ON DELETE CASCADE;
ALTER TABLE businessplan ADD COLUMN IF NOT EXISTS type varchar;
ALTER TABLE businessplan RENAME TO document;











