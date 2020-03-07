package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Startup;
import ch.raising.services.StartupService;

@Controller
@RequestMapping("/startup")
public class StartupController {
	
	StartupService startupService;
	
	@Autowired
	public StartupController(StartupService startupService) {
		this.startupService = startupService;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getStartupById(@PathVariable int id){
		try {
			Startup startup = startupService.getStartupById(id);
			return ResponseEntity.status(HttpStatus.OK).body(startup);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
}
