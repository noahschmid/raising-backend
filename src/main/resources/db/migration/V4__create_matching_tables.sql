CREATE TABLE relationship (
    id serial PRIMARY KEY,
    startupId int REFERENCES startup(id),
    investorId int REFERENCES investor(id),
    state varchar,
    matchingScore int
);

CREATE TABLE history (
    id serial PRIMARY KEY,
    relationshipId int REFERENCES relationship(id),
    action varchar,
    timestamp timestamp
);