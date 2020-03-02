package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AccountUpdateRequest {
    private String username = null;
    private String password = null;
    private String roles = "ROLES_USER";

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
}