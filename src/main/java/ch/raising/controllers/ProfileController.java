package ch.raising.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

	StartupService suService;
	InvestorService invService;
	
	@Autowired
	public ProfileController(StartupService suService, InvestorService invService) {
		this.suService = suService;
		this.invService = invService;
	}

	@GetMapping("/startup/{id}")
	public ResponseEntity<?> getStartup(@PathVariable long id) throws DataAccessException, SQLException{
		return ResponseEntity.ok(suService.getAccount(id));
	}
	
	@GetMapping("/investor/{id}")
	public ResponseEntity<?> getInvestor(@PathVariable long id)throws DataAccessException, SQLException{
		return ResponseEntity.ok(invService.getAccount(id));
	}
	
}
