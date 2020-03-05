package ch.raising.models;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Startup {
    private int id;
    private Account account;
    private String name;
    private int investmentMin;
    private int investmentMax;
    private InvestmentPhase investmentPhase;
    private int boosts;
    private ArrayList<Label> labels;
    private ArrayList<InvestorType> investorTypes;
    private ArrayList<Industry> industries;
    private ArrayList<Support> supervisionTypes;
    private ArrayList<String> continents;
    private ArrayList<String> countries;

    public Startup() {

    }
}