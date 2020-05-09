
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

@RequestMapping("/admin")
@Controller
public class AdminController {
	
	
    private final AccountService accountService;
    private final MatchingService matchingService;
	
	@Autowired
    public AdminController(AccountService accountService,
    MatchingService matchingService) {
        this.accountService = accountService;
        this.matchingService = matchingService; 
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

    @GetMapping("/match")
    public ResponseEntity<?> getMatches() throws Exception {
        return ResponseEntity.ok(matchingService.getAllMatches());
    }

    @GetMapping("/handshake")
    public ResponseEntity<?> getHandshakes() throws Exception {
        return ResponseEntity.ok(matchingService.getAllHandshakes());
    }

    @GetMapping("/relationship")
    public ResponseEntity<?> getRelationships() throws Exception {
        return ResponseEntity.ok(matchingService.getAllRelationships());
    }
}