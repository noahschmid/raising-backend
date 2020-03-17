package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.InvestorTypeService;

@Controller
@RequestMapping("/investortype")
public class InvestorTypeController {
	
	@Autowired
	private InvestorTypeService investorTypeService;
	
	@Autowired 
	public InvestorTypeController(InvestorTypeService investorTypeService) {
		this.investorTypeService = investorTypeService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllInvestorTypes() {
		return investorTypeService.getAllInvestorTypes();
	}
	
}
