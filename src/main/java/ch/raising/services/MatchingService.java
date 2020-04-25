package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.data.StartupRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.Investor;
import ch.raising.models.MatchingProfile;
import ch.raising.models.Relationship;
import ch.raising.models.Startup;
import ch.raising.models.enums.RelationshipState;
import ch.raising.models.responses.MatchResponse;
import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;
import ch.raising.utils.DatabaseOperationException;

@Service
public class MatchingService {

    
    private InvestorRepository investorRepository;
    
    private InvestorService investorService;

    private StartupRepository startupRepository;
    
    private StartupService startupService;
    
    private RelationshipRepository relationshipRepository;

    private final static int MAX_SCORE = 6;

    @Autowired
    public MatchingService(RelationshipRepository relationshipRepository,
        StartupService startupService, StartupRepository startupRepository,
        InvestorService investorService, InvestorRepository investorRepository,
        AccountRepository accountRepository) {
        this.startupService = startupService;
        this.relationshipRepository = relationshipRepository;
        this.investorService = investorService;
        this.investorRepository = investorRepository;
        this.startupRepository = startupRepository;
    }

     /**
     * Loop through all profiles and save matches inside relationship table
     * @param id the account id of the profile to be matched with
     * @param isStartup indicates whether the given profile is a startup
     * @throws Exception throws Exception if there was a problem writing to relationship table
     */
    public void match(long id, boolean isStartup) throws Exception {
        System.out.println("Is account startup? -> " + isStartup);
        List<MatchingProfile> objects;
        MatchingProfile subject;
        if(isStartup) {
            objects = investorService.getAllMatchingProfiles();
            Startup startup = startupRepository.find(id);
            subject = startupService.getMatchingProfile(startup);
        }
        else {
            objects = startupService.getAllMatchingProfiles();
            Investor investor = investorRepository.find(id);
            subject = investorService.getMatchingProfile(investor);
        }

        if(subject == null)
            throw new Error("account could not be found");

        for(MatchingProfile object : objects) {
            int score = getMatchingScore(subject, object);

            if(score > 0) {
                Relationship relationship = new Relationship();
                if(isStartup) {
                    relationship.setInvestorId(object.getAccountId());
                    relationship.setStartupId(subject.getAccountId());
                } else {
                    relationship.setStartupId(object.getAccountId());
                    relationship.setInvestorId(subject.getAccountId());
                }

                relationship.setMatchingScore(score);
                relationship.setState(RelationshipState.MATCH);

                System.out.println("Match found: " + relationship.getInvestorId() + 
                " <--> " + relationship.getStartupId() + " state: " + relationship.getState());

                try {
                    if(relationshipRepository.exists(relationship))
                        relationshipRepository.updateScore(relationship);
                    else {
                        relationshipRepository.add(relationship);
                    }
                } catch (DataIntegrityViolationException  e) {
                    System.out.println("Error while adding/updating relationship: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Loop through matching criteria and return matching score of two profiles
     * @param subject the first matching profile to be matched with
     * @param object the second matching profile to be matched with
     * @return matching score
     */
    public static int getMatchingScore(MatchingProfile subject, MatchingProfile object) {
        int score = 0;
        boolean found = false;

        if(subject.getInvestmentMin() != -1 && subject.getInvestmentMax() != -1 && 
            object.getInvestmentMax() != -1 && object.getInvestmentMin() != -1) {
            if(subject.getInvestmentMax() >= object.getInvestmentMin() &&  
                subject.getInvestmentMin() <= object.getInvestmentMax() &&
                object.getInvestmentMax() >= subject.getInvestmentMin() &&  
                object.getInvestmentMin() <= subject.getInvestmentMax())
                ++score;
        }

        boolean marketsMatch = false;

        continentLoop:
        for(long contnt : object.getContinents()) {
            if(subject.getContinents().contains(contnt)) {
                ++score;
                marketsMatch = true;
                break;
            }

            for(Country cntry : subject.getCountries()) {
                if(cntry.getContinentId() == contnt) {
                    ++score;
                    break continentLoop;
                }
            }
        }

        if(!marketsMatch) {
            countryLoop:
            for(Country cntry : object.getCountries()) {
                if(subject.getCountries().contains(cntry)) {
                    ++score;
                    break;
                }
                for(long contnt : subject.getContinents()) {
                    if(contnt == cntry.getContinentId()) {
                        ++score;
                        break countryLoop;
                    }
                }
            }
        }

        for(Long phase : object.getInvestmentPhases()) {
            if(subject.getInvestmentPhases().contains(phase)) {
                ++score;
                break;
            }
        }

        for(Long type : object.getInvestorTypes()) {
            if(subject.getInvestorTypes().contains(type)) {
                ++score;
                break;
            }
        }

        for(Long industry : object.getIndustries()) {
            if(subject.getIndustries().contains(industry)) {
                ++score;
                break;
            }
        }

        for(Long support : object.getSupport()) {
            if(subject.getSupport().contains(support)) {
                ++score;
                break;
            }
        }

        return score;
    }

    /**
     * Accept match
     * @param id id of the match to decline
     */
    public void accept(long id, boolean isStartup) throws Exception {
        Relationship relationship = relationshipRepository.find(id);
        switch(relationship.getState()) {
            case MATCH:
                if(isStartup)
                    relationshipRepository.updateState(id, RelationshipState.STARTUP_ACCEPTED);
                else
                    relationshipRepository.updateState(id, RelationshipState.INVESTOR_ACCEPTED);
            break;
            
            case INVESTOR_ACCEPTED:
                if(isStartup)
                    relationshipRepository.updateState(id, RelationshipState.HANDSHAKE);
            break;

            case STARTUP_ACCEPTED:
                if(!isStartup)
                    relationshipRepository.updateState(id, RelationshipState.HANDSHAKE);
            break;

            case STARTUP_DECLINED: 
                if(isStartup)
                    relationshipRepository.updateState(id, RelationshipState.STARTUP_ACCEPTED);
            break;

            case INVESTOR_DECLINED:
                if(!isStartup)
                    relationshipRepository.updateState(id, RelationshipState.INVESTOR_ACCEPTED);
            break;

            default:
            break;
        }
    }

    /**
     * Decline match
     * @param id id of the match to decline
     */
    public void decline(long id, boolean isStartup) throws Exception {
        if(isStartup)
            relationshipRepository.updateState(id, RelationshipState.STARTUP_DECLINED);
        else
            relationshipRepository.updateState(id, RelationshipState.INVESTOR_DECLINED);
    }

    /**
     * Get matches of an account
     * @throws SQLException 
     * @throws EmptyResultDataAccessException 
     */
    public List<MatchResponse> getMatches(long accountId, boolean isStartup) throws EmptyResultDataAccessException, SQLException {
        List<Relationship> matches = relationshipRepository.getByAccountIdAndState(accountId, 
                                                                    RelationshipState.MATCH);
        List<MatchResponse> matchResponses = new ArrayList<>();
        matches.forEach(match -> {
            MatchResponse response = new MatchResponse();
            response.setMatchingPercent(getMatchingPercent(match.getMatchingScore()));
            response.setId(match.getId());
            
            if(isStartup) {
                response.setAccountId(match.getInvestorId());
                Investor investor;
                try {
                    investor = investorService.getAccount(match.getInvestorId());
                } catch (Exception e) {
                    investor = new Investor();
                }
                response.setInvestorTypeId(investor.getInvestorTypeId());
                response.setStartup(false);
                response.setDescription(investor.getDescription());
                response.setFirstName(investor.getFirstName());
                response.setLastName(investor.getLastName());
                response.setProfilePictureId(investor.getProfilePictureId());
            } else {
                response.setAccountId(match.getStartupId());
                Startup startup;
                try {
                    startup = (Startup)startupService.getAccount(match.getStartupId());
                } catch(Exception e) {
                    startup = new Startup();
                }
                response.setInvestmentPhaseId(startup.getInvestmentPhaseId());
                response.setStartup(true);
                response.setDescription(startup.getDescription());
                response.setCompanyName(startup.getCompanyName());
                response.setProfilePictureId(startup.getProfilePictureId());
            }
            matchResponses.add(response);
        });
        return matchResponses;
    }

    /**
     * Get percentage out of integer score
     */
    public static int getMatchingPercent(int score) {
        float percent = score;
        percent /= MAX_SCORE;
        percent *= 10;
        return (int)((Math.round(percent * 2) / 2.0) * 10);
    }
}