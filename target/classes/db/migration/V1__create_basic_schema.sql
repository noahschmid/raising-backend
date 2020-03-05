CREATE TABLE account (
	id serial PRIMARY KEY,
	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	roles VARCHAR NOT NULL DEFAULT('ROLE_USER')
);

CREATE TABLE investorType (
  id serial PRIMARY KEY,
  name varchar NOT NULL,
  description varchar
);

CREATE TABLE investor (
  id serial PRIMARY KEY,
  accountId int REFERENCES account(id),
  investmentMin int,
  investmentMax int,
  investorTypeId int REFERENCES investorType(id)
);

CREATE TABLE startup (
  id serial PRIMARY KEY,
  accountId int REFERENCES account(id),
  name varchar NOT NULL,
  investmentMin int,
  investmentMax int,
  investmentPhase int,
  boosts int
);

CREATE TABLE investorTypeAssignment (
  id serial PRIMARY KEY,
  investorTypeId int REFERENCES investorType(id),
  startupId int REFERENCES startup(id)
);

CREATE TABLE label (
  id serial PRIMARY KEY,
  name varchar NOT NULL,
  description varchar
);

CREATE TABLE labelAssignment (
  id serial PRIMARY KEY,
  startupId int REFERENCES startup(id),
  labelId int REFERENCES label(id)
);

CREATE TABLE country (
  id serial PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE continent (
  id serial PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentPhase (
  id serial PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentPhaseAssignment (
  id serial PRIMARY KEY,
  investorId int REFERENCES investor(id),
  investmentPhaseId int REFERENCES investmentPhase(id)
);

CREATE TABLE supervisionType (
  id serial PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE supervisionTypeAssignment (
  id serial PRIMARY KEY,
  accountId int REFERENCES account(id),
  supervisionTypeId int REFERENCES supervisionType(id)
);

CREATE TABLE continentAssignment (
  id serial PRIMARY KEY,
  accountId int REFERENCES account(id),
  continentId int REFERENCES continent(id)
);

CREATE TABLE countryAssignment (
  id serial PRIMARY KEY,
  accountId int REFERENCES account(id),
  countryId int REFERENCES country(id)
);

CREATE TABLE investmentSector (
  id serial PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentSectorAssignment (
  id serial PRIMARY KEY,
  investmentSectorId int REFERENCES investmentSector(id),
  accountId int REFERENCES account(id)
);