CREATE TABLE iossubscription(accountid bigint PRIMARY KEY REFERENCES account(id), expires_date timestamp, originaltransactionid varchar, latestreceiptdata varchar);
CREATE TABLE androidsubscription(accountid bigint PRIMARY KEY REFERENCES account(id), purchaseToken varchar, orderId varchar, expires_date timestamp);