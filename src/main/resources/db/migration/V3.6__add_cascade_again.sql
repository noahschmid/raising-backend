ALTER TABLE continentassignment 
DROP CONSTRAINT continentassignment_accountid_fkey, 
ADD CONSTRAINT continentassignment_accountid_fkey 
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE continentassignment 
DROP CONSTRAINT continentassignment_continentid_fkey,
ADD CONSTRAINT continentassignment_continentid_fkey 
FOREIGN KEY (continentid) 
REFERENCES continent(id)
ON DELETE CASCADE;

ALTER TABLE countryassignment
DROP CONSTRAINT countryassignment_accountid_fkey, 
ADD CONSTRAINT countryassignment_accountid_fkey 
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE countryassignment
DROP CONSTRAINT countryassignment_countryid_fkey, 
ADD CONSTRAINT countryassignment_countryid_fkey 
FOREIGN KEY (countryid) 
REFERENCES country(id)
ON DELETE CASCADE;

ALTER TABLE industryassignment
DROP CONSTRAINT investmentsectorassignment_accountid_fkey, 
ADD CONSTRAINT industryassignment_accountid_fkey
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE industryassignment
DROP CONSTRAINT investmentsectorassignment_investmentsectorid_fkey, 
ADD CONSTRAINT industryassignment_industryid_fkey
FOREIGN KEY (industryid) 
REFERENCES industry(id)
ON DELETE CASCADE;

ALTER TABLE investmentphaseassignment
DROP CONSTRAINT investmentphaseassignment_investmentphaseid_fkey, 
ADD CONSTRAINT  investmentphaseassignment_investmentphaseid_fkey
FOREIGN KEY (investmentphaseid) 
REFERENCES investmentphase(id)
ON DELETE CASCADE;

ALTER TABLE investmentphaseassignment
DROP CONSTRAINT investmentphaseassignment_investorid_fkey, 
ADD CONSTRAINT  investmentphaseassignment_investorid_fkey 
FOREIGN KEY (investorid) 
REFERENCES investor(id)
ON DELETE CASCADE;

ALTER TABLE labelassignment
DROP CONSTRAINT labelassignment_labelid_fkey, 
ADD CONSTRAINT  labelassignment_labelid_fkey
FOREIGN KEY (labelid) 
REFERENCES label(id)
ON DELETE CASCADE;

ALTER TABLE labelassignment
DROP CONSTRAINT labelassignment_startupid_fkey, 
ADD CONSTRAINT  labelassignment_startupid_fkey
FOREIGN KEY (startupid) 
REFERENCES startup(id)
ON DELETE CASCADE;

ALTER TABLE supportassignment
DROP CONSTRAINT supervisiontypeassignment_accountid_fkey, 
ADD CONSTRAINT supportassignment_accountid_fkey
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE supportassignment
DROP CONSTRAINT supervisiontypeassignment_supervisiontypeid_fkey, 
ADD CONSTRAINT supportassignment_supportid_fkey
FOREIGN KEY (supportid) 
REFERENCES support(id)
ON DELETE CASCADE;