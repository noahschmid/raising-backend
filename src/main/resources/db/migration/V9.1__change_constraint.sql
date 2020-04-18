ALTER TABLE interaction DROP CONSTRAINT interaction_investorid_fkey;
ALTER TABLE interaction ADD CONSTRAINT interaction_investorid_fkey FOREIGN KEY (investorid) REFERENCES investor(accountid) ON DELETE SET NULL;
ALTER TABLE interaction DROP CONSTRAINT interaction_startupid_fkey;
ALTER TABLE interaction ADD CONSTRAINT interaction_startupid_fkey FOREIGN KEY (startupid) REFERENCES startup(accountid) ON DELETE SET NULL;