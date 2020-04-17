package ch.raising.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.Interaction;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.services.InteractionRequest;
import ch.raising.services.InteractionService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;


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
	public ResponseEntity<?> addNewInteraction(@RequestBody InteractionRequest interaction) throws DataAccessException, SQLException, InvalidInteractionException{
		interactionService.addInteraction(interaction);
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/accept/{interactionId}")
	public ResponseEntity<?> acceptRequest(@PathVariable long interactionId) throws DataAccessException, InvalidInteractionException, DatabaseOperationException{
		interactionService.acceptInteraction(interactionId);
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/reject/{interactionId}")
	public ResponseEntity<?> rejectRequest(@PathVariable long interactionId) throws DataAccessException, DatabaseOperationException, InvalidInteractionException{
		interactionService.declineInteraction(interactionId);
		return ResponseEntity.ok().build();
	}
}
