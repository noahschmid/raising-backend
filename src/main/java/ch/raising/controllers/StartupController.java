package ch.raising.controllers;

import java.util.List;

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

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.AssignmentTableModelWithDescription;
import ch.raising.models.Boardmember;
import ch.raising.models.Contact;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Founder;
import ch.raising.models.PrivateShareholder;
import ch.raising.models.Startup;
import ch.raising.services.AdditionalInformationService;
import ch.raising.services.AssignmentTableService;
import ch.raising.services.StartupService;

@Controller
@RequestMapping("/startup")
public class StartupController {
	
	private StartupService startupService;
	private AssignmentTableService assignmentTableService;
	private AdditionalInformationService additionalInformationService;
	
	@Autowired
	public StartupController(StartupService startupService, AssignmentTableService assignmentTableService) {
		this.startupService = startupService;
		this.assignmentTableService = assignmentTableService;
	}
	
	/**
     * Return profile of investor by given accountId
     * @param id the id of the account the startup belongs to
     * @return ResponseEntity instance with status code and startup in body
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStartupProfile(@PathVariable int id) {
        return startupService.getProfile(id);
	}
    
    /**
     * Update profile of startup by given accountId
     * @param request the id of the account the investor belongs to
     * @return ResponseEntity with status code and error message (if exists)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStartupProfile(@PathVariable int id, @RequestBody Startup request) {
        return startupService.updateProfile(id, request);
    }
	
	/**
	 * Add new startup
	 * @param startup
	 * @return
	 */
	@PostMapping("/register")
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
		return additionalInformationService.deleteContactByStartupId(id);
	}
	/**
	 * Updates a contact specified by id.
	 * @param id of the contact to be deleted
	 */
	@PatchMapping("/contact/{id}")
	public ResponseEntity<?> updateContact(@PathVariable int id, @RequestBody Contact contact){
		//TODO check if the contact is part of the startup
		return additionalInformationService.updateContactByStartupId(contact, id);
	}
	/**
	 * Add a contact to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/contact")
	public ResponseEntity<?> addContact(@RequestBody Contact contact){
		return additionalInformationService.addContactByStartupId(contact);
	}
	/**
	 * Deletes a boardmember specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/boardmemeber/{id}")
	public ResponseEntity<?> deleteBoardmember(@PathVariable int id){
		return additionalInformationService.deleteBoardmemberByStartupId(id);
	}
	/**
	 * Deletes a boardmember specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PatchMapping("/boardmemeber/{id}")
	public ResponseEntity<?> updateBoardmember(@PathVariable int id, @RequestBody Boardmember bmem){
		return additionalInformationService.updateBoardmemberByStartupId(bmem, id);
	}
	/**
	 * Add a boardmember to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/boardmember")
	public ResponseEntity<?> addBoardmemeber(Boardmember bmem){
		return additionalInformationService.addBoardmemberByStartupId(bmem);
	}
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/founder/{id}")
	public ResponseEntity<?> deleteFounder(@PathVariable int id){
		return additionalInformationService.deleteFounderByStartupId(id);
	}
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PatchMapping("/founder/{id}")
	public ResponseEntity<?> updateFounder(@PathVariable int id, @RequestBody Founder founder ){
		return additionalInformationService.updateFounderByStartupId(founder, id);
	}
	/**
	 * Add a founder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/founder")
	public ResponseEntity<?> addFounder(Founder founder){
		return additionalInformationService.addFounderByStartupId(founder);
	}
	/**
	 * Deletes a privateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/privateshareholder/{id}")
	public ResponseEntity<?> deletePrivateShareholder(@PathVariable int id){
		return additionalInformationService.deletePShareholderByStartupId(id);
	}
	/**
	 * Deletes a privateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PatchMapping("/privateshareholder/{id}")
	public ResponseEntity<?> updatePrivateShareholder(@PathVariable int id, @RequestBody PrivateShareholder psh ){
		return additionalInformationService.updatePShareholderByStartupId(psh, id);
	}
	/**
	 * Add a privateshareholder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/privateshareholder")
	public ResponseEntity<?> addPrivateShareholder(PrivateShareholder psh){
		return additionalInformationService.addPShareholderByStartupId(psh);
	}
	/**
	 * Deletes a corporateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@DeleteMapping("/corporateshareholder/{id}")
	public ResponseEntity<?> deleteCorporateShareholder(@PathVariable int id){
		return additionalInformationService.deleteCShareholderByStartupId(id);
	}
	/**
	 * Deletes a corporateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PatchMapping("/corporateshareholder/{id}")
	public ResponseEntity<?> updateCorporateShareholder(@PathVariable int id, @RequestBody CorporateShareholder csh ){
		return additionalInformationService.updateCShareholderByStartupId(csh, id);
	}
	/**
	 * Add a corporateshareholder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/corporateshreholder")
	public ResponseEntity<?> addCorporateShareholder(CorporateShareholder csh){
		return additionalInformationService.addCShareholderByStartupId(csh);
	}
	/**
	 * Deletes a label specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PostMapping("/label/delete")
	public ResponseEntity<?> deleteLabel(@RequestBody List<AssignmentTableModel> labels){
		return assignmentTableService.deleteFromStartupById("label",labels);
	}
	/**
	 * Add a label to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/label")
	public ResponseEntity<?> addLabel(@RequestBody List<AssignmentTableModel> labels){
		return assignmentTableService.addToStartupById("label", labels);
	}
	
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 */
	@PostMapping("/investortype/delete")
	public ResponseEntity<?> deleteInvestmentPhase(@RequestBody List<AssignmentTableModel> invTypes){
		return assignmentTableService.deleteFromStartupById("investortype", invTypes);
	}
	/**
	 * Add a founder to a startup
	 * @param contact to be added
	 * @return a response with a code
	 */
	@PostMapping("/investortype")
	public ResponseEntity<?> addInvestmentphase(@RequestBody List<AssignmentTableModel> invTypes){
		return assignmentTableService.addToStartupById("investortype", invTypes);
	}
	
	
}