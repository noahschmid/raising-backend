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
import ch.raising.data.SharedDataRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Interaction;
import ch.raising.models.InteractionRequest;
import ch.raising.models.InteractionTypes;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.models.SharedData;
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
		List<Interaction> interactions = interactionRepo.findAll(accountId);
		for(Interaction i: interactions) {
			i.setData(shareRepo.findByIdAndDelete(i.getId(), accountId));
		}
		List<RelationshipState> allExceptMatch = getAllExceptMatch();
		List<Relationship> relationship = new ArrayList<Relationship>();
		for (RelationshipState s : allExceptMatch) {
			relationship.addAll(relationshipRepo.getByAccountIdAndState(accountId, s));
		}

		return new CompleteInteractions(interactions, relationship);
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

		LOGGER.info("Interaction: (otherId: {}, interaction: {}), Data: (accountId: {})", interaction.getAccountId(),
				interaction.getInteraction().name(), interaction.getData().getAccountId());
		interaction.getData().setAccountId(interaction.getAccountId());
		Interaction insert = constructInteraction(interaction);
		long interactionId = interactionRepo.addInteraction(insert);
		
		try {
			SharedData data = interaction.getData();
			data.setAccountId(interaction.getAccountId());
			data.setInteractionId(interactionId);
			validateSharedData(interaction.getInteraction(), interaction.getData());
			shareRepo.addSharedData(interaction.getData());
		} catch (Exception e) {
			interactionRepo.deleteByInteractionId(interactionId);
			throw e;
		}

	}

	private void validateSharedData(InteractionTypes state, SharedData data) throws InvalidInteractionException {

		LOGGER.info(
				"Data: id: {}, accountId: {}, firstName: {}, lastName: {}, email: {}, phone: {}, businessPlanId: {}",
				data.getId(), data.getAccountId(), data.getFirstName(), data.getLastName(), data.getEmail(),
				data.getPhone(), data.getBusinessPlanId());

		if (data.getAccountId() == -1 || data.getAccountId() == 0)
			throw new InvalidInteractionException("add data.accountId");
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

		if (isStartup() && accountRepo.isInvestor(interaction.getAccountId())) {
			insert.setStartupId(requesteeId);
			insert.setInvestorId(interaction.getAccountId());
			insert.setStartupState(State.ACCEPTED);
			insert.setInvestorState(State.OPEN);
		} else if (isInvestor() && accountRepo.isStartup(interaction.getAccountId())) {
			insert.setInvestorId(requesteeId);
			insert.setStartupId(interaction.getAccountId());
			insert.setInvestorState(State.ACCEPTED);
			insert.setStartupState(State.OPEN);
		} else {
			throw new InvalidInteractionException(
					"This type of account cannot have a relationship. One party has to be a startup and the other has to be an investor. token: (startup: "
							+ isStartup() + " ,investor: " + isInvestor() + ") " + " investor: "
							+ accountRepo.isInvestor(interaction.getAccountId()));
		}
		insert.setInteraction(interaction.getInteraction());
		return insert;
	}

	public SharedData acceptInteraction(InteractionRequest accept)
			throws InvalidInteractionException, DataAccessException, DatabaseOperationException, SQLException {
		long interactionId = accept.getInteractionId();
		SharedData data = accept.getData();
		validateSharedData(accept.getInteraction(), data);
		data.setInteractionId(interactionId);
		data.setAccountId(accept.getAccountId());
		long dataId = shareRepo.addSharedData(data);
		try {
			if (isStartup()) {
				interactionRepo.startupUpdate(State.ACCEPTED, interactionId, getAccountId());
			} else if (isInvestor()) {
				interactionRepo.investorUpdate(State.ACCEPTED, interactionId, getAccountId());
			} else {
				throw new InvalidInteractionException("This type of account cannot accept an interaction");
			}
		}catch(Exception e) {
			shareRepo.deleteById(dataId);
			throw e;
		}
		return getSharedDataAndDelete(interactionId);
	}

	private SharedData getSharedDataAndDelete(long interactionId) throws EmptyResultDataAccessException, SQLException {
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
			shareRepo.deleteByInteractionId(interactionId);
		} else if (isInvestor()) {
			interactionRepo.investorUpdate(State.REJECT, interactionId, accountId);
			shareRepo.deleteByInteractionId(interactionId);
		} else {
			throw new InvalidInteractionException("This type of account cannot reject an interaction");
		}
	}

}
