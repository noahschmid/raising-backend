package ch.raising.models;

import java.util.ArrayList;

import lombok.Data;


@Data
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
    private ArrayList<Support> support;
    private ArrayList<Continent> continents;
    private ArrayList<Country> countries;
    private ArrayList<Boardmember> boardmembers;
    private ArrayList<Founder> founders;
    private Contact contact;
    private String street;
    private String city;
    private String zipCode;
    private String website;
    private int breakEvenYear;
}