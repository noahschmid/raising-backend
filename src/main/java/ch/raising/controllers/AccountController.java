package ch.raising.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.data.AccountRepository;
import ch.raising.models.Account;
import ch.raising.models.Response;

@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
	private final AccountRepository accountRepository;
	
	@Autowired
	public AccountController(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * Check whether account exists with the given username and password
	 * @param account provided by the request 
	 * @return response instance with message and status code
	 */
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<Response> login(@RequestBody Account account) {
		if(accountRepository.login(account.getUsername(), account.getPassword()))
			return ResponseEntity.ok(new Response("Login successful"));
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Access denied"));
	}

	/**
	 * Add a new user account
	 * @param account has to include an unique username and a password
	 * @return JSON response with status code and added account details (if added)
	 */
	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<Response> register(@RequestBody Account account) {
		if(accountRepository.usernameExists(account.getUsername()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Username already exists!"));

		accountRepository.add(account);
		return ResponseEntity.ok(new Response("Successfully registered new account!", account));
	}
	
	/**
	 * Get all accounts
	 * @return list of all accounts
	 */
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Account> getAccounts(){
		ArrayList<Account> accounts = new ArrayList<Account>();
		accountRepository.getAllAccounts().forEach(acc -> accounts.add(acc));
		return accounts;
	}

	/**
	 * Searches for an account by id
	 * @param id the id of the desired account
	 * @return details of specific account
	 */
	@GetMapping("/{id}")
	@ResponseBody
	public Account getAccountById(@PathVariable int id) {
		return accountRepository.find(id);
	}

	/**
	 * Delete account
	 * @param id
	 * @return response object with status text
	 */
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity deleteAccount(@PathVariable int id) {
		accountRepository.delete(id);
		return ResponseEntity.ok(new Response("Successfully deleted account"));
	}
}
