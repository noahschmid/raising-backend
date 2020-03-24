ALTER TABLE privateshareholder DROP CONSTRAINT privateshareholder_startupid_fkey;
ALTER TABLE privateshareholder ADD CONSTRAINT privateshareholder_startupid_fkey FOREIGN KEY (startupid) REFERENCES startup(accountid)  ON DELETE CASCADE;

ALTER TABLE corporateshareholder DROP CONSTRAINT corporateshareholder_startupid_fkey;
ALTER TABLE corporateshareholder ADD CONSTRAINT corporateshareholder_startupid_fkey FOREIGN KEY (startupid) REFERENCES startup(accountid)  ON DELETE CASCADE;

ALTER TABLE startup RENAME COLUMN finacetypeid TO financetypeid;
