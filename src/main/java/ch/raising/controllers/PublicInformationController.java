package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.AssignmentTableService;

@Controller
@RequestMapping("/public")
public class PublicInformationController {
	
	
	AssignmentTableService publicInformationService;
	
	@Autowired
	public PublicInformationController(AssignmentTableService pis) {
		this.publicInformationService =pis;
	}
	
	@GetMapping("/ticketsize")
	public ResponseEntity<?> ticketsize(){
		return publicInformationService.getAll("ticketsize");
	}
	
	@GetMapping("/continent")
	public ResponseEntity<?> getContinent(){
		return publicInformationService.getAll("continent");
	}
	
	@GetMapping("/country")
	public ResponseEntity<?> getCountry(){
		return publicInformationService.getAllCountries();
	}
	
	@GetMapping("/industry")
	public ResponseEntity<?> getIndustry(){
		return publicInformationService.getAll("industry");
	}
	
	@GetMapping("/investmentphase")
	public ResponseEntity<?> getInvestmentPhase(){
		return publicInformationService.getAll("investmentphase");
	}

	@GetMapping("/investortype")
	public ResponseEntity<?> getInvestorType(){
		return publicInformationService.getAllWithDescription("investortype");
	}
	
	@GetMapping("/label")
	public ResponseEntity<?> getLabel(){
		return publicInformationService.getAllWithDescription("label");
	}
	
	@GetMapping("/support")
	public ResponseEntity<?> getSupport(){
		return publicInformationService.getAll("support");
	}
	
	@GetMapping("/corporatebody")
	public ResponseEntity<?> getCorporatebody(){
		return publicInformationService.getAll("corporatebody");
	}
	
	@GetMapping("/financetype")
	public ResponseEntity<?> getFinanceType(){
		return publicInformationService.getAll("financetype");
	}
	
	@GetMapping("/revenue")
	public ResponseEntity<?> getRevenueSteps(){
		return publicInformationService.getAllRevenueSteps();
	}
 }
