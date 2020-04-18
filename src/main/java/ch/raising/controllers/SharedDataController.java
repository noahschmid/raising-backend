package ch.raising.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.SharedDataService;

@Controller
@RequestMapping("/share")
public class SharedDataController {
	
	SharedDataService sharedDataService;
	
	@Autowired
	public SharedDataController(SharedDataService shareService) {
		this.sharedDataService = shareService;
	}
	
	@GetMapping
	public ResponseEntity<?> getAllSharedDataOfAccount() throws EmptyResultDataAccessException, SQLException {
		return ResponseEntity.ok(sharedDataService.getAllByAccount());
	}
	@GetMapping("/{interactionId}")
	public ResponseEntity<?> getSharedDataById(@PathVariable long interactionId) throws EmptyResultDataAccessException, SQLException{
		return ResponseEntity.ok(sharedDataService.getByInteractionId(interactionId));
	}
}
