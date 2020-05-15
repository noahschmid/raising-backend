package ch.raising.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.models.AccountDetails;
import ch.raising.models.Investor;
import ch.raising.models.responses.LoginResponse;
import ch.raising.services.AccountService;
import ch.raising.services.AssignmentTableService;
import ch.raising.services.InvestorService;
import ch.raising.services.MatchingService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.NotAuthorizedException;

@Controller
@RequestMapping("/investor")
public class InvestorController {

	InvestorService investorService;
	AssignmentTableService assignmentService;
	MatchingService matchingService;
	AccountService accountService;

	@Autowired
	public InvestorController(InvestorService investorService, AssignmentTableService assignmentService,
		MatchingService matchingService, AccountService accountService) {
		this.investorService = investorService;
		this.assignmentService = assignmentService;
		this.matchingService = matchingService;
		this.accountService = accountService;
	}

	/**
	 * Return profile of investor by given accountId
	 * 
	 * @param tableEntryId the tableEntryId of the account the investor belongs to
	 * @return ResponseEntity instance with status code and investor or startup in
	 *         body
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws DatabaseOperationException 
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getInvestorProfile(@PathVariable int id) throws DataAccessException, SQLException, DatabaseOperationException {
		return ResponseEntity.ok(investorService.getAccount(id));
	}

	/**
	 * Update profile of investor by given accountId
	 * 
	 * @param request the tableEntryId of the account the investor belongs to
	 * @return ResponseEntity with status code and error message (if exists)
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateInvestorProfile(@PathVariable int id, @RequestBody Investor request, 
	@RequestHeader("Authorization") String token)
			throws DataAccessException, SQLException, NotAuthorizedException {
		LoginResponse response = investorService.updateAccount(id, request, token);
		matchingService.match(id, false);
		return ResponseEntity.ok(response);
	}

	/**
	 * Add new investor profile
	 * 
	 * @param investor
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@PostMapping("/register")
	public ResponseEntity<?> addInvestor(@RequestBody Investor investor)
			throws DatabaseOperationException, SQLException, Exception {
		LoginResponse registrationResponse = investorService.registerProfile(investor);
		matchingService.match(registrationResponse.getId(), false);
		return ResponseEntity.ok(registrationResponse);
	}

	/**
	 * deletes the investmentphase of investor
	 * 
	 * @param tableEntryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/investmentphase/delete")
	public ResponseEntity<?> deleteInvestmentphaseByToken(@RequestBody List<Long> invPhases)
			throws DataAccessException, SQLException {
		assignmentService.deleteFromInvestorById("investmentphase", invPhases);
		return ResponseEntity.ok().build();
	}

	/**
	 * deletes the investmentphase of investor by id
	 * 
	 * @param tableEntryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/investmentphase/delete")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> deleteInvestmentphaseByAccountId(@RequestBody List<Long> invPhases, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentService.deleteFromInvestorById(accountId, "support", invPhases);
		return ResponseEntity.ok().build();
	}

	/**
	 * adds investmentphase to investor
	 * 
	 * @param tableEntryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	@PostMapping("/investmentphase")
	public ResponseEntity<?> addInvestmentphaseByToken(@RequestBody List<Long> invPhases)
			throws DataAccessException, SQLException {
		assignmentService.addToInvestorById("investmentphase", invPhases);
		return ResponseEntity.ok().build();
	}

	/**
	 * adds investmentphase to investor by id
	 * 
	 * @param tableEntryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	@PostMapping("/{accountId}/investmentphase")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> addInvestmentphaseByAccountId(@RequestBody List<Long> invPhases, @RequestParam long accountId)
			throws DataAccessException, SQLException {
		assignmentService.addToInvestorById(accountId, "investmentphase", invPhases);
		return ResponseEntity.ok().build();
	}
}
