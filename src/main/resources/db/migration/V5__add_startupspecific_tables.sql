CREATE TABLE financing (
	id bigserial PRIMARY KEY,
	name varchar
);

CREATE TABLE revenue(
	id bigserial PRIMARY KEY,
	step int
);

CREATE TABLE corporatebody(
	id bigserial PRIMARY KEY,
	type varchar
);

CREATE TABLE privateshareholder(
	id bigserial PRIMARY KEY,
	prename varchar,
	name varchar,
	city varchar,
	equityshare int,
	investortypeid bigserial REFERENCES investortype(id),
	startupid bigserial REFERENCES startup(accountid)
);

CREATE TABLE corporateshareholder(
	id bigserial PRIMARY KEY,
	name varchar,
	website varchar,
	equityshare int,
	corporatebodyid bigserial REFERENCES corporatebody(id),
	startupid bigserial REFERENCES startup(accountid)
);

ALTER TABLE startup ADD financetype int REFERENCES financing(id) ON DELETE RESTRICT;