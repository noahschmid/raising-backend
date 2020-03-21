package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.PublicInformationService;

@Controller
@RequestMapping("/")
public class PublicInformationController {
	
	@Autowired
	PublicInformationService pis;
	
	@Autowired
	public PublicInformationController(PublicInformationService pis) {
		this.pis =pis;
	}
	
	@GetMapping("/continent")
	public ResponseEntity<?> getContinent(){
		return pis.getAll("continent");
	}
	
	@GetMapping("/country")
	public ResponseEntity<?> getCountry(){
		return pis.getAllCountries("country");
	}
	
	@GetMapping("/industry")
	public ResponseEntity<?> getIndustry(){
		return pis.getAll("industry");
	}
	
	@GetMapping("/investmentphase")
	public ResponseEntity<?> getInvestmentPhase(){
		return pis.getAll("investmentphase");
	}

	@GetMapping("/investortype")
	public ResponseEntity<?> getInvestorType(){
		return pis.getAllWithDescription("investortype");
	}
	
	@GetMapping("/label")
	public ResponseEntity<?> getLabel(){
		return pis.getAllWithDescription("label");
	}
	
	@GetMapping("/support")
	public ResponseEntity<?> getSupport(){
		return pis.getAll("support");
	}
	
	@GetMapping("/corporatebody")
	public ResponseEntity<?> getCorporatebody(){
		return pis.getAll("corporatebody");
	}
	
	@GetMapping("/financetype")
	public ResponseEntity<?> getFinanceType(){
		return pis.getAll("financetype");
	}
	
	@GetMapping("/revenue")
	public ResponseEntity<?> getRevenueSteps(){
		return pis.getAllRevenueSteps("revenue");
	}
 }
