package com.weekly.weekly;

import java.util.List;

import lombok.Data;

@Data
public class Dish {
	private final String name;
	private final int id;
	private List<String> ingredients;
}
