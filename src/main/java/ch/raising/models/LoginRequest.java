package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginRequest {
    private String username;
    private String password;
    
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequest() {
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = encoder.encode(password); }
}