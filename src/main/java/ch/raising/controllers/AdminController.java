
package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Account;
import ch.raising.models.LoginRequest;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.services.AccountService;
import ch.raising.services.MatchingService;

/**
 * This class manages the endpoints for {BaseUrl}/admin/. This is reserved for
 * users that have the roles admin.
 * 
 * @see ch.raising.config.SecurityConfig
 * 
 * @author noahs, manus
 *
 */
@RequestMapping("/admin")
@Controller
public class AdminController {

	private final AccountService accountService;
	private final MatchingService matchingService;

	@Autowired
	public AdminController(AccountService accountService, MatchingService matchingService) {
		this.accountService = accountService;
		this.matchingService = matchingService;
	}

	/**
	 * this endpoint is specific for registering an admin.
	 * 
	 * @param admin
	 * @return ResponseEntity with the registered admin as json.
	 * @throws Exception if anything goes wrong
	 */
	@PostMapping("/register")
	public ResponseEntity<?> registerAdmin(@RequestBody Account admin) throws Exception {
		return ResponseEntity.ok(accountService.registerAdmin(admin));
	}

	/**
	 * This endpoint can be used for an admin to log in.
	 * 
	 * @param request
	 * @return ResponseEntity with statuscode 200 or a custom response
	 *         {@link ControllerExceptionHandler}
	 * @throws Exception
	 */
	@PostMapping("/login")
	public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) throws Exception {
		return ResponseEntity.ok(accountService.adminLogin(request));
	}

	/**
	 * Use this endpoint to get all current matches of the matchingalgorithm.
	 * 
	 * @return ResponseEntity with statuscode 200 and all current matches or a
	 *         custom errorresponse {@link ControllerExceptionHandler},
	 *         {@link ErrorResponse}
	 * @throws Exception
	 */
	@GetMapping("/match")
	public ResponseEntity<?> getMatches() throws Exception {
		return ResponseEntity.ok(matchingService.getAllMatches());
	}

	/**
	 *
	 * @return ResponseEntity with statuscode 200 and all current relationships in
	 *         the handshake status or a custom errorresponse
	 *         {@link ControllerExceptionHandler}, {@link ErrorResponse}
	 * @throws Exception
	 */
	@GetMapping("/handshake")
	public ResponseEntity<?> getHandshakes() throws Exception {
		return ResponseEntity.ok(matchingService.getAllHandshakes());
	}

	/**
	 * 
	 *@return ResponseEntity with statuscode 200 and all relationships as a list or a custom errorresponse
	 *         {@link ControllerExceptionHandler}, {@link ErrorResponse}
	 * @throws Exception
	 */
	@GetMapping("/relationship")
	public ResponseEntity<?> getRelationships() throws Exception {
		return ResponseEntity.ok(matchingService.getAllRelationships());
	}
}