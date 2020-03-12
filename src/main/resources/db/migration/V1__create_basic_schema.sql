CREATE TABLE account (
	id bigserial PRIMARY KEY,
	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	roles VARCHAR NOT NULL DEFAULT('ROLE_USER'),
  emailhash VARCHAR NOT NULL
);

CREATE TABLE investorType (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL,
  description varchar
);

CREATE TABLE investmentPhase (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE investor (
  accountId int PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
  name varchar,
  description varchar,
  investmentMin int,
  investmentMax int,
  investorTypeId int REFERENCES investorType(id) ON DELETE CASCADE
);

CREATE TABLE startup (
  accountId int PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
  name varchar NOT NULL,
  investmentMin int DEFAULT 0,
  investmentMax int DEFAULT 0,
  investmentPhaseId int REFERENCES investmentPhase(id)  ON DELETE CASCADE,
  boosts int DEFAULT 0,
  numberOfFTE int,
  turnover int,
  street varchar,
  city varchar,
  website varchar, 
  breakevenYear int,
  zipCode int
);

CREATE TABLE investorTypeAssignment (
  id bigserial PRIMARY KEY,
  investorTypeId int REFERENCES investorType(id) ON DELETE CASCADE,
  startupId int REFERENCES startup(accountId) ON DELETE CASCADE
);

CREATE TABLE label (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL,
  description varchar
);

CREATE TABLE labelAssignment (
  id bigserial PRIMARY KEY,
  startupId int REFERENCES startup(accountId) ON DELETE CASCADE,
  labelId int REFERENCES label(id) ON DELETE CASCADE
);

CREATE TABLE country (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE continent (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE investmentPhaseAssignment (
  id bigserial PRIMARY KEY,
  investorId int REFERENCES investor(accountId) ON DELETE CASCADE,
  investmentPhaseId int REFERENCES investmentPhase(id) ON DELETE CASCADE
);

CREATE TABLE support (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE supportAssignment (
  id bigserial PRIMARY KEY,
  accountId int REFERENCES account(id) ON DELETE CASCADE,
  supportId int REFERENCES support(id) ON DELETE CASCADE
);

CREATE TABLE continentAssignment (
  id bigserial PRIMARY KEY,
  accountId int REFERENCES account(id) ON DELETE CASCADE,
  continentId int REFERENCES continent(id) ON DELETE CASCADE
);

CREATE TABLE countryAssignment (
  id bigserial PRIMARY KEY,
  accountId int REFERENCES account(id) ON DELETE CASCADE,
  countryId int REFERENCES country(id) ON DELETE CASCADE
);

CREATE TABLE industry (
  id bigserial PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE industryAssignment (
  id bigserial PRIMARY KEY,
  industryId int REFERENCES industry(id) ON DELETE CASCADE,
  accountId int REFERENCES account(id) ON DELETE CASCADE
);