package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.InteractionRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Interaction;
import ch.raising.models.InteractionState;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.models.State;
import ch.raising.models.responses.CompleteInteractions;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;

@Service
public class InteractionService {

	private final InteractionRepository interactionRepo;
	private final RelationshipRepository relationshipRepo;
	private final AccountRepository accountRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InteractionService.class);

	@Autowired
	public InteractionService(InteractionRepository interactionRepo, RelationshipRepository relationshipRepo,
			AccountRepository accountRepo) {
		this.interactionRepo = interactionRepo;
		this.relationshipRepo = relationshipRepo;
		this.accountRepo = accountRepo;
	}

	public CompleteInteractions getAllByAccountId()
			throws EmptyResultDataAccessException, DataAccessException, SQLException {
		long accountId = getAccountId();
		List<Interaction> interaction = interactionRepo.findAll(accountId);
		List<RelationshipState> allExceptMatch = getAllExceptMatch();
		List<Relationship> relationship = new ArrayList<Relationship>();
		for (RelationshipState s : allExceptMatch) {
			relationship.addAll(relationshipRepo.getByAccountIdAndState(accountId, s));
		}

		return new CompleteInteractions(interaction, relationship);
	}

	private List<RelationshipState> getAllExceptMatch() {
		RelationshipState[] all = RelationshipState.values();
		RelationshipState[] allExceptMatch = new RelationshipState[all.length-1];
		int c = 0;
 		for(int i = 0; i < all.length; i++) {
			if(all[i] != RelationshipState.MATCH) {
				allExceptMatch[c++] = all[i];
			}
		}
		
		return Arrays.asList(allExceptMatch);
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}

	private boolean isStartup() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getStartup();
	}

	private boolean isInvestor() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getInvestor();
	}

	public void addInteraction(InteractionRequest interaction)
			throws DataAccessException, SQLException, InvalidInteractionException {
		assert interaction != null;
		LOGGER.info("Interaction: (id: {}, interaction: {})", interaction.getId(), interaction.getInteraction().name());
		Interaction insert = new Interaction();
		long requesteeId = getAccountId();
		if (interaction.getInteraction() == null)
			throw new InvalidInteractionException("no interaction set");

		if (isStartup() && accountRepo.isInvestor(interaction.getId())) {
			insert.setStartupId(requesteeId);
			insert.setInvestorId(interaction.getId());
			insert.setStartupState(State.ACCEPTED);
			insert.setInvestorState(State.OPEN);
		} else if (isInvestor() && accountRepo.isStartup(interaction.getId())) {
			insert.setInvestorId(requesteeId);
			insert.setStartupId(interaction.getId());
			insert.setInvestorState(State.ACCEPTED);
			insert.setStartupState(State.OPEN);
		} else {
			throw new InvalidInteractionException(
					"This type of account cannot have a relationship. One party has to be a startup and the other has to be an investor. token: (startup: "
							+ isStartup() + " ,investor: " + isInvestor() +") "+ " investor: " + accountRepo.isInvestor(interaction.getId()));
		}
		insert.setInteraction(interaction.getInteraction());
		interactionRepo.addInteraction(insert);

	}

	public void acceptInteraction(long interactionId)
			throws InvalidInteractionException, DataAccessException, DatabaseOperationException {
		if (isStartup()) {
			interactionRepo.startupUpdate(State.ACCEPTED, interactionId, getAccountId());
		} else if (isInvestor()) {
			interactionRepo.startupUpdate(State.ACCEPTED, interactionId, getAccountId());
		} else {
			throw new InvalidInteractionException("This type of account cannot accept an interaction");
		}
	}

	public void declineInteraction(long interactionId)
			throws DataAccessException, DatabaseOperationException, InvalidInteractionException {
		if (isStartup()) {
			interactionRepo.startupUpdate(State.DECLINED, interactionId, getAccountId());
		} else if (isInvestor()) {
			interactionRepo.startupUpdate(State.DECLINED, interactionId, getAccountId());
		} else {
			throw new InvalidInteractionException("This type of account cannot accept an interaction");
		}
	}

}
