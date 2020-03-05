package ch.raising.models;

import java.util.ArrayList;
import ch.raising.models.InvestorType;
import ch.raising.models.SupervisionType;
import lombok.Getter;
import lombok.Setter;
import ch.raising.models.InvestmentPhase;

@Getter
@Setter
public class Investor {
    private int id;
    private Account account;
    private int investmentMin;
    private int investmentMax;
    private InvestorType investorType;
    private ArrayList<InvestmentPhase> investmentPhases;
    private ArrayList<SupervisionType> supervisionTypes;
    private ArrayList<String> countries;
    private ArrayList<String> continents;
    private ArrayList<InvestmentSector> investmentSectors;

    public Investor() {
    }
}