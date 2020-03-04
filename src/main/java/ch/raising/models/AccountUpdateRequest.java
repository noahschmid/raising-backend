package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AccountUpdateRequest {
    private String username = null;
    private String password = null;
    private String roles = null;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AccountUpdateRequest(String username, String password, String roles) {
        this.username = username;
        if(password != null)
            this.password = encoder.encode(password);
        if(roles != null)
            this.roles = roles;
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public String getRoles() { return this.roles; }

    public void setRoles(String roles) { this.roles = roles; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}