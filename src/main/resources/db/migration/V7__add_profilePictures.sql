ALTER TABLE startup ADD COLUMN IF NOT EXISTS company varchar;
ALTER TABLE startup RENAME COLUMN revenuemax TO revenuemaxid;
ALTER TABLE startup RENAME COLUMN revenuemin TO revenueminid;
ALTER TABLE startup ADD CONSTRAINT startup_revenuemaxid_fkey FOREIGN KEY (revenuemaxid) REFERENCES revenue(id) ON DELETE RESTRICT;
ALTER TABLE startup ADD CONSTRAINT startup_revenueminid_fkey FOREIGN KEY (revenueminid) REFERENCES revenue(id) ON DELETE RESTRICT;
ALTER TABLE startup ADD COLUMN raised int;
ALTER TABLE startup RENAME COLUMN company TO companyName;


ALTER TABLE account RENAME COLUMN investmentmax TO ticketmaxid;
ALTER TABLE account RENAME COLUMN investmentmin TO ticketminid;
ALTER TABLE account ADD CONSTRAINT account_ticketmaxid_fkey FOREIGN KEY (ticketmaxid) REFERENCES ticketsize(id) ON DELETE RESTRICT;
ALTER TABLE account ADD CONSTRAINT account_ticketminid_fkey FOREIGN KEY (ticketminid) REFERENCES ticketsize(id) ON DELETE RESTRICT;

CREATE TABLE profilepicture(id bigserial PRIMARY KEY, accountid bigint UNIQUE REFERENCES account(id) ON DELETE CASCADE, image bytea);

ALTER TABLE image RENAME TO gallery;
ALTER TABLE gallery DROP CONSTRAINT image_accountid_fkey;
ALTER TABLE gallery ADD CONSTRAINT gallery_accountid_fkey FOREIGN KEY (accountid) REFERENCES account(id)  ON DELETE CASCADE;

CREATE TABLE ticketsize(id serial PRIMARY KEY, step integer);

CREATE TABLE report(id bigint PRIMARY KEY, accountid bigint REFERENCES account(id) ON DELETE CASCADE, reporttime date, reason varchar, description varchar);

DELETE FROM investortype WHERE id = 7;
ALTER SEQUENCE investortype_id_seq RESTART WITH 7;
INSERT INTO investortype(name) VALUES ('Strategic Investor');