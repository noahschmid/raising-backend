ALTER TABLE iossubscription DROP CONSTRAINT iossubscription_accountid_fkey;
ALTER TABLE androidsubscription DROP CONSTRAINT androidsubscription_accountid_fkey;

ALTER TABLE iossubscription ADD CONSTRAINT iossubscription_accountid_fkey FOREIGN KEY (accountid) REFERENCES account(id) ON DELETE CASCADE;
ALTER TABLE androidsubscription ADD CONSTRAINT androidsubscription_accountid_fkey FOREIGN KEY (accountid) REFERENCES account(id) ON DELETE CASCADE;

ALTER TABLE interaction ADD COLUMN IF NOT EXISTS relationshipId INTEGER REFERENCES relationship(id) ON DELETE CASCADE;

ALTER TABLE account ADD COLUMN IF NOT EXISTS lastchanged TIMESTAMP DEFAULT now();