package ch.raising.models;

import java.util.ArrayList;
import java.util.List;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.Data;

@Data
public class MatchingProfile {
    private boolean isStartup;
    private String name;
    private String description;
    private Long accountId = -1L;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private List<IAssignmentTableModel> investorTypes;
    private List<IAssignmentTableModel> industries;
    private List<IAssignmentTableModel> investmentPhases;
    private List<IAssignmentTableModel> countries;
    private List<IAssignmentTableModel> continents;
    private List<IAssignmentTableModel> support;

    public MatchingProfile() {
        investorTypes = new ArrayList<IAssignmentTableModel>();
        industries = new ArrayList<IAssignmentTableModel>();
        investmentPhases = new ArrayList<IAssignmentTableModel>();
        countries = new ArrayList<IAssignmentTableModel>();
        continents = new ArrayList<IAssignmentTableModel>();
        support = new ArrayList<IAssignmentTableModel>();
    }

    public void addSupport(IAssignmentTableModel support) {
        this.support.add(support);
    }

    public void addCountry(Country country) {
        this.countries.add(country);
    }

    public void addContinent(IAssignmentTableModel continent) {
        this.continents.add(continent);
    }

    public void addIndustry(IAssignmentTableModel industry) {
        this.industries.add(industry);
    }

    public void addInvestorType(IAssignmentTableModel type) {
        this.investorTypes.add(type);
    }

    public void addInvestmentPhase(IAssignmentTableModel phase) {
        this.investmentPhases.add(phase);
    }
}