package ch.raising.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import ch.raising.models.ErrorResponse;
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
     * Register new user account
     * @param  the account to register
     * @return  ResponseEntity with status code and message
     */
    public ResponseEntity<?> register(Account account) {
        if(account.getUsername() == null || account.getPassword() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Please provide username and password"));
        if(accountRepository.usernameExists(account.getUsername()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Username already exists"));
        try  {
            accountRepository.add(account);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(e.toString()));
        }
		return ResponseEntity.ok().build();
    }

    /**
     * Delete user account
     * @param id the id of the account to delete
     * @return ResponseEntity with status code and message
     */
    public ResponseEntity<?> deleteAccount(int id) {
        if(accountRepository.find(id) == null)
            return ResponseEntity.status(500).body(new ErrorResponse("Account doesn't exist"));
        try {   
            accountRepository.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
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
     * Check if given id belongs to own account
     * @param id the id of the account to check against
     * @param isAdmin indicates whether the user is admin
     * @return true if account belongs to request, false otherwise
     */
    public boolean isOwnAccount(int id) {
        Account account = findById(id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(account == null || username == null)
            return false;

        if(!account.getUsername().equals(username))
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
            return ResponseEntity.status(500).body(new ErrorResponse("Account doesn't exist"));
        try {  
            if(req.getUsername() != null) {
                accountRepository.findByUsername(req.getUsername());
                return ResponseEntity.status(500).body(new ErrorResponse("Username already in use"));
            }
            accountRepository.update(id, req, isAdmin);
            return ResponseEntity.ok().build();
        } catch(DataAccessException e) { // username not in use
            accountRepository.update(id, req, isAdmin);
            return ResponseEntity.ok().build();
        }
    }
}
