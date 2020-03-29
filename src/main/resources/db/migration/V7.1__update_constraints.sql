ALTER TABLE profilepicture DROP CONSTRAINT profilepicture_accountid_fkey;
ALTER TABLE profilepicture ADD CONSTRAINT profilepicture_accountid_fkey FOREIGN KEY (accountid) REFERENCES account(id)  ON DELETE CASCADE;

ALTER TABLE startup DROP COLUMN street;
ALTER TABLE startup DROP COLUMN city;
ALTER TABLE startup DROP COLUMN zipcode;
ALTER TABLE startup DROP COLUMN companyname;