CREATE TABLE resetCode (
    accountId integer REFERENCES account(id)  ON DELETE CASCADE,
    code varchar UNIQUE,
    expiresAt timestamp,
    PRIMARY KEY (accountId, code));