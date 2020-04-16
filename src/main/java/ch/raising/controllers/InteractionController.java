package ch.raising.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Interaction;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.services.InteractionService;


@RequestMapping("/interaction")
@Controller
public class InteractionController {

	private final InteractionService interactionService;
	
	@Autowired
	public InteractionController(InteractionService relService) {
		this.interactionService = relService;
	}
	
	
	
	
	@GetMapping
	public ResponseEntity<?> getAllCurrentRelationships() throws EmptyResultDataAccessException, DataAccessException, SQLException {
		return ResponseEntity.ok(interactionService.getAllByAccountId());
	}
	
	@PostMapping
	public ResponseEntity<?> addNewInteraction(@RequestBody Interaction interaction){
		if((interaction.getStartupId() == -1 && interaction.getInvestorId() == -1) || interaction.getInteraction() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Request is missing data. ((startupId || investorId) , interaction)"));
		interactionService.addInteraction(interaction);
		return ResponseEntity.ok().build();
	}
}
