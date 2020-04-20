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
import ch.raising.data.InvestorRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.data.SharedDataRepository;
import ch.raising.data.StartupRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Interaction;
import ch.raising.models.InteractionRequest;
import ch.raising.models.InteractionTypes;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.models.SharedData;
import ch.raising.models.State;
import ch.raising.models.responses.MatchResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;

@Service
public class InteractionService {

	private final InteractionRepository interactionRepo;
	private final RelationshipRepository relationshipRepo;
	private final AccountRepository accountRepo;
	private final SharedDataRepository shareRepo;
	private final InvestorRepository invRepo;
	private final StartupRepository suRepo;

	private static final Logger LOGGER = LoggerFactory.getLogger(InteractionService.class);

	@Autowired
	public InteractionService(InteractionRepository interactionRepo, RelationshipRepository relationshipRepo,
			AccountRepository accountRepo, SharedDataRepository shareRepo, InvestorRepository invRepo, StartupRepository suRepo) {
		this.interactionRepo = interactionRepo;
		this.relationshipRepo = relationshipRepo;
		this.accountRepo = accountRepo;
		this.shareRepo = shareRepo;
		this.invRepo = invRepo;
		this.suRepo = suRepo;
	}
	public List<MatchResponse> getAllByAccountId()
			throws EmptyResultDataAccessException, DataAccessException, SQLException, InvalidInteractionException {
		long accountId = getAccountId();
		List<Relationship> relationships = getRelationships(accountId);
		List<MatchResponse> matchResponses = populateWithInteraction(relationships, accountId);
		populateWithAccountInfo(matchResponses);
		return matchResponses;
	}

	private void populateWithAccountInfo(List<MatchResponse> matchResponses) throws InvalidInteractionException {
		for(MatchResponse m: matchResponses) {
			MatchResponse userData = accountRepo.getDataForMatchResponse(m.getAccountId());
			m.setFirstName(userData.getFirstName());
			m.setLastName(userData.getLastName());
			m.setCompanyName(userData.getCompanyName());
			m.setProfilePictureId(userData.getProfilePictureId());
			if(isStartup()) {
				m.setInvestorTypeId(invRepo.getInvestorType(m.getAccountId()));
			}else if (isInvestor()) {
				m.setInvestmentPhaseId(suRepo.getInvestmentPhase(m.getAccountId()));
			}else {
				throw new InvalidInteractionException("cannot map matches to an account that is neither a startup nor an investor");
			}
		}
		
	}
	private List<MatchResponse> populateWithInteraction(List<Relationship> relationships,long accountId) throws SQLException, InvalidInteractionException {
		List<Interaction> interactions = interactionRepo.findAllByAccountId(accountId);
		List<MatchResponse> responses = new ArrayList<MatchResponse>();
		for(Relationship r: relationships) {
			List<Interaction> interactionsOfR = new ArrayList<Interaction>();
			for(Interaction i: interactions) {
				if(i.getStartupId() == r.getStartupId() && i.getInvestorId() == r.getInvestorId()) {
					SharedData shared = null;
					try {
						shared = getSharedDataAndDelete(i.getId());
						i.setData(shared);
					}catch (EmptyResultDataAccessException e) {
						i.setData(shared);
					}
					i.setInvestorId(0); //because this should be changed to relationshipid in the database
					i.setStartupId(0); //same here
					interactionsOfR.add(i);
				}
			}
			for(Interaction i: interactionsOfR) {
				interactions.remove(i);
			}
			MatchResponse matchResp = new MatchResponse();
			matchResp.setId(r.getId());
			matchResp.setState(r.getState());
			matchResp.setInteractions(interactionsOfR);
			matchResp.setMatchingPercent(MatchingService.getMatchingPercent(r.getMatchingScore()));
			if(isStartup()) {
				matchResp.setAccountId(r.getInvestorId());
			}else if(isInvestor()) {
				matchResp.setAccountId(r.getStartupId());
			}else {
				throw new InvalidInteractionException("cannot map matches to an account that is neither a startup nor an investor");
			}
			responses.add(matchResp);
		}
		return responses;
	}
	private List<Relationship> getRelationships(long accountId) throws EmptyResultDataAccessException, SQLException {
		List<RelationshipState> allExceptMatch = new ArrayList<RelationshipState>(Arrays.asList(RelationshipState.values()));
		List<Relationship> all = new ArrayList<Relationship>();
		allExceptMatch.remove(RelationshipState.MATCH);
		for(RelationshipState s: allExceptMatch) {
			all.addAll(relationshipRepo.getByAccountIdAndState(accountId, s));
		}
		
		return all;
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
		
		if (data.getAccountId() == -1 || data.getAccountId() == 0)
			throw new InvalidInteractionException("add data.accountId");
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
			SharedData retreived = null;
			try {
				retreived = shareRepo.findByInteractionIdAndAccountId(interactionId, accountId);
			}catch (Exception e) {
				if(retreived != null)
					shareRepo.addSharedData(retreived);
				throw e;
			}
			shareRepo.deleteByInteractionIdAndAccountId(interactionId, accountId);
			return retreived;
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
