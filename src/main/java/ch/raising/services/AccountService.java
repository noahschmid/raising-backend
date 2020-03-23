package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.ErrorResponse;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InValidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UpdateQueryBuilder;
import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.interfaces.IAssignmentTableModel;

@Primary
@Service
public class AccountService implements UserDetailsService {
	@Autowired
	protected AccountRepository accountRepository;

	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private ResetCodeUtil resetCodeUtil;

	@Autowired
	private JdbcTemplate jdbc;

	@Autowired
	private JwtUtil jwtUtil;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	protected AssignmentTableRepository countryRepository;

	protected AssignmentTableRepository continentRepository;

	protected AssignmentTableRepository supportRepository;

	protected AssignmentTableRepository industryRepository;

	@Autowired
	public AccountService(AccountRepository accountRepository, MailUtil mailUtil, ResetCodeUtil resetCodeUtil,
			JdbcTemplate jdbc) {
		this.accountRepository = accountRepository;
		this.mailUtil = mailUtil;
		this.resetCodeUtil = resetCodeUtil;
		this.jdbc = jdbc;
		this.countryRepository = new AssignmentTableRepository(jdbc, "country", MapUtil::mapRowToCountry);
		this.continentRepository = new AssignmentTableRepository(jdbc, "continent");
		this.supportRepository = new AssignmentTableRepository(jdbc, "support");
		this.industryRepository = new AssignmentTableRepository(jdbc, "industry");
	}

	@Override
	public AccountDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			Account account = accountRepository.findByEmail(email);
			return new AccountDetails(account);
		} catch (EmailNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
	}

	/**
	 * Check whether given email is already registered
	 * 
	 * @param email the email to check
	 * @return
	 */
	public ResponseEntity<?> isEmailFree(String email) {
		if (accountRepository.emailExists(email)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} else {
			return ResponseEntity.ok().build();
		}
	}

	/**
	 * Register new user account
	 * 
	 * @param account to be registered
	 * @return ResponseEntity with status code and message
	 */
	public ResponseEntity<?> registerProfile(Account account) {
		try {
			registerAccount(account);
			return ResponseEntity.ok().build();
		} catch (DatabaseOperationException e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (NullPointerException e) {
			return ResponseEntity.status(500).body(new ErrorResponse("NullpointerException: "+e.getMessage()));
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * saves an {@link Account} supertype to the database should be overridden and
	 * called by subclass.
	 * 
	 * @param req sent from the frontend
	 * @throws Exception
	 */
	protected long registerAccount(Account req) throws Exception {
		if (req.isInComplete()) {
			throw new InValidProfileException("Profile is invalid");
		} else if (accountRepository.emailExists(req.getEmail())) {
			throw new InValidProfileException("Email already exists");
		}

		long accountId = accountRepository.add(req);

		if(req.getCountries() != null)
			req.getCountries().forEach(country -> countryRepository.addEntryToAccountById(country.getId(), accountId));
		if(req.getContinents() != null)
			req.getContinents()
				.forEach(continent -> continentRepository.addEntryToAccountById(continent.getId(), accountId));
		if(req.getSupport() != null)
			req.getSupport().forEach(sup -> supportRepository.addEntryToAccountById(sup.getId(), accountId));
		if(req.getIndustries() != null)
			req.getIndustries().forEach(ind -> industryRepository.addEntryToAccountById(ind.getId(), accountId));

		return accountId;
	}

	/**
	 * Delete user account
	 * 
	 * @param tableEntryId the tableEntryId of the account to delete
	 * @return ResponseEntity with status code and message
	 */
	public ResponseEntity<?> deleteProfile(long id) {
		try {
			deleteAccount(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * is overwritten by subtype {@link InvestorService} and {@link StartupService}
	 * to allow the retrieving of a specific accounttype.
	 * 
	 * @param tableEntryId
	 * @return the account with the specified tableEntryId. the account is fully
	 *         initialized with all lists and objects non null.
	 */
	protected void deleteAccount(long id) {
		accountRepository.delete(id);
	}

	/**
	 * gets the requested Profile and handles all the Responses for the request
	 * 
	 * @param tableEntryId
	 * @return
	 */
	public ResponseEntity<?> getProfile(long id) {
		try {
			Account acc = getAccount(id);
			return ResponseEntity.ok(acc);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * is overwritten by subtype {@link InvestorService} and {@link StartupService}
	 * to allow the retrieving of a specific accounttype.
	 * 
	 * @param tableEntryId
	 * @return the account with the specified tableEntryId. the account is fully
	 *         initialized with all lists and objects non null.
	 */
	protected Account getAccount(long id) {
		List<AssignmentTableModel> countries = countryRepository.findByAccountId(id);
		List<AssignmentTableModel> continents = continentRepository.findByAccountId(id);
		List<AssignmentTableModel> support = supportRepository.findByAccountId(id);
		List<AssignmentTableModel> industries = industryRepository.findByAccountId(id);

		Account acc = accountRepository.find(id);

		acc.setCountries(countries);
		acc.setContinents(continents);
		acc.setSupport(support);
		acc.setIndustries(industries);

		return acc;
	}

	/**
	 * Get all accounts
	 * 
	 * @return list of user accounts
	 */
	public List<Account> getAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		accountRepository.getAll().forEach(acc -> accounts.add(acc));
		return accounts;
	}

	/**
	 * Find user account by tableEntryId
	 * 
	 * @param tableEntryId the tableEntryId of the desired account
	 * @return Account instance of the desired account
	 */
	public Account findById(long id) {
		return accountRepository.find(id);
	}

	/**
	 * Check if given tableEntryId belongs to own account
	 * 
	 * @param tableEntryId the tableEntryId of the account to check against
	 * @param isAdmin      indicates whether the user is admin
	 * @return true if account belongs to request, false otherwise
	 */
	public boolean isOwnAccount(long id) {
		Account account = findById(id);
		String username = SecurityContextHolder.getContext().getAuthentication().getName().toLowerCase();
		if (account == null || username == null)
			return false;

		if (!account.getName().equals(username))
			return false;
		return true;
	}

	/**
	 * Update user account
	 * 
	 * @param tableEntryId the tableEntryId of the account to be updated
	 * @param req          the http request instance
	 * @param isAdmin      indicates whether or not the user requesting the update
	 *                     is admin
	 * @return Response entity with status code and message
	 */
	public ResponseEntity<?> updateAccount(int id, Account acc) {
		return ResponseEntity.status(500).body(new ErrorResponse("Not Implemented yet"));
	}

	/**
	 * See if email matches hashed email in existing account
	 * 
	 * @param request the password reset request with the email in clear text
	 * @return response entity with status code
	 */
	public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
		try {
			Account account = accountRepository.findByEmail(request.getEmail());
			String code = resetCodeUtil.createResetCode(account);
			mailUtil.sendPasswordForgotEmail(request.getEmail(), code);

		} catch (MessagingException e) {
			return ResponseEntity.status(500).body(e.getStackTrace());
		} catch (EmailNotFoundException e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}

		return ResponseEntity.ok().build();
	}

	/**
	 * Reset password if valid request
	 * 
	 * @param tableEntryId the tableEntryId of the account to reset password
	 * @param request      the request with reset code and new password
	 * @return response entity with status code
	 */
	public ResponseEntity<?> resetPassword(PasswordResetRequest request) {
		try {
			long id = resetCodeUtil.validate(request);
			if (id != -1) {
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
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addCountryToAccountById(long countryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			countryRepository.addEntryToAccountById(countryId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * remove entry in countryrepository specified by those ids
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteCountryFromAccountById(long countryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			countryRepository.deleteEntryFromAccountById(countryId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Add Continent to account
	 * 
	 * @param continentId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addContinentToAccountById(long continentId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			continentRepository.addEntryToAccountById(continentId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * deletes continent from account
	 * 
	 * @param continentId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteContinentFromAccountById(long continentId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			continentRepository.deleteEntryFromAccountById(continentId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * adds support to account
	 * 
	 * @param supportId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addSupportToAccountById(long supportId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			supportRepository.addEntryToAccountById(accDet.getId(), supportId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * deletes support form account
	 * 
	 * @param supportId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteSupportFromAccountById(long supportId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			supportRepository.deleteEntryFromAccountById(supportId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * deletes industry form account
	 * 
	 * @param industryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteIndustryFromAccountById(long industryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			industryRepository.deleteEntryFromAccountById(industryId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * adds industry to account
	 * 
	 * @param industryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addIndustryToAccountById(long industryId) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getAuthorities();
			industryRepository.addEntryToAccountById(industryId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
}
