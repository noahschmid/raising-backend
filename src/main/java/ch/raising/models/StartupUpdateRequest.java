package ch.raising.models;

import lombok.Data;

@Data
public class StartupUpdateRequest {
	
	 private int id;
	 private String name;
	 private String description;
	 private int accountId;
	 private int investmentMin = -1;
	 private int investmentMax = -1;
	 private int boosts = -1;
	 private int numberOfFTE;
	 private int turnover;
	 private String street;
	 private String city;
	 private String website;
	 private int breakEvenYear;
	 private int zipCode;
}