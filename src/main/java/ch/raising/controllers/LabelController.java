package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.services.LabelService;

@Controller
@RequestMapping("/label")
public class LabelController {
	
	@Autowired
	private LabelService labelService;
	
	@Autowired 
	public LabelController(LabelService labelservice) {
		this.labelService = labelservice;
	}
	
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> getAllLabel() {
		return labelService.getAllLabels();
	}
	
}
