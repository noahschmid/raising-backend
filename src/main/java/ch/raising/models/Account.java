package ch.raising.models;

import lombok.Data;

@Data
public class Account {
	
	private final long id;
	private final String username;
	private final String password;

	public Account(long id, String username, String password) {
		this.id = id;
		this.username = username; 
		this.password = password;
	}

	public long getId() { return this.id; }
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
}
