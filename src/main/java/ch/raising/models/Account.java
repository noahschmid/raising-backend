package ch.raising.models;

import org.springframework.util.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;

public class Account {
	
	private final long id;
	private String username;
	private String password;
	private String roles;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	public Account(long id, String username, String password, String roles) {
		this.id = id;
		this.username = username; 
		this.password = password;
		this.roles = roles == null ? "ROLE_USER" : roles;
	}

	/**
	 * Hash the password using bcrypt
	 */
	public void hashPassword() {
		password = encoder.encode(password);
	}

	public long getId() { return this.id; }
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	public String getRoles() { return this.roles; }
}
