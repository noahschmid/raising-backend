package ch.raising.models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorProfileResponse {
    private Account account;
    private String username;
    private String name;
    private String description;
    private int investmentMin;
    private int investmentMax;
    private String investorType;
    private ArrayList<Support> support;
    private ArrayList<Country> countries;
    private ArrayList<Continent> continents;
    private ArrayList<Industry> industries;
    private ArrayList<InvestmentPhase> investmentPhases;

    public InvestorProfileResponse() {
        support = new ArrayList<>();
        countries = new ArrayList<>();
        continents = new ArrayList<>();
        industries = new ArrayList<>();
        investmentPhases = new ArrayList<>();
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

    public void addInvestmentPhase(InvestmentPhase phase) {
        this.investmentPhases.add(phase);
    }
}