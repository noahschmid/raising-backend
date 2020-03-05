package ch.raising.models;

import java.util.ArrayList;
import ch.raising.models.Label;
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
    private ArrayList<InvestmentSector> investmentSectors;
    private ArrayList<SupervisionType> supervisionTypes;
    private ArrayList<String> continents;
    private ArrayList<String> countries;

    public Startup() {
        
    }
}