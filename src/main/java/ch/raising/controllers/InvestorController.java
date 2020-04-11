package ch.raising.controllers;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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
import ch.raising.models.Investor;
import ch.raising.models.LoginRequest;
import ch.raising.services.AccountService;
import ch.raising.services.AssignmentTableService;
import ch.raising.services.InvestorService;
import ch.raising.utils.DatabaseOperationException;

@Controller
@RequestMapping("/investor")
public class InvestorController {

	InvestorService investorService;
	AssignmentTableService assignmentService;

	@Autowired
	public InvestorController(InvestorService investorService, AssignmentTableService assignmentService) {
		this.investorService = investorService;
		this.assignmentService = assignmentService;
	}

	/**
	 * Return profile of investor by given accountId
	 * 
	 * @param tableEntryId the tableEntryId of the account the investor belongs to
	 * @return ResponseEntity instance with status code and investor or startup in
	 *         body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getInvestorProfile(@PathVariable int id) throws DataAccessException, SQLException {
		return ResponseEntity.ok(investorService.getProfile(id));
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
	public ResponseEntity<?> updateInvestorProfile(@PathVariable int id, @RequestBody Investor request)
			throws DataAccessException, SQLException {
		investorService.updateProfile(id, request);
		return ResponseEntity.ok().build();
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
		investorService.registerProfile(investor);
		return ResponseEntity.ok().build();
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
	public ResponseEntity<?> deleteInvestmentphaseByInvestorId(@RequestBody List<Long> invPhases)
			throws DataAccessException, SQLException {
		assignmentService.deleteFromInvestorById("investmentphase", invPhases);
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
	public ResponseEntity<?> addInvestmentphaseByInvestorId(@RequestBody List<Long> invPhases)
			throws DataAccessException, SQLException {
		assignmentService.addToInvestorById("investmentphase", invPhases);
		return ResponseEntity.ok().build();
	}
}
