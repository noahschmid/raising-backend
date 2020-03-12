CREATE TABLE contact (
	id bigserial PRIMARY KEY,
	startupId int REFERENCES startup(accountId)  ON DELETE CASCADE,
	name varchar NOT NULL,
	role varchar NOT NULL,
	email varchar,
	phone varchar
);
CREATE TABLE boardmember (
	id bigserial PRIMARY KEY,
	startupId int REFERENCES startup(accountId)  ON DELETE CASCADE,
	name varchar,
	education varchar,
	profession varchar,
	pullDownType varchar,
	PullDownDuration int
);

CREATE TABLE founder (
	id bigserial PRIMARY KEY, 
	startupId int REFERENCES startup(accountId)  ON DELETE CASCADE,
	name varchar,
	role varchar,
	education varchar
);