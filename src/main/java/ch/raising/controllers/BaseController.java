package ch.raising.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.data.JdbcRepository;
import ch.raising.models.User;

@Controller
@RequestMapping("/test")
public class BaseController {
	
	
	@Autowired
	private JdbcRepository jdbc;
	private final JdbcRepository repo;
	
	@Autowired
	public BaseController(JdbcRepository repo) {
		this.repo = repo;
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<User> base(){
		
		
		ArrayList<User> users = new ArrayList<User>();
		
		repo.findALL().forEach(user -> users.add(user));
		
		
		return users;
	}
	
}
