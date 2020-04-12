package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.FreeEmailRequest;
import ch.raising.models.Media;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PasswordResetException;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UpdateQueryBuilder;
import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.MediaRepository;
import ch.raising.interfaces.IMediaRepository;

@Primary
@Service
public class AccountService implements UserDetailsService {

	protected AccountRepository accountRepository;
	private MailUtil mailUtil;
	private ResetCodeUtil resetCodeUtil;
	private JdbcTemplate jdbc;
	private JwtUtil jwtUtil;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private IMediaRepository<Media> galleryRepository;
	private IMediaRepository<Media> pPicRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	protected AssignmentTableRepository countryRepo;
	protected AssignmentTableRepository continentRepo;
	protected AssignmentTableRepository supportRepo;
	protected AssignmentTableRepository industryRepo;

	@Autowired
	public AccountService(AccountRepository accountRepository, MailUtil mailUtil, ResetCodeUtil resetCodeUtil,
			JdbcTemplate jdbc, JwtUtil jwtUtil) {
		this.accountRepository = accountRepository;
		this.mailUtil = mailUtil;
		this.resetCodeUtil = resetCodeUtil;
		this.jdbc = jdbc;
		this.jwtUtil = jwtUtil;
		this.countryRepo = AssignmentTableRepository.getInstance(jdbc).withTableName("country")
				.withRowMapper(MapUtil::mapRowToCountry);
		this.continentRepo = AssignmentTableRepository.getInstance(jdbc).withTableName("continent");
		this.supportRepo = AssignmentTableRepository.getInstance(jdbc).withTableName("support");
		this.industryRepo = AssignmentTableRepository.getInstance(jdbc).withTableName("industry");
		this.galleryRepository = new MediaRepository(jdbc, "gallery");
		this.pPicRepository = new MediaRepository(jdbc, "profilepicture");
	}

	@Override
	public AccountDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			AccountDetails accDet = new AccountDetails(accountRepository.findByEmail(email));
			accDet.setStartup(accountRepository.isStartup(accDet.getId()));
			accDet.setInvestor(accountRepository.isInvestor(accDet.getId()));
			return accDet;
		} catch (EmailNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		} catch (DataAccessException e) {
			throw new UsernameNotFoundException(e.getMessage());
		} catch (SQLException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
	}

	/**
	 * Check whether given email is already registered
	 * 
	 * @param email the email to check
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public boolean isEmailFree(String email) throws DataAccessException, SQLException {
		try {
			return accountRepository.findByEmail(email) == null;
		} catch (EmailNotFoundException e) {
			return true;
		}
	}

	/**
	 * Register new user account
	 * 
	 * @param account to be registered
	 * @return ResponseEntity with status code and message
	 * @throws Exception
	 */
	public LoginResponse registerProfile(Account account) throws DatabaseOperationException, SQLException, Exception {
		long id;
		try {
			id = registerAccount(account);
		} catch (DatabaseOperationException e) {
			rollback(account);
			throw e;
		} catch (Exception e) {
			rollback(account);
			throw e;
		}
		account.setAccountId(id);
		LoginResponse loginResp = (LoginResponse) login(new LoginRequest(account.getEmail(), account.getPassword()));
		loginResp.setAccount(account);
		return loginResp;
	}

	private void rollback(Account account) {
		try {
			Account found = accountRepository.findByEmail(account.getEmail());
			accountRepository.delete(found.getAccountId());
		} catch (Exception e) {
		}
	}

	/**
	 * saves an {@link Account} supertype to the database should be overridden and
	 * called by subclass.
	 * 
	 * @param req sent from the frontend
	 * @throws DatabaseOperationException
	 * @throws Exception
	 */
	protected long registerAccount(Account req)
			throws SQLException, DataAccessException, InvalidProfileException, DatabaseOperationException {
		if (req.isInComplete()) {
			throw new InvalidProfileException("Profile is invalid");
		}
		try {
			accountRepository.findByEmail(req.getEmail());
			throw new InvalidProfileException("Email already exists");
		} catch (EmailNotFoundException e) {

			long accountId = accountRepository.add(req);

			if (req.getGallery() != null) {
				for (long pic : req.getGallery()) {
					galleryRepository.addAccountIdToMedia(pic, accountId);
				}
			}
			
			pPicRepository.addAccountIdToMedia(req.getProfilePictureId(), accountId);

			countryRepo.addEntriesToAccount(accountId, req.getCountries());
			continentRepo.addEntriesToAccount(accountId, req.getContinents());
			supportRepo.addEntriesToAccount(accountId, req.getSupport());
			industryRepo.addEntriesToAccount(accountId, req.getIndustries());
		
		return accountId;

	}

	}

	/**
	 * Delete user account
	 * 
	 * @param id the id of the account to delete
	 * @return ResponseEntity with status code and message
	 */
	public void deleteProfile(long id) throws SQLException, DataAccessException {
		accountRepository.delete(id);
	}

	/**
	 * is overwritten by subtype {@link InvestorService} and {@link StartupService}
	 * to allow the retrieving of a specific accounttype.
	 * 
	 * @param id
	 * @return the account with the specified id. the account is fully initialized
	 *         with all lists and objects non null.
	 * @throws SQLException
	 * @throws DataAccessException
	 */	
	public Account getAccount(long id) throws DataAccessException, SQLException {
		List<Long> countries = countryRepo.findIdByAccountId(id);
		List<Long> continents = continentRepo.findIdByAccountId(id);
		List<Long> support = supportRepo.findIdByAccountId(id);
		List<Long> industries = industryRepo.findIdByAccountId(id);
		List<Long> gallery = galleryRepository.findMediaIdByAccountId(id);
		Account acc = accountRepository.find(id);
		acc.setPassword("");
		acc.setRoles("");

		acc.setGallery(gallery);

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
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public List<Account> getAccounts() throws DataAccessException, SQLException {
		List<Account> accounts = accountRepository.getAll();
		for(Account a: accounts) {
			long accountId = a.getAccountId();
			a.setCountries(countryRepo.findIdByAccountId(accountId));
			a.setContinents(continentRepo.findIdByAccountId(accountId));
			a.setSupport(supportRepo.findIdByAccountId(accountId));
			a.setIndustries(industryRepo.findIdByAccountId(accountId));
			a.setGallery(galleryRepository.findMediaIdByAccountId(accountId));
		}
		return accounts;
	}

	/**
	 * Check if given id belongs to own account
	 * 
	 * @param id      the id of the account to check against
	 * @param isAdmin indicates whether the user is admin
	 * @return true if account belongs to request, false otherwise
	 */
	public boolean isOwnAccount(long id) {
		AccountDetails udet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return udet.getId() == id && (udet != null && udet.getId() != 0);
	}

	/**
	 * should be used by
	 * {@link ch.raising.controllers.StartupController},{@link ch.raising.controllersAccountController},{@link ch.raising.controllersInvestorController}
	 * 
	 * @param id
	 * @param acc
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void updateProfile(int id, Account acc) throws DataAccessException, SQLException {
		updateAccount(id, acc);
	}

	/**
	 * updates the account. should be overwritten and called by subtypes
	 * {@link InvestorService} and {@link StartupService}
	 * 
	 * @param id the id for the account
	 * @param acc Account containing all uninitialized fields to be updated
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	protected void updateAccount(int id, Account acc) throws DataAccessException, SQLException {
		countryRepo.updateAssignment(id, acc.getCountries());
		continentRepo.updateAssignment(id, acc.getContinents());
		supportRepo.updateAssignment(id, acc.getSupport());
		industryRepo.updateAssignment(id, acc.getIndustries());
		
		accountRepository.update(id, acc);
	}

	/**
	 * See if email matches hashed email in existing account
	 * 
	 * @param request the password reset request with the email in clear text
	 * @return response entity with status code
	 * @throws EmailNotFoundException
	 * @throws MessagingException
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void forgotPassword(ForgotPasswordRequest request) throws EmailNotFoundException, MessagingException, DataAccessException, SQLException {
		Account account = accountRepository.findByEmail(request.getEmail());
		String code = resetCodeUtil.createResetCode(account);
		mailUtil.sendPasswordForgotEmail(request.getEmail(), code);
	}

	/**
	 * Reset password if valid request
	 * 
	 * @param id      the id of the account to reset password
	 * @param request the request with reset code and new password
	 * @return response entity with status code
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws PasswordResetException
	 * @throws Exception
	 */
	public LoginResponse resetPassword(PasswordResetRequest request)
			throws DataAccessException, SQLException, PasswordResetException {
		long id = resetCodeUtil.validate(request);

		UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("account", id, jdbc);
		updateQuery.addField(encoder.encode(request.getPassword()), "password");
		updateQuery.execute();
		AccountDetails userDetails = new AccountDetails(accountRepository.find(id));
		return new LoginResponse(jwtUtil.generateToken(userDetails), id);
	}

	/**
	 * Method to login a specific user. used by
	 * {@link ch.raising.controllers.AccountController}
	 * 
	 * @param request The Login Request containing the email and password
	 *                {@link ch.raising.models.LoginRequest }
	 * @return A login response Model {@link ch.raising.models.LoginResponse}
	 */
	public LoginResponse login(LoginRequest request) throws AuthenticationException, UsernameNotFoundException {
		UsernamePasswordAuthenticationToken unamePwToken = new UsernamePasswordAuthenticationToken(request.getEmail(),
				request.getPassword());

		authenticationManager.authenticate(unamePwToken);

		final AccountDetails userDetails = loadUserByUsername(request.getEmail());
		final String token = jwtUtil.generateToken(userDetails);
		return new LoginResponse(token, userDetails.getId(), userDetails.getStartup(), userDetails.getInvestor());
	}

}
