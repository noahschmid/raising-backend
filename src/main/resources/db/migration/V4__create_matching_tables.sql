CREATE TABLE relationship (
    id bigserial PRIMARY KEY,
    startupId int REFERENCES startup(accountId)  ON DELETE CASCADE,
    investorId int REFERENCES investor(accountId)  ON DELETE CASCADE,
    state varchar NOT NULL,
    matchingScore int NOT NULL
);

CREATE TABLE history (
    id bigserial PRIMARY KEY,
    relationshipId int REFERENCES relationship(id)  ON DELETE CASCADE,
    action varchar NOT NULL,
    timestamp timestamp NOT NULL
);