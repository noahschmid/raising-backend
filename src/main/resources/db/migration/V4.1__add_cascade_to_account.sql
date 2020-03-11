ALTER TABLE startup
DROP CONSTRAINT startup_accountid_fkey, 
ADD CONSTRAINT startup_accountid_fkey 
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE investor
DROP CONSTRAINT investor_accountid_fkey,
ADD CONSTRAINT investor_accountid_fkey
FOREIGN KEY (accountid)
REFERENCES account(id)
ON DELETE CASCADE;