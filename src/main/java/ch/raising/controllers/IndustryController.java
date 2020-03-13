package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.IndustryService;

@Controller
@RequestMapping("/industry")
public class IndustryController {
	
	@Autowired
	private IndustryService industryService;
	
	@Autowired 
	public IndustryController(IndustryService industryService) {
		this.industryService = industryService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllLabel() {
		return industryService.getAllIndustries();
	}
	
}
