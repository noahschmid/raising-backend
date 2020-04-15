package ch.raising.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ch.raising.data.InvestorRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.data.StartupRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Investor;
import ch.raising.models.MatchingProfile;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.models.Startup;
import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;

@Service
public class MatchingService {

    
    private InvestorRepository investorRepository;
    
    private InvestorService investorService;

    private StartupRepository startupRepository;
    
    private StartupService startupService;
    
    private RelationshipRepository relationshipRepository;

    @Autowired
    public MatchingService(RelationshipRepository relationshipRepository,
        StartupService startupService, StartupRepository startupRepository,
        InvestorService investorService, InvestorRepository investorRepository) {
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
            objects = startupService.getAllMatchingProfiles();
            Startup startup = startupRepository.find(id);
            subject = startupService.getMatchingProfile(startup);
        }
        else {
            objects = investorService.getAllMatchingProfiles();
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
                        relationshipRepository.update(relationship);
                    else {
                        relationship.setState(null); //making sure state doesn't get overwritten
                        relationshipRepository.add(relationship);
                        System.out.println("relationship already exists");
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

        if(subject.getInvestmentMin() != -1 && subject.getInvestmentMax() != -1 && 
            object.getInvestmentMax() != -1 && object.getInvestmentMin() != -1) {
            if(subject.getInvestmentMax() >= object.getInvestmentMin() &&  
                subject.getInvestmentMin() <= object.getInvestmentMax() &&
                object.getInvestmentMax() >= subject.getInvestmentMin() &&  
                object.getInvestmentMin() <= subject.getInvestmentMax())
                ++score;
        }
        for(AssignmentTableModel cntry : object.getCountries()) {
            if(subject.getCountries().contains(cntry)) {
                ++score;
                break;
            }
        }

        for(AssignmentTableModel phase : object.getInvestmentPhases()) {
            if(subject.getInvestmentPhases().contains(phase)) {
                ++score;
                break;
            }
        }

        for(AssignmentTableModel type : object.getInvestorTypes()) {
            if(subject.getInvestorTypes().contains(type)) {
                ++score;
                break;
            }
        }

        for(AssignmentTableModel industry : object.getIndustries()) {
            if(subject.getIndustries().contains(industry)) {
                ++score;
                break;
            }
        }

        for(AssignmentTableModel support : object.getSupport()) {
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
     */
    public List<Relationship> getMatches(long accountId) {
        List<Relationship> matches = relationshipRepository.getByAccountIdAndState(accountId, 
                                                                    RelationshipState.MATCH);
        return matches;
    }
}