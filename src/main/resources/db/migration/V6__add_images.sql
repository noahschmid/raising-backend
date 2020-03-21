CREATE TABLE image(
   id serial PRIMARY KEY,
   accountid bigint REFERENCES account(id) ON DELETE CASCADE,
   image bytea
);