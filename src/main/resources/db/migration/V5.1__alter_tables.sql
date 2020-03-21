ALTER TABLE account DROP CONSTRAINT account_username_key;
ALTER TABLE account ADD CONSTRAINT account_emailhash_key UNIQUE (emailhash);
ALTER TABLE account ADD COLUMN pitch varchar;
ALTER TABLE account ADD COLUMN company varchar;
ALTER TABLE account ADD COLUMN description varchar;

ALTER TABLE country ADD COLUMN continentid bigint;
UPDATE continent SET name = 'Oceania' WHERE id = 4;

ALTER TABLE investor DROP COLUMN description;

ALTER TABLE financing RENAME TO financetype;
ALTER TABLE financetype RENAME COLUMN name TO type;

ALTER TABLE startup RENAME COLUMN financetype TO finacetypeid;
ALTER TABLE startup DROP CONSTRAINT startup_investmentphaseid_fkey;
ALTER TABLE startup ADD CONSTRAINT startup_investmentphaseid_fkey ON DELETE RESTRICT;
ALTER TABLE startup DROP COLUMN revenue;
ALTER TABLE startup ADD COLUMN revenueMax int; 
ALTER TABLE startup ADD COLUMN revenueMin int;
ALTER TABLE startup ADD COLUMN scope int;
ALTER TABLE startup ADD COLUMN uid varchar;
ALTER TABLE startup ADD COLUMN foundingyear int;

ALTER TABLE boardmember ADD COLUMN countryId bigint REFERENCES country(id);
ALTER TABLE corporateshareholder ADD COLUMN countryid bigint REFERENCES country(id);
ALTER TABLE privateshareholder ADD COLUMN countryid bigint REFERENCES country(id);
ALTER TABLE founder ADD COLUMN countryid bignint bigint REFERNCES country(id);

ALTER TABLE founder RENAME COLUMN name to firstname;
ALTER TABLE founder ADD COLUMN firstname varchar;
ALTER TABLE founder RENAME COLUMN role to position;

ALTER TABLE boardmember RENAME COLUMN name to firstname;
ALTER TABLE boardmember ADD COLUMN firstname varchar;
ALTER TABLE boardmember ADD COLUMN position varchar;
ALTER TABLE boardmember RENAME COLUMN pulldownduration to membersince;
ALTER TABLE boardmember DROP COLUMN pulldowntype;

ALTER TABLE privateshareholder RENAME COLUMN prename to firstname;
ALTER TABLE privateshareholder RENAME COLUMN name to lastname;

ALTER TABLE contact RENAME COLUMN role TO poisition;

ALTER TABLE corporatebody RENAME COLUMN type TO name;
