package ch.raising.models.responses;

import ch.raising.models.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private boolean startup;
    private boolean investor;
    private Account account;
    public LoginResponse(String token, Long id){
    	this.token = token;
    	this.id = id;
    }
    public LoginResponse(String token, Long id, boolean startup, boolean investor){
    	this.token = token;
    	this.id = id;
    	this.investor = investor;
    	this.startup = startup;
    }
   public void setAccount(Account account) {
	   this.account = account;
   }
}