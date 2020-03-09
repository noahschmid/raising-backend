package ch.raising.models;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String code;
    private String password;

    public PasswordResetRequest(String code, String password) {
        this.code = code;
        this.password = password;
    }
}