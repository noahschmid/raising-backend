package ch.raising.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final String token;
    private final Long id;
    private boolean startup;
    private boolean investor;
    public LoginResponse(String token, Long id){
    	this.token = token;
    	this.id = id;
    }
}