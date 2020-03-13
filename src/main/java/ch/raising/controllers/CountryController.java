package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.CountryService;

@Controller
@RequestMapping("/country")
public class CountryController {
	
	@Autowired
	private CountryService countryService;
	
	@Autowired 
	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllCountries() {
		return countryService.getAllCountries();
	}
	
}
