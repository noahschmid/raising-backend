package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AccountUpdateRequest {
    private String name = null;
    private String password = null;
    private String roles = null;
    private String emailHash = null;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AccountUpdateRequest() {}

    public AccountUpdateRequest(String name, String password, String roles, String email) {
        this.name = name.toLowerCase();
        if(password != null)
            this.password = encoder.encode(password);
        if(roles != null)
            this.roles = roles;
        if(email != null)
            this.emailHash = encoder.encode(email);
    }

    public String getName() { return this.name.toLowerCase(); }
    public String getPassword() { return this.password; }
    public String getRoles() { return this.roles; }
    public String getEmailHash() { return this.emailHash; }

    public void setRoles(String roles) { this.roles = roles; }
    public void setName(String name) { this.name = name.toLowerCase(); }
    public void setPassword(String password) { this.password = encoder.encode(password); }
    public void setEmail(String email) { this.emailHash = encoder.encode(email); }
}