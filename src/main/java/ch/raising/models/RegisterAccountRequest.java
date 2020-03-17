package ch.raising.models;

import java.util.List;

import lombok.Data;

@Data
public class RegisterAccountRequest {
	
	protected long accountId = -1l;
	protected String name;
	private String password;
	private String roles;
	private String email;
	private int investmentMin = -1;
	private int investmentMax = -1;
	private List<Integer> countries;
	private List<Integer> continents;
	private List<Integer> support;
	private List<Integer> industries;
	private long investmentPhaseId = -1;
	private int boosts = 0;
	private String street;
	private String city;
	private String zipCode;
	private String website;
	private int breakEvenYear = -1;
	private int numberOfFTE = -1;
	private int turnover = -1;
	private List<Integer> invTypes;
	private List<Integer> labels;
	private Contact contact;
	private List<Founder> founders;
	private String description;
	private long investorTypeId = -1;
	private List<Integer> invPhases;

}
