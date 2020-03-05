package ch.raising.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AccountUpdateRequest;
import ch.raising.models.Response;
import ch.raising.data.AccountRepository;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private static AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        return new AccountDetails(account);
    }

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * Log in with given user account
     * @param account the user account to log in
     * @return ResponseEntity with status code and message
     */
    public ResponseEntity login(Account account) {
        account.hashPassword();
		if(accountRepository.accountExists(account))
			return ResponseEntity.ok(new Response("Login successful"));
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Access denied"));
    }

    /**
     * Register new user account
     * @param  the account to register
     * @return  ResponseEntity with status code and message
     */
    public ResponseEntity<?> register(Account account) {
        if(account.getUsername().length() == 0 || account.getPassword().length() == 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Please provide username and password"));
        if(accountRepository.usernameExists(account.getUsername()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Username already exists"));
        try  {
            accountRepository.add(account);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.toString()));
        }
		return ResponseEntity.ok(new Response("Successfully registered new account", account));
    }

    /**
     * Delete user account
     * @param id the id of the account to delete
     * @return ResponseEntity with status code and message
     */
    public ResponseEntity<?> deleteAccount(int id) {
        if(accountRepository.find(id) == null)
            return ResponseEntity.status(500).body(new Response("Account doesn't exist"));
        try {   
            accountRepository.delete(id);
            return ResponseEntity.ok(new Response("Successfully deleted account"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
    }

    /**
     * Get all accounts
     * @return list of user accounts
     */
    public List<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();
		accountRepository.getAllAccounts().forEach(acc -> accounts.add(acc));
		return accounts;
    }

    /**
     * Find user account by id
     * @param id the id of the desired account
     * @return Account instance of the desired account
     */
    public Account findById(int id) {
        return accountRepository.find(id);
    }

    /**
     * Check if request comes from own account (or admin account)
     * @param id the id of the account to check against
     * @param request instance of the http request
     * @return true if account belongs to request, false otherwise
     */
    public boolean isOwnAccount(int id, boolean isAdmin) {
        Account account = findById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(account == null)
            return false;

        if(!account.getUsername().equals(username) && !isAdmin)
            return false;

        return true;
    }

    /**
     * Update user account
     * @param id the id of the account to be updated
     * @param req the http request instance
     * @param isAdmin indicates whether or not the user requesting the update is admin
     * @return Response entity with status code and message
     */
    public ResponseEntity<?> updateAccount(int id, AccountUpdateRequest req, boolean isAdmin) {
        if(accountRepository.find(id) == null)
            return ResponseEntity.status(500).body(new Response("Account doesn't exist"));
        try {  
            if(req.getUsername() != null) {
                if(accountRepository.findByUsername(req.getUsername()) != null)
                    return ResponseEntity.status(500).body(new Response("Username already in use"));
            }
            accountRepository.update(id, req, isAdmin);
            return ResponseEntity.ok(new Response("Successfully updated account"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response(e.getMessage()));
        }
    }
}
