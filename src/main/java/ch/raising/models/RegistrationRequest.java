package ch.raising.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegistrationRequest {
    private String username = null;
    private String password = null;
    private String emailHash = null;
    private String email = null;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public RegistrationRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        if(email != null)
            this.emailHash = encoder.encode(email);
        this.email = email;
    }

    public RegistrationRequest() {
        super();
    }

    /**
     * 
     * @return username from request
     */
    public String getUsername() { return this.username; }
    /**
     * 
     * @return password from request
     */
    public String getPassword() { return this.password; }
    /**
     * 
     * @return hashed email from request
     */
    public String getEmailHash() { return this.emailHash; }
    public String getEmail() { return this.email; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { 
        this.password = password; 
    }
    public void setEmail(String email) {
        if(email != null)
            this.emailHash = encoder.encode(email);
        this.email = email;
    }
}