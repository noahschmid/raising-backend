package ch.raising.models;

import org.springframework.util.DigestUtils;
import org.springframework.security.crypto.password.*;

public class Account {
	
	private final long id;
	private String username;
	private String password;

	private PasswordEncoder passwordEncoder;

	public Account(long id, String username, String password) {
		this.id = id;
		this.username = username; 
		this.password = password;
	}

	public void hashPassword() {
		password = passwordEncoder.encode(password);
	}

	public long getId() { return this.id; }
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
}
