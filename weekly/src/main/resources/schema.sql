CREATE TABLE IF NOT EXISTS Dish(
	id int,
	name varchar(50));
CREATE TABLE IF NOT EXISTS Ingredient(
	id int,
	name varchar(50));
CREATE TABLE IF NOT EXISTS Dish_Ingredients(
	dish int,
	ingredient int);
CREATE TABLE IF NOT EXISTS Hello_World(
	message varchar(100));
