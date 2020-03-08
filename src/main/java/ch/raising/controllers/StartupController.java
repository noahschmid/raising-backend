package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Startup;
import ch.raising.models.StartupProfileResponse;
import ch.raising.services.StartupService;

@Controller
@RequestMapping("/startup")
public class StartupController {
	
	StartupService startupService;
	
	@Autowired
	public StartupController(StartupService startupService) {
		this.startupService = startupService;
	}
	
	/**
     * Return profile of investor by given accountId
     * @param id the id of the account the startup belongs to
     * @return ResponseEntity instance with status code and startup in body
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getInvestorProfile(@PathVariable int id) {
        return startupService.getStartupProfile(id);
	}
	
	/**
	 * Add new startup
	 * @param startup
	 * @return
	 */
	@PostMapping("/{id}")
	public ResponseEntity<?> addStartup(@RequestBody Startup startup) {
		return startupService.addStartup(startup);
	}
}
