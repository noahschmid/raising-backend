
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

@RequestMapping("/admin")
@Controller
public class AdminController {
	
	
	private final AccountService accountService;
	
	@Autowired
	public AdminController(AccountService accountService) {
		this.accountService = accountService; 
	}
	
    @GetMapping("/")
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok().body(new ErrorResponse("hello world"));
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Account admin) throws Exception{
    	return ResponseEntity.ok(accountService.registerAdmin(admin));
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) throws Exception{
    	return ResponseEntity.ok(accountService.adminLogin(request));
    }
}