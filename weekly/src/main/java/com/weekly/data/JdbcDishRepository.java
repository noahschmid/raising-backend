package com.weekly.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.weekly.weekly.Dish;

@Repository
public class JdbcDishRepository implements DishRepository {

	private JdbcTemplate jdbc;

	@Autowired
	public JdbcDishRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Iterable<Dish> findAll() {
		String getDish = "select id, name from Dish";
		String getIng = "SELECT * "
				+ "FROM Dish "
				+ "INNER JOIN Dish_Ingredients ON Dish.id = Dish_Ingredients.dish "
				+ "JOIN Ingredient ON Dish_Ingredients.ingredient = Ingredient.id";
		
		
		List<Dish> dishes =  jdbc.query(getDish, this::mapRowToDish);
		
		for(Dish dish: dishes) {
			//find all ingredients
			ArrayList<String> ingredients = new ArrayList<String>();
			ingredients.addAll(jdbc.query("SELECT * "
					+ "FROM Dish "
					+ "LEFT OUTER JOIN Dish_Ingredients ON " + dish.getId() +" = Dish_Ingredients.dish "
					+ "LEFT OUTER JOIN Ingredient ON Dish_Ingredients.ingredient = Ingredient.id "
					+ "WHERE " + dish.getId() + " = Dish.id" , this::mapRowToIngredient));
			//add them to dish
			dish.setIngredients(ingredients);
		}
		
		return dishes;
	}

	@Override
	public Dish findOne(int id) {
		return jdbc.queryForObject("select id, name from Dish where id=" + id, this::mapRowToDish);
	}

	@Override
	public Dish save(Dish dish) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		jdbc.update("insert into Dish (id, name) values (?,?)", dish.getId(), dish.getName());
		for(String ing: dish.getIngredients()) {
			map.put(ing, random());
			jdbc.update("insert into Ingredient (id, name) values (?,?)", map.get(ing), ing);
		}
		for(String ing: map.keySet()) {
			jdbc.update("insert into Dish_Ingredients (dish, ingredient) values(?,?)", dish.getId(), map.get(ing));
		}
		return dish;
	}
	@Override
	public void delete(int id) {
		jdbc.execute("delete from Ingredient where ingredient.id in "
				+ "(select Dish_Ingredients.ingredient from Dish join Dish_Ingredients "
				+ "on "+ id +"= Dish_Ingredients.dish)");
		jdbc.execute("delete from Dish_Ingredients where dish = " + id );
		jdbc.execute("delete from Dish where Dish.id = " + id );
		
	}

	private Dish mapRowToDish(ResultSet rs, int rowNum) throws SQLException {
		return new Dish(rs.getString("name"), rs.getInt("id"));
	}

	private String mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
		return rs.getString("Ingredient.name");
	}
	
	private int random() {
	    double base = 1000;
	    double range= 9000;
		return (int) (base +  Math.random()*range);
	}
}
