CREATE TABLE account (
	id serial PRIMARY KEY,
	name VARCHAR(50) UNIQUE,
	password VARCHAR(100) NOT NULL
)