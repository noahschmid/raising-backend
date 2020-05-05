ALTER TABLE settings DROP COLUMN id;
ALTER TABLE settings DROP CONSTRAINT unq_notification_accountid;
ALTER TABLE settings ADD CONSTRAINT notification_pkey PRIMARY KEY (accountid);