package ch.raising.services;

import java.util.ArrayList;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AccountUpdateRequest;
import ch.raising.models.Contact;
import ch.raising.models.Continent;
import ch.raising.models.ErrorResponse;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.RegistrationRequest;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UpdateQueryBuilder;
import ch.raising.data.AccountRepository;
import ch.raising.data.ContinentRepository;
import ch.raising.data.CountryRepository;
import ch.raising.data.IndustryRepository;
import ch.raising.data.SupportRepository;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private ResetCodeUtil resetCodeUtil;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ContinentRepository continentRepository;
    
    @Autowired
    private SupportRepository supportRepository;
    
    @Autowired
    private IndustryRepository industryRepository;

    public AccountService(AccountRepository accountRepository, 
                        MailUtil mailUtil,
                        ResetCodeUtil resetCodeUtil,
                        JdbcTemplate jdbc, CountryRepository countryRepository,
                        ContinentRepository continentRepository, SupportRepository supportRepository,
                        IndustryRepository industryRepository) {
        this.accountRepository = accountRepository;
        this.mailUtil = mailUtil;
        this.resetCodeUtil = resetCodeUtil;
        this.jdbc = jdbc;
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
        this.supportRepository = supportRepository;
        this.industryRepository = industryRepository;
    }
    
    @Override
    public AccountDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        return new AccountDetails(account);
    }
    
    /**
     * Register new user account
     * @param  request account to register
     * @return  ResponseEntity with status code and message
     */
    public void register(RegistrationRequest request) throws Error {
        if(request.getUsername() == null || request.getPassword() == null || request.getEmailHash() == null)
            throw new Error("Please provide username, email and password");

        if(accountRepository.usernameExists(request.getUsername().toLowerCase()))
            throw new Error("Username already exists");

        System.out.println(request.getEmail());
        if(accountRepository.findByEmail(request.getEmail()) != null)
            throw new Error("Account with same email exists");

        try  {
            Account account = new Account(-1, request.getUsername().toLowerCase(), request.getPassword(), null, request.getEmailHash());
            accountRepository.add(account);
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
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
                if(accountRepository.findByUsername(req.getUsername()) != null)
                    return ResponseEntity.status(500).body(new ErrorResponse("Username already in use"));
            }
            if(!isAdmin)
                req.setRoles(null);
            accountRepository.update(id, req);
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * See if email matches hashed email in existing account
     * @param request the password reset request with the email in clear text
     * @return response entity with status code
     */
	public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail());
        try {
            if(account != null) {
                String code = resetCodeUtil.createResetCode(account);
                mailUtil.sendPasswordForgotEmail(request.getEmail(), code);
            }
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(e.getStackTrace());
        }

		return ResponseEntity.ok().build();
    }
    
    /**
     * Reset password if valid request
     * @param id the id of the account to reset password
     * @param request the request with reset code and new password
     * @return response entity with status code
     */
    public ResponseEntity<?> resetPassword(PasswordResetRequest request){
        try {
            long id = resetCodeUtil.validate(request);
            if(id != -1) {
                UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("account", id, accountRepository);
                updateQuery.setJdbc(jdbc);
                updateQuery.addField(encoder.encode(request.getPassword()), "password");
                updateQuery.execute();
                AccountDetails userDetails = new AccountDetails(accountRepository.find(id));
                return ResponseEntity.ok().body(new LoginResponse(jwtUtil.generateToken(userDetails), id));
            } 
            return ResponseEntity.status(500).body(new ErrorResponse("Invalid Reset Code"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Error updating password", e));
        }
    }

    /**
     * Add entry in assignmenttable with both ids
     * @param countryId
     * @return Responsenetitiy with a statuscode and an optional body
     */
	public ResponseEntity<?> addCountryToAccountById(long countryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			countryRepository.addCountryToAccountById(countryId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * remove entry in countryrepository specified by those ids
	 * @param countryId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteCountryFromAccountById(long countryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			countryRepository.deleteCountryFromAccountById(countryId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Add Continent to account
	 * @param continentId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addContinentToAccountById(long continentId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			continentRepository.addContinentToAccountById(continentId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * deletes continent from account
	 * @param continentId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteContinentFromAccountById(long continentId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			continentRepository.deleteContinentFromAccountById(continentId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * adds support to account
	 * @param supportId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addSupportToAccountById(long supportId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			supportRepository.addSupportToAccountById(accDet.getId(),supportId);
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * deletes support form account
	 * @param supportId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteSupportFromAccountById(long supportId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			supportRepository.deleteSupportFromAccountById(supportId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * deletes industry form account
	 * @param industryId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteIndustryFromAccountById(long industryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			industryRepository.deleteIndustryFromAccountById(industryId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	/**
	 * adds industry to account
	 * @param industryId
     * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addIndustryToAccountById(long industryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			industryRepository.addIndustryToAccountById(industryId, accDet.getId());
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	

	
}
