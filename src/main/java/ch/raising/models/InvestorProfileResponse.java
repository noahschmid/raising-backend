package ch.raising.models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestorProfileResponse {
    private int accountId;
    private String username;
    private int investmentMin;
    private int investmentMax;
    private String investorType;
    private ArrayList<String> support;
    private ArrayList<String> countries;
    private ArrayList<String> continents;
    private ArrayList<String> industries;
    private ArrayList<String> investmentPhases;

    public InvestorProfileResponse() {
        support = new ArrayList<>();
        countries = new ArrayList<>();
        continents = new ArrayList<>();
        industries = new ArrayList<>();
        investmentPhases = new ArrayList<>();
    }

    public void addSupport(String support) {
        this.support.add(support);
    }

    public void addCountry(String country) {
        this.countries.add(country);
    }

    public void addContinent(String continent) {
        this.continents.add(continent);
    }

    public void addIndustry(String industry) {
        this.industries.add(industry);
    }

    public void addInvestmentPhase(String phase) {
        this.investmentPhases.add(phase);
    }
}