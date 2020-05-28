package ch.raising.services;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import ch.raising.models.Relationship;
import ch.raising.models.SharedData;
import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.RelationshipState;
import ch.raising.models.enums.State;
import ch.raising.models.responses.MatchResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.InvalidInteractionException;

/**
 * service for handling all the interactions
 * 
 * @author manus
 *
 */
@Service
public class InteractionService {
	private final InteractionRepository interactionRepo;
	private final RelationshipRepository relationshipRepo;
	private final AccountRepository accountRepo;
	private final SharedDataRepository shareRepo;
	private final InvestorRepository invRepo;
	private final StartupRepository suRepo;
	private final NotificationService notificationService;

	@Autowired
	public InteractionService(InteractionRepository interactionRepo, RelationshipRepository relationshipRepo,
			AccountRepository accountRepo, SharedDataRepository shareRepo, InvestorRepository invRepo,
			StartupRepository suRepo, NotificationService notifiactionService) {
		this.interactionRepo = interactionRepo;
		this.relationshipRepo = relationshipRepo;
		this.accountRepo = accountRepo;
		this.shareRepo = shareRepo;
		this.invRepo = invRepo;
		this.suRepo = suRepo;
		this.notificationService = notifiactionService;
	}

	/**
	 * 
	 * @return a list of {@link MatchResonse} objects of a certain account
	 * @throws EmptyResultDataAccessException
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws InvalidInteractionException
	 */
	public List<MatchResponse> getAllByAccountId()
			throws EmptyResultDataAccessException, DataAccessException, SQLException, InvalidInteractionException {
		long accountId = getAccountId();
		List<Relationship> relationships = getRelationships(accountId);
		List<MatchResponse> matchResponses = populateWithInteraction(relationships);
		populateWithAccountInfo(matchResponses);
		return matchResponses;
	}

	/**
	 * 
	 * @param investorId
	 * @param startupId
	 * @return a list of {@link Interaction} objects of an investor and a startup
	 */
	public List<Interaction> getInteractionsByInvestorAndStartup(long investorId, long startupId) {
		List<Interaction> response = new ArrayList<Interaction>();
		response.addAll(interactionRepo.findByInvestorAndStartup(investorId, startupId));
		return response;
	}

	private void populateWithAccountInfo(List<MatchResponse> matchResponses) throws InvalidInteractionException {
		for (MatchResponse m : matchResponses) {
			MatchResponse userData = accountRepo.getDataForMatchResponse(m.getAccountId());
			m.setFirstName(userData.getFirstName());
			m.setLastName(userData.getLastName());
			m.setCompanyName(userData.getCompanyName());
			m.setProfilePictureId(userData.getProfilePictureId());
			m.setAccountLastChanged(userData.getAccountLastChanged());
			if (isStartup()) {
				m.setInvestorTypeId(invRepo.getInvestorType(m.getAccountId()));
				m.setInvestmentPhaseId(-1);
				m.setStartup(false);
			} else if (isInvestor()) {
				m.setInvestmentPhaseId(suRepo.getInvestmentPhase(m.getAccountId()));
				m.setInvestorTypeId(-1);
				m.setStartup(true);
			} else {
				throw new InvalidInteractionException(
						"cannot map matches to an account that is neither a startup nor an investor");
			}
		}

	}

	private List<MatchResponse> populateWithInteraction(List<Relationship> relationship)
			throws SQLException, InvalidInteractionException {
		List<MatchResponse> responses = new ArrayList<MatchResponse>();
		for (Relationship rId : relationship) {
			List<Interaction> interactions = interactionRepo.findByRelationshipId(rId.getId());
			for (Interaction interaction : interactions) {
				SharedData shared = getSharedDataAndDelete(interaction.getId());
				interaction.setData(shared);
			}
			MatchResponse matchResp = new MatchResponse();
			matchResp.setId(rId.getId());
			matchResp.setState(rId.getState());
			matchResp.setInteractions(interactions);
			matchResp.setMatchingPercent(MatchingService.getMatchingPercent(rId.getMatchingScore()));
			matchResp.setLastchanged(rId.getLastchanged());
			if (isStartup()) {
				matchResp.setAccountId(rId.getInvestorId());
			} else if (isInvestor()) {
				matchResp.setAccountId(rId.getStartupId());
			}
			responses.add(matchResp);
		}
		return responses;
	}

	private List<Relationship> getRelationships(long accountId) throws EmptyResultDataAccessException, SQLException {
		List<RelationshipState> allExceptMatch = new ArrayList<RelationshipState>(
				Arrays.asList(RelationshipState.values()));
		List<Relationship> all = new ArrayList<Relationship>();
		allExceptMatch.remove(RelationshipState.MATCH);
		for (RelationshipState s : allExceptMatch) {
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

	/**
	 * adds an interaction
	 * 
	 * @param interaction {@link InteractionRequest} to be newly inserted
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws InvalidInteractionException
	 */
	public void addInteraction(InteractionRequest interaction)
			throws DataAccessException, SQLException, InvalidInteractionException {
		Interaction insert = constructInteraction(interaction);
		long interactionId = interactionRepo.addInteraction(insert);

		try {
			SharedData data = interaction.getData();
			data.setInteractionId(interactionId);
			validateSharedData(interaction.getInteraction(), interaction.getData());
			shareRepo.addSharedData(interaction.getData());
		} catch (Exception e) {
			interactionRepo.deleteByInteractionId(interactionId);
			throw e;
		}
		LoggerFactory.getLogger(this.getClass().getName()).info(interaction.toString());
		notificationService.sendLeadNotification(getAccountId(), interaction.getData().getAccountId(),
				interaction.getInteraction(), interaction.getRelationshipId());
	}

	private void validateSharedData(InteractionType state, SharedData data) throws InvalidInteractionException {
		if (data == null)
			throw new InvalidInteractionException("send the required data object: " + new SharedData());
		if (data.getAccountId() == -1 || data.getAccountId() == 0)
			throw new InvalidInteractionException("add data.accountId");
		if (data.getEmail() == "" || data.getEmail() == null)
			throw new InvalidInteractionException("add data.email");

		if (state == InteractionType.PHONE_CALL) {
			if (data.getPhone() == null || data.getPhone() == "")
				throw new InvalidInteractionException("add data.phone");
		}
	}

	private Interaction constructInteraction(InteractionRequest interaction)
			throws InvalidInteractionException, SQLException {
		Interaction insert = new Interaction();
		long requesteeId = getAccountId();
		if (interaction.getInteraction() == null)
			throw new InvalidInteractionException("no interaction set");

		if (isStartup() && accountRepo.isInvestor(interaction.getData().getAccountId())) {
			insert.setStartupId(requesteeId);
			insert.setInvestorId(interaction.getData().getAccountId());
			insert.setStartupState(State.ACCEPTED);
			insert.setInvestorState(State.OPEN);
		} else if (isInvestor() && accountRepo.isStartup(interaction.getData().getAccountId())) {
			insert.setInvestorId(requesteeId);
			insert.setStartupId(interaction.getData().getAccountId());
			insert.setInvestorState(State.ACCEPTED);
			insert.setStartupState(State.OPEN);
		} else {
			throw new InvalidInteractionException(
					"This type of account cannot have a relationship. One party has to be a startup and the other has to be an investor. token: (startup: "
							+ isStartup() + " ,investor: " + isInvestor() + ") " + " investor: "
							+ accountRepo.isInvestor(interaction.getData().getAccountId()));
		}
		insert.setInteraction(interaction.getInteraction());
		insert.setRelationshipId(interaction.getRelationshipId());
		return insert;
	}

	/**
	 * updates the state of an interaction
	 * 
	 * @param interactionId to be accepted
	 * @param accept        {@link InteractionRequest} that holds the data for the
	 *                      interaction to be accepted
	 * @return the {@link SharedData} of the partner that already accepted the
	 *         request
	 * @throws InvalidInteractionException
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 * @throws SQLException
	 */
	public SharedData acceptInteraction(long interactionId, InteractionRequest accept)
			throws InvalidInteractionException, DataAccessException, DatabaseOperationException, SQLException {

		SharedData data = accept.getData();
		data.setInteractionId(interactionId);
		validateSharedData(accept.getInteraction(), data);
		shareRepo.addSharedData(data);
		try {
			if (isStartup()) {
				interactionRepo.startupUpdate(State.ACCEPTED, interactionId, getAccountId());
			} else if (isInvestor()) {
				interactionRepo.investorUpdate(State.ACCEPTED, interactionId, getAccountId());
			} else {
				throw new InvalidInteractionException("This type of account cannot accept an interaction");
			}
		} catch (Exception e) {
			shareRepo.deleteByInteractionIdAndAccountId(interactionId, data.getAccountId());
			throw e;
		}
		notificationService.sendConnectionNotification(getAccountId(), accept.getData().getAccountId(),
				accept.getInteraction(), accept.getRelationshipId());
		return getSharedDataAndDelete(interactionId);
	}

	private SharedData getSharedDataAndDelete(long interactionId) throws EmptyResultDataAccessException, SQLException {
		long accountId = getAccountId();
		Interaction updated = interactionRepo.findByIdAndAccountId(interactionId, accountId);

		if (updated.getInvestorState() == State.ACCEPTED && updated.getStartupState() == State.ACCEPTED) {
			SharedData retreived = null;
			try {
				retreived = shareRepo.findByInteractionIdAndAccountId(interactionId, accountId);
			} catch (Exception e) {
				if (retreived != null)
					shareRepo.addSharedData(retreived);
			}
			shareRepo.deleteByInteractionIdAndAccountId(interactionId, accountId);
			return retreived;
		}
		return null;
	}

	/**
	 * The declinging will delete any data that is saved by any party and also the
	 * state will be updated.
	 * 
	 * @param interactionId of the interaction to be declined
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 * @throws InvalidInteractionException
	 * @throws SQLException
	 */
	public void declineInteraction(long interactionId)
			throws DataAccessException, DatabaseOperationException, InvalidInteractionException, SQLException {
		long accountId = getAccountId();
		if (isStartup()) {
			interactionRepo.startupUpdate(State.DECLINED, interactionId, accountId);
			shareRepo.deleteByInteractionId(interactionId);
		} else if (isInvestor()) {
			interactionRepo.investorUpdate(State.DECLINED, interactionId, accountId);
			shareRepo.deleteByInteractionId(interactionId);
		} else {
			throw new InvalidInteractionException("This type of account cannot decline an interaction");
		}
	}

	/**
	 * if a request was mistakingly made the interaction can be reopened
	 * 
	 * @param interactionId of the interaction to be reopened
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 * @throws InvalidInteractionException
	 */
	public void reopenInteraction(long interactionId)
			throws DataAccessException, SQLException, DatabaseOperationException, InvalidInteractionException {
		long accountId = getAccountId();
		if (isStartup()) {
			interactionRepo.startupUpdate(State.OPEN, interactionId, accountId);
			shareRepo.deleteByInteractionId(interactionId);
		} else if (isInvestor()) {
			interactionRepo.investorUpdate(State.OPEN, interactionId, accountId);
			shareRepo.deleteByInteractionId(interactionId);
		} else {
			throw new InvalidInteractionException("This type of account cannot decline an interaction");
		}
	}

}
