package ch.raising.services;

import java.sql.SQLException
;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.AssignmentTableRepositoryFactory;
import ch.raising.data.MediaRepositoryFactory;
import ch.raising.data.SettingRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.LoginRequest;
import ch.raising.models.Media;
import ch.raising.models.PasswordResetRequest;
import ch.raising.models.Settings;
import ch.raising.models.enums.NotificationType;
import ch.raising.models.responses.AdminAccountResponse;
import ch.raising.models.responses.LoginResponse;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MediaException;
import ch.raising.utils.NotAuthorizedException;
import ch.raising.utils.PasswordResetException;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UpdateQueryBuilder;

/**
 * this class provides the services for any accountrelated functions
 * @see StartupService
 * @see InestorService
 * @author manus
 *
 */
@Primary
@Service
public class AccountService implements UserDetailsService {

	private final int NUMBER_OF_MATCHES = 5;
	private final String STANDARD_LANGUAGE = "ENGLISH";
	protected AccountRepository accountRepository;
	private MailUtil mailUtil;
	private ResetCodeUtil resetCodeUtil;
	private JwtUtil jwtUtil;
	private PasswordEncoder encoder;
	private IMediaRepository galleryRepository;
	private IMediaRepository pPicRepository;
	private final SettingRepository settingRepo;

	@Autowired
	private AuthenticationManager authenticationManager;

	protected AssignmentTableRepository countryRepo;
	protected AssignmentTableRepository continentRepo;
	protected AssignmentTableRepository supportRepo;
	protected AssignmentTableRepository industryRepo;

	private final JdbcTemplate jdbc;

	@Autowired
	public AccountService(AccountRepository accountRepository, MailUtil mailUtil, ResetCodeUtil resetCodeUtil,
			JwtUtil jwtUtil, PasswordEncoder encoder, AssignmentTableRepositoryFactory assignmentFactory,
			MediaRepositoryFactory mrFactory, JdbcTemplate jdbc, SettingRepository settingRepo) throws SQLException {
		this.settingRepo = settingRepo;
		this.accountRepository = accountRepository;
		this.mailUtil = mailUtil;
		this.resetCodeUtil = resetCodeUtil;
		this.encoder = encoder;
		this.jwtUtil = jwtUtil;
		this.countryRepo = assignmentFactory.getRepository("country");
		this.continentRepo = assignmentFactory.getRepository("continent");
		this.supportRepo = assignmentFactory.getRepository("support");
		this.industryRepo = assignmentFactory.getRepository("industry");
		this.galleryRepository = mrFactory.getMediaRepository("gallery");
		this.pPicRepository = mrFactory.getMediaRepository("profilepicture");
		this.jdbc = jdbc;
	}
	
	@Override
	public AccountDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			Account account = accountRepository.findByEmail(email);
			AccountDetails accDet = new AccountDetails(account);
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
	 * @see ch.raising.utils.JwtRequestFilter for faster retreiving that by email, since it is hashed.
	 * @param id
	 * @return {@link AccountDetails} of that user
	 * @throws UsernameNotFoundException if the id did yield a result
	 */
	public AccountDetails loadUserById(long id) throws UsernameNotFoundException {
		try {
			Account account = accountRepository.find(id);
			AccountDetails accDet = new AccountDetails(account);
			accDet.setStartup(accountRepository.isStartup(accDet.getId()));
			accDet.setInvestor(accountRepository.isInvestor(accDet.getId()));
			return accDet;
		} catch (DataAccessException | SQLException e) {
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
	 * for registering a new admin
	 * @param admin
	 * @return {@link LoginResponse} 
	 * @throws Exception
	 */
	public LoginResponse registerAdmin(Account admin) throws Exception {
		long id;
		try {
			id = accountRepository.registerAdmin(admin);
			admin.setAccountId(id);
			LoginResponse loginResp = new LoginResponse();
			loginResp.setAccount(admin);
			return loginResp;
		} catch (Exception e) {
			e.printStackTrace();
			rollback(admin);
			throw e;
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
	 * saves an {@link Account} super type to the database should be overridden and
	 * called by subclass.
	 * 
	 * @param req sent from the frontend
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 * @throws Exception
	 */
	protected long registerAccount(Account req) throws SQLException, DataAccessException, InvalidProfileException,
			MediaException, DatabaseOperationException {
		if (!req.isComplete()) {
			throw new InvalidProfileException("Profile is inComplete");
		}
		if (!this.isEmailFree(req.getEmail().toLowerCase())) {
			throw new InvalidProfileException("Email already exists");
		}
		req.setEmail(req.getEmail().toLowerCase());
		long accountId = accountRepository.add(req);

		settingRepo.addSettings(accountId, Settings.builder().accountId(accountId).notificationTypes(NotificationType.getAll())
				.numberOfMatches(NUMBER_OF_MATCHES).language(STANDARD_LANGUAGE).build());

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
	public Account getAccount(long id) throws DataAccessException, SQLException, DatabaseOperationException {

		List<Long> countries = countryRepo.findIdByAccountId(id);
		List<Long> continents = continentRepo.findIdByAccountId(id);
		List<Long> support = supportRepo.findIdByAccountId(id);
		List<Long> industries = industryRepo.findIdByAccountId(id);
		List<Long> gallery = galleryRepository.findMediaIdByAccountId(id);
		Account acc = accountRepository.find(id);
		acc.setPassword("");
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
	public AdminAccountResponse getAccounts() throws DataAccessException, SQLException {
		List<Account> accounts = accountRepository.getAll();
		List<Account> startups = new ArrayList<Account>();
		List<Account> investors = new ArrayList<Account>();

		for (Account a : accounts) {
			long accountId = a.getAccountId();
			a.setCountries(countryRepo.findIdByAccountId(accountId));
			a.setContinents(continentRepo.findIdByAccountId(accountId));
			a.setSupport(supportRepo.findIdByAccountId(accountId));
			a.setIndustries(industryRepo.findIdByAccountId(accountId));
			a.setGallery(galleryRepository.findMediaIdByAccountId(accountId));
			if (accountRepository.isInvestor(a.getAccountId())) {
				investors.add(a);
			} else if (accountRepository.isStartup(a.getAccountId())) {
				startups.add(a);
			}
		}
		accounts.removeAll(startups);
		accounts.removeAll(investors);
		return new AdminAccountResponse(startups, investors, accounts);
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
	 * updates the account. should be overwritten and called by subtypes
	 * {@link InvestorService} and {@link StartupService} * should be used by
	 * {@link ch.raising.test.controllers.StartupController},{@link ch.raising.controllersAccountController},{@link ch.raising.controllersInvestorController}
	 * 
	 * @param id  the id for the account
	 * @param acc Account containing all uninitialized fields to be updated
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public LoginResponse updateAccount(int id, Account acc, String token) throws DataAccessException, SQLException, 
		NotAuthorizedException {
		countryRepo.updateAssignment(id, acc.getCountries());
		continentRepo.updateAssignment(id, acc.getContinents());
		supportRepo.updateAssignment(id, acc.getSupport());
		industryRepo.updateAssignment(id, acc.getIndustries());

		accountRepository.update(id, acc);

		if(acc.getEmail() != null && !acc.getEmail().isEmpty()) {
			AccountDetails accDetails = (AccountDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			accDetails.setEmail(acc.getEmail());
			return new LoginResponse(jwtUtil.generateToken(accDetails), accDetails.getId());
		}

		return refreshToken(token);
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
	public void forgotPassword(ForgotPasswordRequest request)
			throws EmailNotFoundException, MessagingException, DataAccessException, SQLException {
		Account account = accountRepository.findByEmail(request.getEmail());
		String code = resetCodeUtil.createResetCode(account);
		if (code != null) {
			mailUtil.sendPasswordForgotEmail(request.getEmail(), code);
		}
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

		UpdateQueryBuilder updateQuery = new UpdateQueryBuilder(jdbc, "account", id);
		updateQuery.addField(encoder.encode(request.getPassword()), "password");
		updateQuery.execute();
		AccountDetails userDetails = new AccountDetails(accountRepository.find(id));
		return new LoginResponse(jwtUtil.generateToken(userDetails), id);
	}

	/**
	 * Method to login a specific user. used by
	 * {@link ch.raising.test.controllers.AccountController}
	 * 
	 * @param request The Login Request containing the email and password
	 *                {@link ch.raising.test.models.LoginRequest }
	 * @return A login response Model
	 *         {@link ch.raising.test.models.responses.LoginResponse}
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public LoginResponse login(LoginRequest request)
			throws AuthenticationException, UsernameNotFoundException, DataAccessException, SQLException {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(),
				request.getPassword());

		Authentication auth = authenticationManager.authenticate(token);

		AccountDetails userDetails = (AccountDetails) auth.getPrincipal();
		userDetails.setInvestor(accountRepository.isInvestor(userDetails.getId()));
		userDetails.setStartup(accountRepository.isStartup(userDetails.getId()));

		final String returnToken = jwtUtil.generateToken(userDetails);
		return new LoginResponse(returnToken, userDetails.getId(), userDetails.getStartup(), userDetails.getInvestor());
	}
	/**
	 * logs an admin in
	 * @param req
	 * @return {@link LoginRequest} containing a special token to identify an admin.
	 * @throws UsernameNotFoundException
	 * @throws DataAccessException
	 * @throws AuthenticationException
	 * @throws SQLException
	 * @throws NotAuthorizedException
	 */
	public LoginResponse adminLogin(LoginRequest req) throws UsernameNotFoundException, DataAccessException,
			AuthenticationException, SQLException, NotAuthorizedException {

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(req.getEmail(),
				req.getPassword());

		Authentication auth = authenticationManager.authenticate(token);

		AccountDetails userDetails = (AccountDetails) auth.getPrincipal();

		List<String> roles = new ArrayList<String>();
		for (GrantedAuthority g : userDetails.getAuthorities()) {
			roles.add(g.getAuthority());
		}
		if (!roles.contains("ROLE_ADMIN"))
			throw new NotAuthorizedException("not an admin");

		final String returnToken = jwtUtil.generateToken(userDetails);
		return new LoginResponse(returnToken, userDetails.getId(), userDetails.getStartup(), userDetails.getInvestor());
	}
	/**
	 * use to refresh a valid token
	 * @param token
	 * @return {@link LoginRequest} 
	 * @throws NotAuthorizedException
	 */
	public LoginResponse refreshToken(String token) throws NotAuthorizedException {
		if (token == null) {
			throw new NotAuthorizedException("token not found");
		}
		token = token.substring(7);
		String username = jwtUtil.extractUsername(token);

		AccountDetails uDet = loadUserByUsername(username);
		return new LoginResponse(jwtUtil.generateToken(uDet), uDet.getId(), uDet.getStartup(), uDet.getInvestor());
	}
	/**
	 * makes a db request to evaluate if a user is a startup or an investor
	 * @param accountId
	 * @return true if account is a startup
	 */
	public boolean isStartup(long accountId) {
		try {
			return accountRepository.isStartup(accountId);
		} catch (DataAccessException | SQLException e) {
			return false;
		}
	}
	/**
	 * makes a db request to evaluate if a user is a investor or an investor
	 * @param accountId
	 * @return true if account is a investor
	 */
	public boolean isInvestor(long accountId) {
		try {
			return accountRepository.isInvestor(accountId);
		} catch (DataAccessException | SQLException e) {
			return false;
		}
	}
}
