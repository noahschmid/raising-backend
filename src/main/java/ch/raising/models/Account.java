package ch.raising.models;

import org.springframework.beans.factory.annotation.Autowired;

public class Account {
	
	private final long id;
	private String username;
	private String password;
	private String roles;

	@Autowired
	public Account(long id, String username, String password, String roles) {
		this.id = id;
		this.username = username; 
		this.password = password;
		this.roles = roles == null ? "ROLE_USER" : roles;
	}

	public long getId() { return this.id; }
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	public String getRoles() { return this.roles; }
}
