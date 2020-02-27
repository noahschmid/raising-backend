package com.weekly.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weekly.data.DishRepository;
import com.weekly.data.JdbcDishRepository;
import com.weekly.weekly.Dish;

@Controller
@RequestMapping("/dish")
public class DishController {
	
	@Autowired
	private JdbcDishRepository jdbc;
	private final DishRepository repo;
	
	@Autowired
	public DishController(DishRepository repo) {
		this.repo = repo;
	}
	
	@GetMapping
	public String home(Model model) {
		
		ArrayList<Dish> dishes = new ArrayList<Dish>();
		repo.findAll().forEach(dish -> dishes.add(dish));
		
		model.addAttribute("dishes", dishes);
				
		return "home";
	}
	
	@PutMapping
	@ResponseBody
	public String putDishes(@RequestBody Dish dish) {
		
		jdbc.save(dish);
		
		return "added";
	}
	
	@DeleteMapping
	@ResponseBody
	public String deleteDishes(@RequestBody int id) {
		jdbc.delete(id);
		return "deleted";
	}
	
	
}
