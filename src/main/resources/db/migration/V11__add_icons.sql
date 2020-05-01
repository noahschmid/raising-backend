ALTER TABLE notification RENAME TO settings;
ALTER TABLE settings ADD COLUMN language varchar;
ALTER TABLE settings ADD COLUMN numberofmatches varchar;


CREATE TABLE icon(id bigserial PRIMARY KEY, type varchar DEFAULT 'none' , media bytea, lastchanged timestamp DEFAULT now());

ALTER TABLE industry ADD COLUMN iconid int REFERENCES icon(id) ON DELETE SET NULL;
ALTER TABLE support ADD COLUMN iconid int REFERENCES icon(id) ON DELETE SET NULL;
ALTER TABLE investmentphase ADD COLUMN iconid int REFERENCES icon(id) ON DELETE SET NULL;
ALTER TABLE investortype ADD COLUMN iconid int REFERENCES icon(id) ON DELETE SET NULL;