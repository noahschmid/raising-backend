package ch.raising.models;

import lombok.Data;

@Data
public class Boardmember {
	private int id;
	private int startupId;
	private String name;
	private String education;
	private String profession;
	private String roleInStartup;
	private int joinedIn;
}
