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
import ch.raising.models.InteractionRequest;
import ch.raising.models.InteractionTypes;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.models.Share;
import ch.raising.models.State;
import ch.raising.models.responses.CompleteInteractions;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;

@Service
public class InteractionService {

	private final InteractionRepository interactionRepo;
	private final RelationshipRepository relationshipRepo;
	private final AccountRepository accountRepo;
	private final SharedDataRepository shareRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(InteractionService.class);

	@Autowired
	public InteractionService(InteractionRepository interactionRepo, RelationshipRepository relationshipRepo,
			AccountRepository accountRepo, SharedDataRepository shareRepo) {
		this.interactionRepo = interactionRepo;
		this.relationshipRepo = relationshipRepo;
		this.accountRepo = accountRepo;
		this.shareRepo = shareRepo;
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
		RelationshipState[] allExceptMatch = new RelationshipState[all.length - 1];
		int c = 0;
		for (int i = 0; i < all.length; i++) {
			if (all[i] != RelationshipState.MATCH) {
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
		LOGGER.info("Interaction: (otherId: {}, interaction: {})", interaction.getId(),
				interaction.getInteraction().name());
		validateSharedData(interaction.getInteraction(), interaction.getData());
		long dataId = shareRepo.addSharedData(interaction.getData());
		try {
			Interaction insert = constructInteraction(interaction);
			interactionRepo.addInteraction(insert);
		} catch (Exception e) {
			shareRepo.deleteById(dataId);
			throw e;
		}

	}

	private void validateSharedData(InteractionTypes state, Share data) throws InvalidInteractionException {

		if (data.getFirstName() == "" || data.getFirstName() == null)
			throw new InvalidInteractionException("add data.firstName");
		if (data.getLastName() == "" || data.getLastName() == null)
			throw new InvalidInteractionException("add data.lastName");
		if (data.getEmail() == "" || data.getEmail() == null)
			throw new InvalidInteractionException("add data.email");

		switch (state) {
		case BUSINESSPLAN:
			if (data.getBusinessPlanId() == -1 || data.getBusinessPlanId() == 0)
				throw new InvalidInteractionException("add data.businessplanId");
		case PHONE_CALL:
			if (data.getPhone() == -1 || data.getPhone() == 0)
				throw new InvalidInteractionException("add data.phone");
		default:
			break;
		}
	}

	private Interaction constructInteraction(InteractionRequest interaction)
			throws InvalidInteractionException, SQLException {
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
							+ isStartup() + " ,investor: " + isInvestor() + ") " + " investor: "
							+ accountRepo.isInvestor(interaction.getId()));
		}
		insert.setInteraction(interaction.getInteraction());
		return insert;
	}

	public Share acceptInteraction(long interactionId)
			throws InvalidInteractionException, DataAccessException, DatabaseOperationException, SQLException {
		if (isStartup()) {
			interactionRepo.startupUpdate(State.ACCEPTED, interactionId, getAccountId());
		} else if (isInvestor()) {
			interactionRepo.investorUpdate(State.ACCEPTED, interactionId, getAccountId());
		} else {
			throw new InvalidInteractionException("This type of account cannot accept an interaction");
		}
		
		return getSharedDataAndDelete(interactionId);
	}

	private Share getSharedDataAndDelete(long interactionId) throws EmptyResultDataAccessException, SQLException {
		long accountId = getAccountId();
		Interaction updated = interactionRepo.findByAccountIdAndId(interactionId, accountId);

		if (updated.getInvestorState() == State.ACCEPTED && updated.getStartupState() == State.ACCEPTED) {
			return shareRepo.findByIdAndDelete(interactionId, accountId);
		}
		return null;
	}

	public void rejectInteraction(long interactionId)
			throws DataAccessException, DatabaseOperationException, InvalidInteractionException, SQLException {
		long accountId = getAccountId();
		if (isStartup()) {
			interactionRepo.startupUpdate(State.REJECT, interactionId, accountId);
			shareRepo.deleteByInteractionIdAndAccountId(interactionId, accountId);
		} else if (isInvestor()) {
			interactionRepo.investorUpdate(State.REJECT, interactionId, accountId);
			shareRepo.deleteByInteractionIdAndAccountId(interactionId, accountId);
		} else {
			throw new InvalidInteractionException("This type of account cannot reject an interaction");
		}
	}

}
