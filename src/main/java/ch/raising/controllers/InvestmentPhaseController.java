package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.InvestmentPhaseService;
import ch.raising.services.LabelService;

@Controller
@RequestMapping("/investmentphase")
public class InvestmentPhaseController {
	
	@Autowired
	private InvestmentPhaseService investmentPhaseService;
	
	@Autowired 
	public InvestmentPhaseController(InvestmentPhaseService investmentPhaseService) {
		this.investmentPhaseService = investmentPhaseService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllLabel() {
		return investmentPhaseService.getAllinvestmentPhases();
	}
	
}
