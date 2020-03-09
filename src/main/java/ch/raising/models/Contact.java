package ch.raising.models;

import lombok.Data;

@Data
public class Contact {
	private int id;
	private int startupId;
	private String name;
	private String role;
	private String email;
	private String phone;
}
