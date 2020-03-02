package ch.raising.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.data.AccountRepository;
import ch.raising.models.Account;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.Response;
import ch.raising.services.AccountService;
import ch.raising.utils.JwtUtil;
import ch.raising.controllers.AccountController;

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
	@RequestMapping(value = "/account/login", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        
        final UserDetails userDetails = accountService.loadUserByUsername(request.getUsername());
        final String token = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(new LoginResponse(token));
	}

	/**
	 * Add a new user account
	 * @param account has to include an unique username and a password
	 * @return JSON response with status code and added account details (if added)
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> register(@RequestBody Account account) {
		return accountService.register(account);
	}
	
	/**
	 * Get all accounts
	 * @return list of all accounts
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Account> getAccounts(){
		return accountService.getAccounts();
	}

	/**
	 * Searches for an account by id
	 * @param id the id of the desired account
	 * @return details of specific account
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public Account getAccountById(@PathVariable int id) {
		return accountService.findById(id);
	}

	/**
	 * Delete account
	 * @param id
	 * @return response object with status text
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity deleteAccount(@PathVariable int id) {
		return accountService.deleteAccount(id);
	}
}
