package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.RevenueService;

@Controller
@RequestMapping("/revenue")
public class RevenueController {
	
	@Autowired
	private RevenueService revenueService;
	
	@Autowired 
	public RevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllRevenues() {
		return revenueService.getAllRevenues();
	}
	
}
