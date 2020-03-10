ALTER TABLE continentassignment 
DROP CONSTRAINT continentassignment_accountid_fkey, 
ADD CONSTRAINT continentassignment_accountid_fkey 
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;