package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AccountUpdateRequest {
    private String username = null;
    private String password = null;
    private String roles = null;
    private String emailHash = null;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AccountUpdateRequest(String username, String password, String roles, String email) {
        this.username = username.toLowerCase();
        if(password != null)
            this.password = encoder.encode(password);
        if(roles != null)
            this.roles = roles;
        if(email != null)
            this.emailHash = encoder.encode(email);
    }

    public String getUsername() { return this.username.toLowerCase(); }
    public String getPassword() { return this.password; }
    public String getRoles() { return this.roles; }
    public String getEmailHash() { return this.emailHash; }

    public void setRoles(String roles) { this.roles = roles; }
    public void setUsername(String username) { this.username = username.toLowerCase(); }
    public void setPassword(String passwordHash) { this.password = passwordHash; }
    public void setEmail(String email) { this.emailHash = encoder.encode(email); }
}