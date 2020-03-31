package ch.raising.services;

import java.util.ArrayList;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.ErrorResponse;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.Image;
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
import ch.raising.data.ImageRepository;

@Primary
@Service
public class AccountService implements UserDetailsService {
	
	protected AccountRepository accountRepository;
	private MailUtil mailUtil;
	private ResetCodeUtil resetCodeUtil;
	private JdbcTemplate jdbc;
	private JwtUtil jwtUtil;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private ImageRepository galleryRepository;
	private ImageRepository pPicRepository;
	@Autowired
    private AuthenticationManager authenticationManager ;
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
		this.galleryRepository = new ImageRepository(jdbc, "gallery");
		this.pPicRepository = new ImageRepository(jdbc, "profilepicture");
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
		} catch (DatabaseOperationException e) {
			rollback(account);
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (InValidProfileException e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage(), e.getAccount()));
		} catch (Exception e) {
			rollback(account);
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
		try {
			return login(new LoginRequest(account.getEmail(), account.getPassword()));
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	private void rollback(Account account) {
		try {
			Account found = accountRepository.findByEmail(account.getEmail());
			accountRepository.delete(found.getAccountId());
		} catch (Exception e) {}
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

		if (req.getGallery() != null)
			for (Image pic : req.getGallery()) {
				galleryRepository.addImageToAccount(pic, accountId);
			}
		if (req.getProfilePicture() != null)
			pPicRepository.addImageToAccount(req.getProfilePicture(), accountId);
		if (req.getCountries() != null)
			req.getCountries().forEach(country -> countryRepo.addEntryToAccountById(country.getId(), accountId));
		if (req.getContinents() != null)
			req.getContinents().forEach(continent -> continentRepo.addEntryToAccountById(continent.getId(), accountId));
		if (req.getSupport() != null)
			req.getSupport().forEach(sup -> supportRepo.addEntryToAccountById(sup.getId(), accountId));
		if (req.getIndustries() != null)
			req.getIndustries().forEach(ind -> industryRepo.addEntryToAccountById(ind.getId(), accountId));

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
			accountRepository.delete(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
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
		List<AssignmentTableModel> countries = countryRepo.findByAccountId(id);
		List<AssignmentTableModel> continents = continentRepo.findByAccountId(id);
		List<AssignmentTableModel> support = supportRepo.findByAccountId(id);
		List<AssignmentTableModel> industries = industryRepo.findByAccountId(id);
		List<Image> pPic = pPicRepository.findImagesByAccountId(id);
		List<Image> gallery = galleryRepository.findImagesByAccountId(id);
		Account acc = accountRepository.find(id);
		acc.setPassword("");
		acc.setRoles("");

		acc.setProfilePicture(pPic.get(0));
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
	 * Update user account, is called by the
	 * {@link ch.raising.controllers.StartupController},{@link ch.raising.controllersAccountController},{@link ch.raising.controllersInvestorController}
	 * 
	 * @param id      the tableEntryId of the account to be updated
	 * @param req     the http request instance
	 * @param isAdmin indicates whether or not the user requesting the update is
	 *                admin
	 * @return Response entity with status code and message
	 */
	public ResponseEntity<?> updateProfile(int id, Account acc) {
		try {
			updateAccount(id, acc);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * updates the account. should be overwritten and called by subtypes
	 * {@link InvestorService} and {@link StartupService}
	 * 
	 * @param id
	 * @param acc
	 * @return
	 * @throws Exception
	 */
	protected void updateAccount(int id, Account acc) throws Exception {
		accountRepository.update(id, acc);
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

	public ResponseEntity<?> addGalleryImageToAccountById(Image img) {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		if (accDet.getId() != img.getAccountId())
			return ResponseEntity.status(403)
					.body(new ErrorResponse("Not authorized to add picture to foreign account"));
		try {
			galleryRepository.addImageToAccount(img, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> deleteGalleryImageFromAccountById(long imgId) {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		try {
			galleryRepository.deleteImageFromAccount(imgId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> findGalleryImagesFromAccountById() {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		try {
			List<Image> images = galleryRepository.findImagesByAccountId(accDet.getId());
			return ResponseEntity.ok().body(images);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> addProfilePictureToAccountById(Image img) {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		long currentPPicId = pPicRepository.containsPictureOf(accDet.getId());
		if (accDet.getId() != img.getAccountId())
			return ResponseEntity.status(403)
					.body(new ErrorResponse("Not authorized to add profile picture to foreign account"));
		try {
			if (currentPPicId != 0)
				pPicRepository.deleteImageFromAccount(currentPPicId, accDet.getId());
			pPicRepository.addImageToAccount(img, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> deleteProfilePictureFromAccountById(long imgId) {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		try {
			pPicRepository.deleteImageFromAccount(imgId, accDet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> findProfilePictureFromAccountById() {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		try {
			List<Image> images = pPicRepository.findImagesByAccountId(accDet.getId());
			return ResponseEntity.ok().body(images);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> login(LoginRequest request) {
		UsernamePasswordAuthenticationToken unamePwToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
		try {
            authenticationManager.authenticate(unamePwToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Wrong username or password"));
        }
        final AccountDetails userDetails = loadUserByUsername(request.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token, userDetails.getId(), userDetails.getStartup(), userDetails.getInvestor()));
	}

}
