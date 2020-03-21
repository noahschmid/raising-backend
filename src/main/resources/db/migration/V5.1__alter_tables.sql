ALTER TABLE account DROP CONSTRAINT account_username_key;
ALTER TABLE account ADD CONSTRAINT account_emailhas_key UNIQUE (emailhash);

ALTER TABLE country ADD COLUMN continent bigint;
UPDATE continent SET name = 'Oceania' WHERE id = 4;

ALTER TABLE investor DROP COLUMN description;
ALTER TABLE investor ADD column company;

ALTER TABLE account ADD COLUMN description varchar;

ALTER TABLE financing RENAME TO financetype REFERENCES finacetype(id);


ALTER TABLE startup RENAME COLUMN financetype TO finacetypeid;
ALTER TAVLE startup DROP CONSTRAINT startup_investmentphaseid_fkey;
ALTER TABLE startup ADD COLUMN pitch varchar;
ALTER TABLE startup DROP COLUMN revenue;
ALTER TABLE startup ADD COLUMN revenueMax bigint; 
ALTER TABLE startup ADD COLUMN revenueMin bigint;
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