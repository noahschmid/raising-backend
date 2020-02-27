package com.weekly.data;

import org.springframework.stereotype.Repository;
import com.weekly.weekly.Dish;

@Repository
public interface DishRepository {
	
		Iterable<Dish> findAll();
		Dish findOne(int id);
		Dish save(Dish dish);
		void delete(int dish);
		
}
