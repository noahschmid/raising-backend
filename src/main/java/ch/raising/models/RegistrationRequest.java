package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegistrationRequest {
    private String username = null;
    private String password = null;
    private String emailHash = null;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public RegistrationRequest(String username, String password, String email) {
        this.username = username;
        if(password != null)
            this.password = encoder.encode(password);
        if(email != null)
            this.emailHash = encoder.encode(email);
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public String getEmailHash() { return this.emailHash; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String passwordHash) { this.password = passwordHash; }
    public void setEmail(String email) { this.emailHash = encoder.encode(email); }
}