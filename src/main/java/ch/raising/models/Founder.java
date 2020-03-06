package ch.raising.models;

import lombok.Data;

@Data
public class Founder {
	private int id;
	private int startupId;
	private String name;
	private String education;
	private String roleInStartup;
}
