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

import ch.raising.models.Boardmember;
import ch.raising.models.Contact;
import ch.raising.models.Founder;
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
        return startupService.getProfile(id);
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
		return startupService.registerProfile(startup);
	}
	/**
	 * Deletes a contact specified by id.
	 * @param id of the contact to be deleted
	 */
	@DeleteMapping("/contact/{id}")
	public ResponseEntity<?> deleteContact(@PathVariable int id){
		//TODO check if the contact is part of the startup
		return startupService.deleteContactByStartupId(id);
	}
	/**
	 * Add a contact to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/contact")
	public ResponseEntity<?> addContact(@RequestBody Contact contact){
		return startupService.addContactByStartupId(contact);
	}
	/**
	 * Deletes a boardmember specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/boardmemeber/{id}")
	public ResponseEntity<?> deleteBoardmember(@PathVariable int id){
		return startupService.deleteBoardmemberByStartupId(id);
	}
	/**
	 * Add a boardmember to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/boardmember")
	public ResponseEntity<?> addBoardmemeber(Boardmember bmem){
		return startupService.addBoardmemberByStartupId(bmem);
	}
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/founder/{id}")
	public ResponseEntity<?> deleteFounder(@PathVariable int id){
		return startupService.deleteFounderByStartupId(id);
	}
	/**
	 * Add a founder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/founder")
	public ResponseEntity<?> addFounder(Founder founder){
		return startupService.addFounderByStartupId(founder);
	}
	/**
	 * Deletes a label specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/label/{id}")
	public ResponseEntity<?> deleteLabel(@PathVariable int id){
		return startupService.deleteLabelByStartupId(id);
	}
	/**
	 * Add a label to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/label/{id}")
	public ResponseEntity<?> addLabel(int id){
		return startupService.addLabelByStartupId(id);
	}
	
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/investmenttype/{id}")
	public ResponseEntity<?> deleteInvestmentPhase(@PathVariable int id){
		return startupService.deleteInvestorTypeByStartupId(id);
	}
	/**
	 * Add a founder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/investmenttype/{id}")
	public ResponseEntity<?> addInvestmentphase(int id){
		return startupService.addInvestorTypeByStartupId(id);
	}
	
	
}