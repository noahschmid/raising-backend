CREATE TABLE contact (
	id serial PRIMARY KEY,
	startupId int REFERENCES startup(id),
	name varchar,
	role varchar,
	email varchar,
	phone varchar
);
CREATE TABLE boardmember (
	id serial PRIMARY KEY,
	startupId int REFERENCES startup(id),
	name varchar,
	education varchar,
	profession varchar,
	pullDownType varchar,
	PullDownDuration int
);
CREATE TABLE founder (
	id serial PRIMARY KEY, 
	startipId int REFERENCES startup(id),
	name varchar,
	role varchar,
	education varchar
);