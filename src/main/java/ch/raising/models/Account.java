package ch.raising.models;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {
	
	private final long id;
	private String username;
	private String password;
	private String roles;
	private String emailHash;

	@Autowired
	public Account(long id, String username, String password, String roles, String emailHash) {
		this.id = id;
		this.username = username; 
		this.password = password;
		this.roles = roles == null ? "ROLE_USER" : roles;
		this.emailHash = emailHash;
	}
}
