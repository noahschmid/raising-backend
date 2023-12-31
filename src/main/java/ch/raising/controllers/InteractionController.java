package ch.raising.controllers;

import java.sql.SQLException;

import org.slf4j.LoggerFactory;
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

import ch.raising.models.InteractionRequest;
import ch.raising.models.SharedData;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.services.InteractionService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;

/**
 * This class manages all the requests on {BaseUrl}/interaction/** ry
 * 
 * @author manus
 *
 */
@RequestMapping("/interaction")
@Controller
public class InteractionController {

	private final InteractionService interactionService;

	@Autowired
	public InteractionController(InteractionService relService) {
		this.interactionService = relService;
	}

	/**
	 * 
	 * @return ResponseEntity with a list of all relationships or a response
	 *         according to {@link ControllerExceptionHandler}
	 * @throws EmptyResultDataAccessException
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws InvalidInteractionException
	 */
	@GetMapping
	public ResponseEntity<?> getAllCurrentRelationships()
			throws EmptyResultDataAccessException, DataAccessException, SQLException, InvalidInteractionException {
		return ResponseEntity.ok(interactionService.getAllByAccountId());
	}

	/**
	 * 
	 * @param interaction {@link InteractionRequest} representing the new
	 *                    interaction
	 * @return ResponseEntity with status 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws InvalidInteractionException
	 */
	@PostMapping
	public ResponseEntity<?> addNewInteraction(@RequestBody InteractionRequest interaction)
			throws DataAccessException, SQLException, InvalidInteractionException {
		interactionService.addInteraction(interaction);
		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @param interactionid the id that is to be inserted
	 * @param accept        {@link InteractionRequest} representing the interaction
	 *                      to be added
	 * @return ResponseEntity with a the data of the requestee as jasonor a response
	 *         according to {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws InvalidInteractionException
	 * @throws DatabaseOperationException
	 * @throws SQLException
	 */
	@PatchMapping("/{interactionId}/accept")
	public ResponseEntity<?> acceptRequest(@PathVariable long interactionId, @RequestBody InteractionRequest accept)
			throws DataAccessException, InvalidInteractionException, DatabaseOperationException, SQLException {
		SharedData data = interactionService.acceptInteraction(interactionId, accept);
		if (data == null) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ErrorResponse("The request was acceped, but the other party has not accepted yet"));
		}
		return ResponseEntity.ok(data);
	}

	/**
	 * 
	 * @param interactionId the interaction to be declined
	 * @return ResponseEntity with status 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 * @throws InvalidInteractionException
	 * @throws SQLException
	 */
	@PatchMapping("/{interactionId}/decline")
	public ResponseEntity<?> rejectRequest(@PathVariable long interactionId)
			throws DataAccessException, DatabaseOperationException, InvalidInteractionException, SQLException {
		interactionService.declineInteraction(interactionId);
		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @param interactionId
	 * @return ResponseEntity with status 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 * @throws InvalidInteractionException
	 * @throws SQLException
	 */
	@PatchMapping("/{interactionId}/reopen")
	public ResponseEntity<?> reopenRequest(@PathVariable long interactionId)
			throws DataAccessException, DatabaseOperationException, InvalidInteractionException, SQLException {
		interactionService.reopenInteraction(interactionId);
		return ResponseEntity.ok().build();
	}
}
