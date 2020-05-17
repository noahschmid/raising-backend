package ch.raising.services;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.data.StartupRepository;
import ch.raising.models.Country;
import ch.raising.models.Investor;
import ch.raising.models.MatchingProfile;
import ch.raising.models.Relationship;
import ch.raising.models.Startup;
import ch.raising.models.enums.RelationshipState;
import ch.raising.models.responses.AdminMatchResponse;
import ch.raising.models.responses.MatchResponse;

@Service
public class MatchingService {

	private InvestorRepository investorRepository;

	private InvestorService investorService;

	private StartupRepository startupRepository;

	private StartupService startupService;

	private RelationshipRepository relationshipRepository;

	private SettingService settingService;

    private final NotificationService notificationService;
    
    private InteractionService interactionService;

	private final static int MAX_SCORE = 6;

	private final static int MAX_WEEKLY_MATCHES_COUNT = 5;

	private SubscriptionService subscriptionService;

	@Autowired
	public MatchingService(RelationshipRepository relationshipRepository, StartupService startupService,
			StartupRepository startupRepository, InvestorService investorService, InvestorRepository investorRepository,
			AccountRepository accountRepository, SettingService settingService, InteractionService interactionService,
			NotificationService notificationService, SubscriptionService subscriptionService) {
		this.startupService = startupService;
		this.relationshipRepository = relationshipRepository;
		this.investorService = investorService;
		this.investorRepository = investorRepository;
		this.startupRepository = startupRepository;
		this.settingService = settingService;
        this.notificationService = notificationService;
		this.interactionService = interactionService;
		this.subscriptionService = subscriptionService;
	}

	/**
	 * Loop through all profiles and save matches inside relationship table
	 * 
	 * @param id        the account id of the profile to be matched with
	 * @param isStartup indicates whether the given profile is a startup
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws Exception           throws Exception if there was a problem writing
	 *                             to relationship table
	 */
	public void match(long id, boolean isStartup) throws DataAccessException, SQLException {
		List<MatchingProfile> objects;
		MatchingProfile subject;
		if (isStartup) {
			objects = investorService.getAllMatchingProfiles();
			Startup startup = startupRepository.find(id);
			subject = startupService.getMatchingProfile(startup);
		} else {
			objects = startupService.getAllMatchingProfiles();
			Investor investor = investorRepository.find(id);
			subject = investorService.getMatchingProfile(investor);
		}

		if (subject == null)
			throw new Error("account could not be found");

		for (MatchingProfile object : objects) {
			int score = getMatchingScore(subject, object);

			if (score > 0) {
				Relationship relationship = new Relationship();
				if (isStartup) {
					relationship.setInvestorId(object.getAccountId());
					relationship.setStartupId(subject.getAccountId());
				} else {
					relationship.setStartupId(object.getAccountId());
					relationship.setInvestorId(subject.getAccountId());
				}

				relationship.setMatchingScore(score);
				relationship.setState(RelationshipState.MATCH);

				System.out.println("Match found: " + relationship.getInvestorId() + " <--> "
						+ relationship.getStartupId() + " state: " + relationship.getState());

				if (relationshipRepository.exists(relationship))
					relationshipRepository.updateScore(relationship);
				else {
					relationshipRepository.add(relationship);
				}
			}
		}
	}

	/**
	 * Loop through matching criteria and return matching score of two profiles
	 * 
	 * @param subject the first matching profile to be matched with
	 * @param object  the second matching profile to be matched with
	 * @return matching score
	 */
	public static int getMatchingScore(MatchingProfile subject, MatchingProfile object) {
		int score = 0;
		boolean found = false;

		if (subject.getInvestmentMin() != -1 && subject.getInvestmentMax() != -1 && object.getInvestmentMax() != -1
				&& object.getInvestmentMin() != -1) {
			if (subject.getInvestmentMax() >= object.getInvestmentMin()
					&& subject.getInvestmentMin() <= object.getInvestmentMax()
					&& object.getInvestmentMax() >= subject.getInvestmentMin()
					&& object.getInvestmentMin() <= subject.getInvestmentMax())
				++score;
		}

		boolean marketsMatch = false;

		continentLoop: for (long contnt : object.getContinents()) {
			if (subject.getContinents().contains(contnt)) {
				++score;
				marketsMatch = true;
				break;
			}

			for (Country cntry : subject.getCountries()) {
				if (cntry.getContinentId() == contnt) {
					++score;
					break continentLoop;
				}
			}
		}

		if (!marketsMatch) {
			countryLoop: for (Country cntry : object.getCountries()) {
				if (subject.getCountries().contains(cntry)) {
					++score;
					break;
				}
				for (long contnt : subject.getContinents()) {
					if (contnt == cntry.getContinentId()) {
						++score;
						break countryLoop;
					}
				}
			}
		}

		for (Long phase : object.getInvestmentPhases()) {
			if (subject.getInvestmentPhases().contains(phase)) {
				++score;
				break;
			}
		}

		for (Long type : object.getInvestorTypes()) {
			if (subject.getInvestorTypes().contains(type)) {
				++score;
				break;
			}
		}

		for (Long industry : object.getIndustries()) {
			if (subject.getIndustries().contains(industry)) {
				++score;
				break;
			}
		}

		for (Long support : object.getSupport()) {
			if (subject.getSupport().contains(support)) {
				++score;
				break;
			}
		}

		return score;
	}

	/**
	 * Accept match
	 * 
	 * @param id id of the match to decline
	 */
	public void accept(long id, boolean isStartup) throws Exception {
		Relationship relationship = relationshipRepository.find(id);

		if (isStartup && relationship.getStartupDecidedAt() == null)
			relationship.setStartupDecidedAt(new Timestamp(new Date().getTime()));

		if (!isStartup && relationship.getInvestorDecidedAt() == null)
			relationship.setInvestorDecidedAt(new Timestamp(new Date().getTime()));

		switch (relationship.getState()) {
		case MATCH:
			if (isStartup) {
				relationship.setState(RelationshipState.STARTUP_ACCEPTED);
				notificationService.sendRequestMatch(relationship.getStartupId(), relationship.getInvestorId(), id);
			} else {
				relationship.setState(RelationshipState.INVESTOR_ACCEPTED);
				notificationService.sendRequestMatch(relationship.getInvestorId(), relationship.getStartupId(), id);
			}

			break;

		case INVESTOR_ACCEPTED:
			if (isStartup) {
				relationship.setState(RelationshipState.HANDSHAKE);
				notificationService.sendMatchRequestAccept(relationship.getStartupId(), relationship.getInvestorId(), id);
			}
			break;

		case STARTUP_ACCEPTED:
			if (!isStartup) {
				relationship.setState(RelationshipState.HANDSHAKE);
				notificationService.sendMatchRequestAccept(relationship.getInvestorId(), relationship.getStartupId(), id);
			}
			break;

		case STARTUP_DECLINED:
			if (isStartup)
				relationship.setState(RelationshipState.STARTUP_ACCEPTED);
			break;

		case INVESTOR_DECLINED:
			if (!isStartup)
				relationship.setState(RelationshipState.INVESTOR_ACCEPTED);
			break;

		default:
			break;
		}

		relationshipRepository.update(relationship);

	}

	/**
	 * Decline match
	 * 
	 * @param id id of the match to decline
	 */
	public void decline(long id, boolean isStartup) throws Exception {
		Relationship relationship = relationshipRepository.find(id);
		if (isStartup) {
			if (relationship.getStartupDecidedAt() == null) {
				relationship.setStartupDecidedAt(new Timestamp(new Date().getTime()));
			}
			relationship.setState(RelationshipState.STARTUP_DECLINED);
			relationshipRepository.update(relationship);
		} else {
			if (relationship.getInvestorDecidedAt() == null) {
				relationship.setInvestorDecidedAt(new Timestamp(new Date().getTime()));
			}
			relationship.setState(RelationshipState.INVESTOR_DECLINED);
			relationshipRepository.update(relationship);
		}
	}

	/**
	 * Get matches of an account
	 * 
	 * @throws SQLException
	 * @throws EmptyResultDataAccessException
	 */
	public List<MatchResponse> getMatches(long accountId, boolean isStartup)
			throws EmptyResultDataAccessException, SQLException {
		List<Relationship> matches = relationshipRepository.getByAccountIdAndState(accountId, RelationshipState.MATCH);
		List<MatchResponse> matchResponses = new ArrayList<>();
		int matchesPreference = MAX_WEEKLY_MATCHES_COUNT;
		try {
			matchesPreference = settingService.getSettings().getNumberOfMatches();
		} catch (Exception e) {
			System.out.println("getMatches: couldn't find settings for account " + accountId);
		}
		int matchesCount = 0;
		int weeklyMatchesCount = Math.min(MAX_WEEKLY_MATCHES_COUNT, matchesPreference);

		// last "release date" of matches
		Date matchDay = Date.from(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.atStartOfDay(ZoneId.systemDefault()).toInstant());

		matchDay = getZeroTimeDate(matchDay);

		System.out.println("Last matchday: " + matchDay.toString());

		for (Relationship match : matches) {
			//boolean startupSubscribed = subscriptionService.isSubscribed(match.getStartupId());
			//boolean investorSubscribed = subscriptionService.isSubscribed(match.getInvestorId());

		/*	if(!isStartup && !startupSubscribed || isStartup && !investorSubscribed) {
				break;
			}*/

			if (match.getStartupDecidedAt() != null) {
				if (getZeroTimeDate(match.getStartupDecidedAt()).before(matchDay) && isStartup) {
					continue;
				}
			}
			if (match.getInvestorDecidedAt() != null) {
				if (getZeroTimeDate(match.getInvestorDecidedAt()).before(matchDay) && !isStartup) {
					continue;
				}
			}

			if (match.getState() != RelationshipState.MATCH) {
				++matchesCount;

				/*
				 * if(matchesCount < weeklyMatchesCount) continue; else break;
				 */
			}

			MatchResponse response = new MatchResponse();
			response.setMatchingPercent(getMatchingPercent(match.getMatchingScore()));
			response.setId(match.getId());

			if (isStartup) {
				response.setStartup(false);

				response.setAccountId(match.getInvestorId());
				Investor investor;
				try {
					investor = investorService.getAccount(match.getInvestorId());
				} catch (Exception e) {
					investor = new Investor();
				}
				response.setInvestorTypeId(investor.getInvestorTypeId());
				response.setDescription(investor.getDescription());
				response.setFirstName(investor.getFirstName());
				response.setLastName(investor.getLastName());
				response.setProfilePictureId(investor.getProfilePictureId());
				response.setAccountLastChanged(investor.getLastChanged());

			} else {
				response.setStartup(true);
				response.setAccountId(match.getStartupId());
				Startup startup;
				try {
					startup = (Startup) startupService.getAccount(match.getStartupId());
				} catch (Exception e) {
					startup = new Startup();
				}
				response.setInvestmentPhaseId(startup.getInvestmentPhaseId());
				response.setDescription(startup.getDescription());
				response.setCompanyName(startup.getCompanyName());
				response.setProfilePictureId(startup.getProfilePictureId());
				response.setAccountLastChanged(startup.getLastChanged());
			}
			matchResponses.add(response);
			++matchesCount;
			
			if(matchesCount == weeklyMatchesCount) { break; }
			 
		}
		return matchResponses;
	}

	/**
	 * Set time value to 0 for a Date instance
	 */
	private static Date getZeroTimeDate(Date fecha) {
		Date res = fecha;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(fecha);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		res = calendar.getTime();

		return res;
	}

	/**
	 * Get percentage out of integer score
	 */
	public static int getMatchingPercent(int score) {
		float percent = score;
		percent /= MAX_SCORE;
		percent *= 10;
		return (int) ((Math.round(percent * 2) / 2.0) * 10);
	}

	/**
	 * Get all matches
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Relationship> getAllMatches() throws Exception {
		List<Relationship> matches = relationshipRepository.getByState(RelationshipState.MATCH);
		return matches;
    }
    
    /**
	 * Get all relationships
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<AdminMatchResponse> getAllRelationships() throws Exception {
        List<Relationship> matches = relationshipRepository.getAll();
        List<AdminMatchResponse> matchResponses = new ArrayList<>();

        for (Relationship match : matches) {
			AdminMatchResponse response = new AdminMatchResponse();
			response.setMatchingPercent(getMatchingPercent(match.getMatchingScore()));
            response.setId(match.getId());
            response.setLastchanged(match.getLastchanged());
            response.setState(match.getState());
            response.setInteractions(interactionService.getInteractionsByInvestorAndStartup(
                match.getInvestorId(), match.getStartupId()));

            Investor investor;
            try {
                investor = investorService.getAccount(match.getInvestorId());
            } catch (Exception e) {
                investor = new Investor();
            }
            response.setInvestor(investor);
        
            Startup startup;
            try {
                startup = (Startup) startupService.getAccount(match.getStartupId());
            } catch (Exception e) {
                startup = new Startup();
            }

			response.setStartup(startup);
			matchResponses.add(response);
		}
		return matchResponses;
	}

	/**
     * Get all handshakes
     * @return
     * @throws Exception
     */
    public List<Relationship> getAllHandshakes() throws Exception {
        List<Relationship> matches = relationshipRepository.getByState(RelationshipState.HANDSHAKE);
        return matches;
    }
}