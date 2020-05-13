CREATE TABLE IF NOT EXISTS iossubscription(accountid bigint PRIMARY KEY REFERENCES account(id), expires_date timestamp, originaltransactionid varchar, latestreceiptdata varchar);
CREATE TABLE IF NOT EXISTS androidsubscription(accountid bigint PRIMARY KEY REFERENCES account(id), purchaseToken varchar, orderId varchar, expires_date timestamp);

ALTER TABLE androidsubscription ADD COLUMN IF NOT EXISTS subscriptionid varchar;
ALTER TABLE iossubscription ADD COLUMN IF NOT EXISTS subscriptionid varchar;

CREATE TABLE boardmembertype(id bigint PRIMARY KEY, name varchar);

INSERT INTO boardmembertype(id, name) VALUES (1, 'President'),(2, 'Vice President'),(3, 'Member');