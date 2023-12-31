package ch.raising.models;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountDetails implements UserDetails {
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private long id;
    private boolean startup;
    private boolean investor;

    public AccountDetails(Account account) {
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.authorities = Arrays.stream(account.getRoles().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        this.id = account.getAccountId();
    }

    public void setStartup(boolean isStartup){
    	this.startup = isStartup;
    }
    public boolean getStartup(){
    	return this.startup;
    }
    public void setInvestor(boolean isInvestor){
    	this.investor = isInvestor;
    }
    public boolean getInvestor(){
    	return this.investor;
    }

    public void setEmail(String email) { this.email = email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
    
    public long getId() {
        return this.id;
    }
    

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}