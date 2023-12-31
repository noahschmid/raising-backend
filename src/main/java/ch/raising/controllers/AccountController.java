package ch.raising.controllers;

import java.sql.SQLException;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ch.raising.models.Account;
import ch.raising.models.LoginRequest;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.LoginResponse;
import ch.raising.services.AccountService;
import ch.raising.services.AssignmentTableService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.NotAuthorizedException;
import ch.raising.utils.PasswordResetException;
import ch.raising.controllers.AccountController;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.FreeEmailRequest;
/**
 * Manages all the endpoints for the account. This means every endpoint a user might use that is not startup or investor specific.
 * @author noahs, manus
 *
 */
@RequestMapping("/account")
@Controller
public class AccountController {

	private final AccountService accountService;
	private final AssignmentTableService assignmentTableService;

	@Autowired
	public AccountController(AccountService accountService, AuthenticationManager authenticationManager,
			JwtUtil jwtUtil, AssignmentTableService assignmentTableService) {
		this.accountService = accountService;
		this.assignmentTableService = assignmentTableService;
	}
	/**
	 * Check whether account exists with the given username and password
	 * 
	 * @param account provided by the request
	 * @return response instance with message and status code
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<?> login(@RequestBody LoginRequest request)
			throws AuthenticationException, UsernameNotFoundException, DataAccessException, SQLException {
		return ResponseEntity.ok(accountService.login(request));
	}

	/**
	 * Forgot password endpoint. Returns reset code if request is valid
	 * 
	 * @param request including email address of account
	 * @return reset code
	 * @throws MessagingException
	 * @throws EmailNotFoundException
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/forgot")
	@ResponseBody
	public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request)
			throws EmailNotFoundException, MessagingException, DataAccessException, SQLException {
		accountService.forgotPassword(request);
		return ResponseEntity.ok().build();
	}

	/**
	 * Reset password endpoint. Sets new password if request is valid
	 * 
	 * @param request has to include reset code and new password
	 * @return status code
	 * @throws PasswordResetException
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/reset")
	@ResponseBody
	public ResponseEntity<LoginResponse> resetPassword(@RequestBody PasswordResetRequest request)
			throws DataAccessException, SQLException, PasswordResetException {
		return ResponseEntity.ok(accountService.resetPassword(request));
	}

	/**
	 * Get all accounts (only for admins)
	 * 
	 * @return list of all accounts
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseBody
	public ResponseEntity<?> getAccounts() throws DataAccessException, SQLException {
		return ResponseEntity.ok(accountService.getAccounts());
	}

	/**
	 * Check whether email is already registered
	 * 
	 * @param request the request containing the email to test
	 * @return response with status 400 if email is already registered, 200 else
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/valid")
	@ResponseBody
	public ResponseEntity<?> isEmailFree(@RequestBody FreeEmailRequest request)
			throws DataAccessException, SQLException {
		boolean isFree = accountService.isEmailFree(request.getEmail());
		if (isFree) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	/**
	 * registers an account that is neither startup nor investor
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<?> registerAccount(@RequestBody Account account)
			throws DatabaseOperationException, SQLException, Exception {
		return ResponseEntity.ok(accountService.registerProfile(account));
	}

	/**
	 * Searches for an account by id
	 * 
	 * @param id      the id of the desired account
	 * @param request instance of the https request
	 * @return details of specific account
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getAccountById(@PathVariable long id, HttpServletRequest request)
			throws DataAccessException, SQLException, DatabaseOperationException {
		//if (!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
			//return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));
		return ResponseEntity.ok().body(accountService.getAccount(id));
	}

	/**
	 * Delete account
	 * 
	 * @param id
	 * @param request instance of the http request
	 * @return response object with status text
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws Exception
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteAccount(@PathVariable int id, HttpServletRequest request)
			throws DataAccessException, SQLException {
		if (!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

		accountService.deleteProfile(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * Update account
	 * 
	 * @return Response entity with status code and message
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PatchMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> updateAccount(@PathVariable int id, @RequestBody Account updateRequest,
			HttpServletRequest request, @RequestHeader("Authorization") String token) throws DataAccessException, SQLException,
			NotAuthorizedException {
		if (!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

		accountService.updateAccount(id, updateRequest, token);
		return ResponseEntity.ok().build();
	}

	/**
	 * add country to account
	 * 
	 * @param countryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/country")
	@ResponseBody
	public ResponseEntity<?> addCountryToAccount(@RequestBody List<Long> countries)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById("country", countries);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete country from account
	 * 
	 * @param countryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/country/delete")
	@ResponseBody
	public ResponseEntity<?> deleteCountryFromAccount(@RequestBody List<Long> countries)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById("country", countries);
		return ResponseEntity.ok().build();
	}

	/**
	 * add country to account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/country")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> addCountryToAccountByAccountId(@RequestBody List<Long> countries, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById(accountId, "country", countries);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete country from account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/country/delete")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> deleteCountryFromAccountByAccountId(@RequestBody List<Long> countries, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById(accountId, "country", countries);
		return ResponseEntity.ok().build();
	}

	/**
	 * add continent to account
	 * 
	 * @param continentId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/continent")
	@ResponseBody
	public ResponseEntity<?> addContinentToAccount(@RequestBody List<Long> continents)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById("continent", continents);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete continent from account
	 * 
	 * @param continentId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/continent/delete")
	@ResponseBody
	public ResponseEntity<?> deleteContinentFromAccount(@RequestBody List<Long> continents)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById("continent", continents);
		return ResponseEntity.ok().build();
	}

	/**
	 * add continent to account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/continent")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> addContinentToAccountByAccountId(@RequestBody List<Long> continents, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById(accountId, "continent", continents);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete continent from account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/continent/delete")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> deleteContinentFromAccountByAccountId(@RequestBody List<Long> continents,
		@PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById(accountId, "continent", continents);
		return ResponseEntity.ok().build();
	}

	/**
	 * add supporttype to account
	 * 
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/support")
	@ResponseBody
	public ResponseEntity<?> addSupportToAccount(@RequestBody List<Long> support)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById("support", support);
		return ResponseEntity.ok().build();
	}

	/**
	 * add supporttype to account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/support")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> addSupportToAccountById(@RequestBody List<Long> support, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById(accountId, "support", support);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete supporttype from account
	 * 
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/support/delete")
	@ResponseBody
	public ResponseEntity<?> deleteSupportFromAccount(@RequestBody List<Long> support)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById("support", support);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete supporttype from account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/support/delete")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> deleteSupportFromAccountById(@RequestBody List<Long> support, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById(accountId, "support", support);
		return ResponseEntity.ok().build();
	}

	/**
	 * add industry to account
	 * 
	 * @param industryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/industry")
	@ResponseBody
	public ResponseEntity<?> addIndustryToAccount(@RequestBody List<Long> industries)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById("industry", industries);
		return ResponseEntity.ok().build();
	}

	/**
	 * add industry to account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/industry")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> addIndustryToAccountById(@RequestBody List<Long> industries, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.addToAccountById(accountId, "industry", industries);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete industry form account
	 * 
	 * @param industryId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/industry/delete")
	@ResponseBody
	public ResponseEntity<?> deleteIndustryFromAccount(@RequestBody List<Long> industries)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById("industry", industries);
		return ResponseEntity.ok().build();
	}

	/**
	 * delete industry form account
	 * 
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@PostMapping("/{accountId}/industry/delete")
	@Secured("ROLE_ADMIN")
	@ResponseBody
	public ResponseEntity<?> deleteIndustryFromAccountById(@RequestBody List<Long> industries, @PathVariable long accountId)
			throws DataAccessException, SQLException {
		assignmentTableService.deleteFromAccountById(accountId, "industry", industries);
		return ResponseEntity.ok().build();
	}
	/**
	 * This endpoint can be used to refresh a token if it is expiring soon. It only works if the token is still valid.
	 * @param token
	 * @return Responseentity with status 200, 500, 403
	 * @throws NotAuthorizedException if the token is invalid
	 */
	@GetMapping("/refresh")
	public ResponseEntity<?> getNewToken(@RequestHeader("Authorization") String token) throws NotAuthorizedException{
		return ResponseEntity.ok(accountService.refreshToken(token));
	}
}
