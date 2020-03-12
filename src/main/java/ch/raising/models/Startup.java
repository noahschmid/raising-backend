package ch.raising.models;

import lombok.Data;

@Data
public class Startup {
    private long accountId = -1;
    private String name;
    private int investmentMin = -1;
    private int investmentMax = -1;
    private long investmentPhaseId = -1;
    private int boosts = 0;
    private String street;
    private String city;
    private String zipCode;
    private String website;
    private int breakEvenYear = -1;
    private int numberOfFTE = -1;
    private int turnover = -1;
}