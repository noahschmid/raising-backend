package ch.raising.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MatchingProfile {
    private boolean isStartup;
    private String name;
    private String description;
    private Long accountId = -1L;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private List<AssignmentTableModel> investorTypes;
    private List<AssignmentTableModel> industries;
    private List<AssignmentTableModel> investmentPhases;
    private List<AssignmentTableModel> countries;
    private List<AssignmentTableModel> continents;
    private List<AssignmentTableModel> support;

    public MatchingProfile() {
        investorTypes = new ArrayList<AssignmentTableModel>();
        industries = new ArrayList<AssignmentTableModel>();
        investmentPhases = new ArrayList<AssignmentTableModel>();
        countries = new ArrayList<AssignmentTableModel>();
        continents = new ArrayList<AssignmentTableModel>();
        support = new ArrayList<AssignmentTableModel>();
    }
    
    public void addInvestorType(AssignmentTableModel invType) {
    	investorTypes.add(invType);
    }
    
    public void addInvestmentPhase(AssignmentTableModel invType) {
    	investmentPhases.add(invType);
    }
    
    public void addCountry(AssignmentTableModel country) {
    	countries.add(country);
    }
    
    public void addIndustry(AssignmentTableModel industry) {
    	industries.add(industry);
    }
}