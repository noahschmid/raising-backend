ALTER TABLE label ADD COLUMN IF NOT EXISTS iconid int REFERENCES icon(id) ON DELETE SET NULL;

ALTER TABLE relationship ADD COLUMN IF NOT EXISTS investorDecidedAt timestamp;
ALTER TABLE relationship ADD COLUMN IF NOT EXISTS startupDecidedAt timestamp;

ALTER TABLE share DROP CONSTRAINT IF EXISTS share_accountid_fkey;
ALTER TABLE share ADD CONSTRAINT share_accountid_fkey FOREIGN KEY (accountid) REFERENCES account(id) ON DELETE CASCADE;

ALTER TABLE interaction DROP CONSTRAINT IF EXISTS interaction_investorid_fkey;
ALTER TABLE interaction DROP CONSTRAINT IF EXISTS interaction_startupid_fkey;
ALTER TABLE interaction ADD CONSTRAINT interaction_investorid_fkey FOREIGN KEY (investorid) REFERENCES investor(accountid) ON DELETE CASCADE;
ALTER TABLE interaction ADD CONSTRAINT interaction_startup_fkey FOREIGN KEY (startupid) REFERENCES startup(accountid) ON DELETE CASCADE;
