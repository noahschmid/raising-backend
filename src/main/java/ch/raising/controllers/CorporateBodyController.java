package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.CorporateBodyService;

@Controller
@RequestMapping("/corporatebody")
public class CorporateBodyController {
	
	@Autowired
	private CorporateBodyService corporateBodyService;
	
	@Autowired 
	public CorporateBodyController(CorporateBodyService corporateBodyService) {
		this.corporateBodyService = corporateBodyService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllCorporateBodies() {
		return corporateBodyService.getAllCorporateBodies();
	}
	
}
