package ch.raising.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AccountUpdateRequest;
import ch.raising.models.ErrorResponse;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.RegistrationRequest;
import ch.raising.services.AccountService;
import ch.raising.utils.JwtUtil;
import ch.raising.controllers.AccountController;
import ch.raising.models.ForgotPasswordRequest;

@RequestMapping("/account")
@Controller
public class AccountController {
	@Autowired
    private final AccountService accountService;
    
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtUtil jwtUtil;
	
	@Autowired
    public AccountController(AccountService accountService, 
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
	}

	/**
	 * Check whether account exists with the given username and password
	 * @param account provided by the request 
	 * @return response instance with message and status code
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Wrong username or password"));
        }

        final AccountDetails userDetails = accountService.loadUserByUsername(request.getUsername());
        final String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getId()));
	}

	/**
	 * Register a new user account
	 * @param request has to include an unique username, email and a password
	 * @return JSON response with status code and error message (if exists)
	 */
	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            accountService.register(request);
            return login(new LoginRequest(request.getUsername(), request.getPassword()));
        } catch (Error e) {
            return ResponseEntity.status(400).body(new ErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Forgot password endpoint. Returns reset code if request is valid 
     * @param request including email address of account
     * @return reset code
     */
    @PostMapping("/forgot")
    @ResponseBody
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return accountService.forgotPassword(request);
    }

    /**
     * Reset password endpoint. Sets new password if request is valid
     * @param request has to include reset code and new password
     * @return status code
     */
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        return accountService.resetPassword(request);
    }
	
	/**
	 * Get all accounts (only for admins)
	 * @return list of all accounts
	 */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
	@ResponseBody
	public List<Account> getAccounts() {
		return accountService.getAccounts();
    }
    
    /**
	 * Searches for an account by id
	 * @param id the id of the desired account
     * @param request instance of the http request
	 * @return details of specific account
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> getAccountById(@PathVariable int id, HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied"));

        return ResponseEntity.ok().body(accountService.findById(id));
    }
    
    /**
	 * Delete account
	 * @param id
     * @param request instance of the http request 
	 * @return response object with status text
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteAccount(@PathVariable int id, HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

        return accountService.deleteAccount(id);
    }

    /**
     * Update account
     * @return Response entity with status code and message
     */
    @PatchMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateAccount(@PathVariable int id, 
                                            @RequestBody AccountUpdateRequest updateRequest,
                                            HttpServletRequest request) {
        if(!accountService.isOwnAccount(id) && !request.isUserInRole("ADMIN"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Access denied"));

        return accountService.updateAccount(id, updateRequest, request.isUserInRole("ADMIN"));
    }
    /**
     * add country to account
     * @param countryId
     * @return
     */
    @PostMapping("/country/{countryId}")
    @ResponseBody
    public ResponseEntity<?> addCountryToAccount(@PathVariable long countryId){
    	return accountService.addCountryToAccountById(countryId);
    }
    /**
     * delete country from account
     * @param countryId
     * @return
     */
    @DeleteMapping("/country/{countryId}")
    @ResponseBody
    public ResponseEntity<?> deleteCountryFromAccount(@PathVariable long countryId){
    	return accountService.deleteCountryFromAccountById(countryId);
    }
    /**
     * add continent to account
     * @param continentId
     * @return
     */
    @PostMapping("/continent/{continentId}")
    @ResponseBody
    public ResponseEntity<?> addContinentToAccount(@PathVariable long continentId){
    	return accountService.addContinentToAccountById(continentId);
    }
    /**
     * delete continent from account
     * @param continentId
     * @return
     */
    @DeleteMapping("/continent/{continentId}")
    @ResponseBody
    public ResponseEntity<?> deleteContinentFromAccount(@PathVariable long continentId){
    	return accountService.deleteContinentFromAccountById(continentId);
    }
    /**
     * add supporttype to account
     * @param supportId
     * @return
     */
    @PostMapping("/support/{supportId}")
    @ResponseBody
    public ResponseEntity<?> addSupportToAccount(@PathVariable long supportId){
    	return accountService.addSupportToAccountById(supportId);
    }
    /**
     * delete supporttype from account
     * @param supportId
     * @return
     */
    @DeleteMapping("/support/{supportId}")
    @ResponseBody
    public ResponseEntity<?> deleteSupportFromAccount(@PathVariable long supportId){
    	return accountService.deleteSupportFromAccountById(supportId);
    }
    /**
     * add industry to account    
     * @param industryId
     * @return
     */
    @PostMapping("/industry/{industryId}")
    @ResponseBody
    public ResponseEntity<?> addIndustryToAccount(@PathVariable long industryId){
    	return accountService.addIndustryToAccountById(industryId);
    }
    /**
     * delete industry form account
     * @param industryId
     * @return
     */
    @DeleteMapping("/industry/{industryId}")
    @ResponseBody
    public ResponseEntity<?> deleteIndustryFromAccount(@PathVariable long industryId){
    	return accountService.deleteContinentFromAccountById(industryId);
    }
    
    
}
 