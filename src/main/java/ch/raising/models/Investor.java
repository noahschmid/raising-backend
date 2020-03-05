package ch.raising.models;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Investor {
    private int id;
    private Account account;
    private int investmentMin;
    private int investmentMax;
    private InvestorType investorType;
    private ArrayList<InvestmentPhase> investmentPhases;
    private ArrayList<Support> supports;
    private ArrayList<String> countries;
    private ArrayList<String> continents;
    private ArrayList<Industry> investmentSectors;

    public Investor() {
    }
}