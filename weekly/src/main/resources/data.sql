INSERT INTO Dish (id, name) VALUES ('1','Daal');

INSERT INTO Ingredient (id, name) VALUES ('11', 'Suesskartoffel');
INSERT INTO Ingredient (id, name) VALUES ('12', 'Linsen');
INSERT INTO Ingredient (id, name) VALUES ('13', 'Kreuzkümmel');
INSERT INTO Ingredient (id, name) VALUES ('14', 'Karotten');
INSERT INTO Ingredient (id, name) VALUES ('15', 'Schwarzwurzel');
INSERT INTO Ingredient (id, name) VALUES ('16', 'Tomaten');


INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '11');
INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '12');
INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '13');
INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '14');
INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '15');
INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('1', '16');

INSERT INTO Dish (id, name) VALUES ('2','Ruehrei');

INSERT INTO Ingredient (id, name) VALUES ('17', 'Ei');


INSERT INTO Dish_Ingredients (dish, ingredient) VALUES ('2', '17');

INSERT INTO Hello_World (message) VALUES ('Hello World');