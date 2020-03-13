package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.LabelService;
import ch.raising.services.SupportService;

@Controller
@RequestMapping("/support")
public class SupportController {
	
	@Autowired
	private SupportService supportService;
	
	@Autowired 
	public SupportController(SupportService supportService) {
		this.supportService = supportService;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllSupports() {
		return supportService.getAllSupports();
	}
	
}
