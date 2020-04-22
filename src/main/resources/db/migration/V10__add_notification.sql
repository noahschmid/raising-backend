CREATE TABLE IF NOT EXISTS notification(id bigserial PRIMARY KEY, accountid bigint REFERENCES account(id) ON DELETE CASCADE , token varchar, device varchar, notificationtypes varchar);

