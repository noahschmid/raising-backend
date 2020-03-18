package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.FinanceService;

@Controller
@RequestMapping("/finance")
public class FinanceController {
	
	@Autowired
	private FinanceService financeService;
	
	@Autowired 
	public FinanceController(FinanceService financeService) {
		this.financeService = financeService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllFinances() {
		return financeService.getAllFinances();
	}
	
}
