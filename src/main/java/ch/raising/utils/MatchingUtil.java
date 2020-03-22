package ch.raising.utils;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import ch.raising.data.InvestorRepository;
import ch.raising.data.RelationshipRepository;
import ch.raising.data.StartupRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Investor;
import ch.raising.models.MatchingProfile;
import ch.raising.models.Relationship;
import ch.raising.models.Startup;
import ch.raising.services.InvestorService;
import ch.raising.services.StartupService;

@Component
public class MatchingUtil {

    @Autowired
    private static InvestorRepository investorRepository;

    @Autowired
    private static InvestorService investorService;

    @Autowired
    private static StartupRepository startupRepository;

    @Autowired
    private static StartupService startupService;

    @Autowired
    private static RelationshipRepository relationshipRepository;

    /**
     * Loop through all profiles and save matches inside relationship table
     * @param id the account id of the profile to be matched with
     * @param isStartup indicates whether the given profile is a startup
     * @throws Exception throws Exception if there was a problem writing to relationship table
     */
    public static void match(long id, boolean isStartup) throws Exception {
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
                relationship.setState("MATCH");
                try {
                    relationshipRepository.add(relationship);
                } catch (DataIntegrityViolationException  e) {
                    System.out.println("relationship already exists");
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
}