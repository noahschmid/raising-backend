package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.InvestorUpdateRequest;
import ch.raising.models.Startup;
import ch.raising.models.StartupUpdateRequest;
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
     * Update profile of startup by given accountId
     * @param request the id of the account the investor belongs to
     * @return ResponseEntity with status code and error message (if exists)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateInvestorProfile(@PathVariable int id, @RequestBody StartupUpdateRequest request) {
        return startupService.updateStartup(id, request);
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
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteStartup(@PathVariable int id){
		return startupService.deleteStartup(id);
	}
}
