package ch.raising.controllers;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.AssignmentTableModelWithDescription;
import ch.raising.models.Boardmember;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Founder;
import ch.raising.models.PrivateShareholder;
import ch.raising.models.Startup;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.LoginResponse;
import ch.raising.services.AccountService;
import ch.raising.services.AdditionalInformationService;
import ch.raising.services.AssignmentTableService;
import ch.raising.services.MatchingService;
import ch.raising.services.StartupService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.NotAuthorizedException;

@Controller
@RequestMapping("/startup")
public class StartupController {
	
	private StartupService startupService;
	private AssignmentTableService assignmentTableService;
	private AdditionalInformationService additionalInformationService;
	private MatchingService matchingService;
	private AccountService accountService;
	
	@Autowired
	public StartupController(StartupService startupService, AssignmentTableService assignmentTableService, 
		AdditionalInformationService additionalInformationService, MatchingService matchingService,
		AccountService accountService) {
		this.startupService = startupService;
		this.assignmentTableService = assignmentTableService;
		this.additionalInformationService = additionalInformationService;
		this.matchingService = matchingService;
		this.accountService = accountService;
	}
	
	/**
     * Return profile of investor by given accountId
     * @param id the id of the account the startup belongs to
     * @return ResponseEntity instance with status code and startup in body
	 * @throws SQLException 
	 * @throws DataAccessException 
	 * @throws DatabaseOperationException 
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStartupProfile(@PathVariable int id) throws DataAccessException, SQLException, DatabaseOperationException {
        return ResponseEntity.ok(startupService.getAccount(id));
	}
    
    /**
     * Update profile of startup by given accountId
     * @param request the id of the account the investor belongs to
     * @return ResponseEntity with status code and error message (if exists)
     * @throws SQLException 
     * @throws DataAccessException 
     */
    @PatchMapping("/{id}")
	public ResponseEntity<?> updateStartupProfile(@PathVariable int id, @RequestBody Startup request, 
		@RequestHeader("Authorization") String token) throws DataAccessException, SQLException, NotAuthorizedException {
		LoginResponse response = startupService.updateAccount(id, request, token);
		matchingService.match(id, true);
        return ResponseEntity.ok(response);
    }
	
	/**
	 * Add new startup
	 * @param startup
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws DatabaseOperationException 
	 */
	@PostMapping("/register")
	public ResponseEntity<?> addStartup(@RequestBody Startup startup) throws DatabaseOperationException, SQLException, Exception {
		LoggerFactory.getLogger(StartupController.class).info("uId of startup: " + startup.getUId());
		LoginResponse registrationResponse = startupService.registerProfile(startup);
		matchingService.match(registrationResponse.getId(), true);
		return ResponseEntity.ok(registrationResponse);
	}

	/**
	 * Deletes a boardmember specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws NotAuthorizedException 
	 * @throws DataAccessException 
	 */
	@DeleteMapping("/boardmember/{id}")
	public ResponseEntity<?> deleteBoardmember(@PathVariable int id) throws DataAccessException, NotAuthorizedException, SQLException{
		additionalInformationService.deleteBoardmemberByStartupId(id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a boardmember specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws NotAuthorizedException 
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PatchMapping("/boardmember/{id}")
	public ResponseEntity<?> updateBoardmember(@PathVariable int id, @RequestBody Boardmember bmem) throws DataAccessException, SQLException, NotAuthorizedException{
		additionalInformationService.updateBoardmemberByStartupId(bmem, id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Add a boardmember to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/boardmember")
	public ResponseEntity<?> addBoardmember(@RequestBody Boardmember bmem) throws DataAccessException, SQLException{
		additionalInformationService.addBoardmemberByStartupId(bmem);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws NotAuthorizedException 
	 * @throws DataAccessException 
	 */
	@DeleteMapping("/founder/{id}")
	public ResponseEntity<?> deleteFounder(@PathVariable int id) throws DataAccessException, NotAuthorizedException, SQLException{
		additionalInformationService.deleteFounderByStartupId(id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws NotAuthorizedException 
	 * @throws DataAccessException 
	 */
	@PatchMapping("/founder/{id}")
	public ResponseEntity<?> updateFounder(@PathVariable int id, @RequestBody Founder founder ) throws DataAccessException, NotAuthorizedException, SQLException{
		additionalInformationService.updateFounderByStartupId(founder, id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Add a founder to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/founder")
	public ResponseEntity<?> addFounder(@RequestBody Founder founder) throws DataAccessException, SQLException{
		additionalInformationService.addFounderByStartupId(founder);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a privateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws NotAuthorizedException 
	 * @throws DataAccessException 
	 */
	@DeleteMapping("/privateshareholder/{id}")
	public ResponseEntity<?> deletePrivateShareholder(@PathVariable int id) throws DataAccessException, NotAuthorizedException, SQLException{
		additionalInformationService.deletePShareholderByStartupId(id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a privateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws Exception 
	 */
	@PatchMapping("/privateshareholder/{id}")
	public ResponseEntity<?> updatePrivateShareholder(@PathVariable int id, @RequestBody PrivateShareholder psh ) throws Exception{
		additionalInformationService.updatePShareholderByStartupId(psh, id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Add a privateshareholder to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/privateshareholder")
	public ResponseEntity<?> addPrivateShareholder(@RequestBody PrivateShareholder psh) throws DataAccessException, SQLException{
		additionalInformationService.addPShareholderByStartupId(psh);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a corporateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws NotAuthorizedException 
	 * @throws DataAccessException 
	 */
	@DeleteMapping("/corporateshareholder/{id}")
	public ResponseEntity<?> deleteCorporateShareholder(@PathVariable int id) throws DataAccessException, NotAuthorizedException, SQLException{
		additionalInformationService.deleteCShareholderByStartupId(id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Deletes a corporateshareholder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws NotAuthorizedException 
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PatchMapping("/corporateshareholder/{id}")
	public ResponseEntity<?> updateCorporateShareholder(@PathVariable int id, @RequestBody CorporateShareholder csh ) throws DataAccessException, SQLException, NotAuthorizedException{
		additionalInformationService.updateCShareholderByStartupId(csh, id);
		return ResponseEntity.ok().build();
	}
	/**
	 * Add a corporateshareholder to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/corporateshareholder")
	public ResponseEntity<?> addCorporateShareholder(@RequestBody CorporateShareholder csh) throws DataAccessException, SQLException{
		additionalInformationService.addCShareholderByStartupId(csh);
		return ResponseEntity.ok().build();
	}

	/**
	 * Deletes a label specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/label/delete")
	public ResponseEntity<?> deleteLabel(@RequestBody List<Long> labels) throws DataAccessException, SQLException{
		assignmentTableService.deleteFromStartupById("label",labels);
		return ResponseEntity.ok().build();
	}

	/**
	 * Deletes a label specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/{accountId}/label/delete")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> deleteLabelByAccountId(@RequestBody List<Long> labels, @PathVariable long accountId) throws DataAccessException, SQLException{
		assignmentTableService.deleteFromStartupById(accountId, "label",labels);
		return ResponseEntity.ok().build();
	}
	/**
	 * Add a label to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/label")
	public ResponseEntity<?> addLabel(@RequestBody List<Long> labels) throws DataAccessException, SQLException{
		assignmentTableService.addToStartupById("label", labels);
		return ResponseEntity.ok().build();
	}

	/**
	 * Add a label to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("{accountId}/label")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> addLabel(@RequestBody List<Long> labels,
		@PathVariable long accountId) throws DataAccessException, SQLException{
		assignmentTableService.addToStartupById(accountId, "label", labels);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/investortype/delete")
	public ResponseEntity<?> deleteInvestmentPhase(@RequestBody List<Long> invTypes) throws DataAccessException, SQLException{
		assignmentTableService.deleteFromStartupById("investortype", invTypes);
		return ResponseEntity.ok().build();
	}

	/**
	 * Deletes a founder specified by id.
	 * @param id to be deleted
	 * @return response with statuscode
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/{accountId}/investortype/delete")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> deleteInvestmentPhaseById(@RequestBody List<Long> invTypes,
		@PathVariable long accountId) throws DataAccessException, SQLException{
		assignmentTableService.deleteFromStartupById(accountId, "investortype", invTypes);
		return ResponseEntity.ok().build();
	}

	/**
	 * Add a founder to a startup
	 * @param stakeholder to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/investortype")
	public ResponseEntity<?> addInvestorType(@RequestBody List<Long> invTypes) throws DataAccessException, SQLException{
		assignmentTableService.addToStartupById("investortype", invTypes);
		return ResponseEntity.ok().build();
	}

	/**
	 * Add a founder to a startup
	 * @param invTypes investor types to be added
	 * @return a response with a code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/{accountId}/investortype")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<?> addInvestorTypeByAccountId(@RequestBody List<Long> invTypes, 
		@PathVariable long accountId) throws DataAccessException, SQLException{
		assignmentTableService.addToStartupById(accountId, "investortype", invTypes);
		return ResponseEntity.ok().build();
	}
}