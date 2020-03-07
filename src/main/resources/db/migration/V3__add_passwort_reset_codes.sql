CREATE TABLE resetCodes (
    accountId integer references account(id),
    code varchar,
    expiresAt timestamp,
    attemptsLeft int NOT NULL DEFAULT(3),
    PRIMARY KEY (accountId, code));