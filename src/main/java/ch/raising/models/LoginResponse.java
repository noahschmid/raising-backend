package ch.raising.models;

public class LoginResponse {
    private final String token;
    private final Long id;

    public LoginResponse(String token, long id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public Long getId() {
        return this.id;
    }
}