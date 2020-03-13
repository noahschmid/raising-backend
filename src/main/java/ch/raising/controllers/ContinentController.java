package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.ContinentService;
import ch.raising.services.LabelService;

@Controller
@RequestMapping("/continent")
public class ContinentController {
	
	@Autowired
	private ContinentService continentService;
	
	@Autowired 
	public ContinentController(ContinentService continentService) {
		this.continentService = continentService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllLabel() {
		return continentService.getAllContinents();
	}
	
}
