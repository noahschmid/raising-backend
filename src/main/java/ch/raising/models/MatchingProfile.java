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
    private List<Long> investorTypes;
    private List<Long> industries;
    private List<Long> investmentPhases;
    private List<Country> countries;
    private List<Long> continents;
    private List<Long> support;

    public MatchingProfile() {
        investorTypes = new ArrayList<Long>();
        industries = new ArrayList<Long>();
        investmentPhases = new ArrayList<Long>();
        countries = new ArrayList<Country>();
        continents = new ArrayList<Long>();
        support = new ArrayList<Long>();
    }
    
    public void addInvestorType(Long investorType) {
    	investorTypes.add(investorType);
    }
    
    public void addInvestmentPhase(Long investmentPhase) {
    	investmentPhases.add(investmentPhase);
    }
    
    public void addCountry(Country country) {
    	countries.add(country);
    }

    public void addContinent(Long continent) {
    	continents.add(continent);
    }
    
    public void addIndustry(Long industry) {
    	industries.add(industry);
    }

    public void addSupport(Long support) {
    	this.support.add(support);
    }
}