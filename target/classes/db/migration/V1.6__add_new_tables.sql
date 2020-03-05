CREATE TABLE investorType (
  id int PRIMARY KEY,
  name varchar NOT NULL,
  description varchar
);

CREATE TABLE investor (
  id int PRIMARY KEY,
  accountId int REFERENCES account(id),
  investmentMin int,
  investmentMax int,
  investorTypeId int REFERENCES investorType(id)
);

CREATE TABLE startup (
  id int PRIMARY KEY,
  accountId int REFERENCES account(id),
  name varchar NOT NULL,
  investmentMin int,
  investmentMax int,
  investmentPhase int,
  boosts int
);

CREATE TABLE investorTypeAssignment (
  id int PRIMARY KEY,
  investorTypeId int REFERENCES investorType(id),
  startupId int REFERENCES startup(id)
);

CREATE TABLE label (
  id int PRIMARY KEY,
  name varchar NOT NULL,
  description varchar
);

CREATE TABLE labelAssignment (
  id int PRIMARY KEY,
  startupId int REFERENCES startup(id),
  labelId int REFERENCES label(id)
);

CREATE TABLE country (
  id int PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE continent (
  id int PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentPhase (
  id int PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentPhaseAssignment (
  id int PRIMARY KEY,
  investorId int REFERENCES investor(id),
  investmentPhaseId int REFERENCES investmentPhase(id)
);

CREATE TABLE supervisionType (
  id int PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE supervisionTypeAssignment (
  id int PRIMARY KEY,
  accountId int REFERENCES account(id),
  supervisionTypeId int REFERENCES supervisionType(id)
);

CREATE TABLE continentAssignment (
  id int PRIMARY KEY,
  accountId int REFERENCES account(id),
  continentId int REFERENCES continent(id)
);

CREATE TABLE countryAssignment (
  id int PRIMARY KEY,
  accountId int REFERENCES account(id),
  countryId int REFERENCES country(id)
);

CREATE TABLE investmentSector (
  id int PRIMARY KEY,
  name varchar NOT NULL
);

CREATE TABLE investmentSectorAssignment (
  id int PRIMARY KEY,
  investmentSectorId int REFERENCES investmentSector(id),
  accountId int REFERENCES account(id)
);