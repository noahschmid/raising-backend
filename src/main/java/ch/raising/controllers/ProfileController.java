package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	StartupService suService;
	InvestorService invService;
	
	@Autowired
	public ProfileController(StartupService suService, InvestorService invService) {
		this.suService = suService;
		this.invService = invService;
	}
	
}
