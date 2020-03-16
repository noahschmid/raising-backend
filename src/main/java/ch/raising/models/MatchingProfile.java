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
    private List<InvestorType> investorTypes;
    private List<Industry> industries;
    private List<InvestmentPhase> investmentPhases;
    private List<Country> countries;
    private List<Continent> continents;
    private List<Support> support;

    public MatchingProfile() {
        investorTypes = new ArrayList<InvestorType>();
        industries = new ArrayList<Industry>();
        investmentPhases = new ArrayList<InvestmentPhase>();
        countries = new ArrayList<Country>();
        continents = new ArrayList<Continent>();
        support = new ArrayList<Support>();
    }

    public void addSupport(Support support) {
        this.support.add(support);
    }

    public void addCountry(Country country) {
        this.countries.add(country);
    }

    public void addContinent(Continent continent) {
        this.continents.add(continent);
    }

    public void addIndustry(Industry industry) {
        this.industries.add(industry);
    }

    public void addInvestorType(InvestorType type) {
        this.investorTypes.add(type);
    }

    public void addInvestmentPhase(InvestmentPhase phase) {
        this.investmentPhases.add(phase);
    }
}