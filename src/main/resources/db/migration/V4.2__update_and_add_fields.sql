ALTER TABLE relationship ADD CONSTRAINT unq_strtup_inv UNIQUE(startupId,investorId);

ALTER TABLE startup DROP COLUMN name;
ALTER TABLE startup DROP COLUMN investmentmin;
ALTER TABLE startup DROP COLUMN investmentmax;
ALTER TABLE startup ADD premoneyvaluation int;
ALTER TABLE startup ADD closingtime date;
ALTER TABLE startup ADD revenue varchar;

ALTER TABLE investor DROP COLUMN name;
ALTER TABLE investor DROP COLUMN investmentmin;
ALTER TABLE investor DROP COLUMN investmentmax;

ALTER TABLE account RENAME COLUMN username TO name;
ALTER TABLE account ADD investmentmin int;
ALTER TABLE account ADD investmentmax int;