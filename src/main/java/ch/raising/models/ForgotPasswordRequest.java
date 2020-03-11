package ch.raising.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest() {
        super();
    }
}
