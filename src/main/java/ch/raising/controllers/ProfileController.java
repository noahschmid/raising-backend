package ch.raising.controllers;

import org.simpleflatmapper.reflect.ReflectionService.PassThrough;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

import ch.raising.models.LoginRequest;
import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;
import ch.raising.utils.DatabaseOperationException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	private final StartupService suService;
	private final InvestorService invService;
	private final PasswordEncoder encoder;
	
	@Autowired
	public ProfileController(StartupService suService, InvestorService invService, PasswordEncoder encoder) {
		this.suService = suService;
		this.invService = invService;
		this.encoder = encoder;
	}

	@PostMapping("/get/hash")
	public ResponseEntity<?> gethash(@RequestBody LoginRequest toHash){
		LoginRequest hashed = new LoginRequest(encoder.encode(toHash.getEmail()), encoder.encode(toHash.getPassword()));
		return ResponseEntity.ok(hashed);
	}
	@GetMapping("/startup/{id}")
	public ResponseEntity<?> getStartup(@PathVariable long id) throws DataAccessException, SQLException, DatabaseOperationException{
		return ResponseEntity.ok(suService.getAccount(id));
	}
	
	@GetMapping("/investor/{id}")
	public ResponseEntity<?> getInvestor(@PathVariable long id)throws DataAccessException, SQLException, DatabaseOperationException{
		return ResponseEntity.ok(invService.getAccount(id));
	}
	
}
