package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.AssignmentTableRepositoryFactory;
import ch.raising.data.InvestorRepository;
import ch.raising.data.MediaRepositoryFactory;
import ch.raising.data.SettingRepository;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.Investor;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.MediaException;
import ch.raising.utils.NotAuthorizedException;
import ch.raising.utils.ResetCodeUtil;

import ch.raising.models.MatchingProfile;
import ch.raising.models.Settings;
import ch.raising.models.enums.NotificationType;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.LoginResponse;
/**
 * service that handles interaction specific functions
 * @see AccountService
 * @author noahs, manus
 *
 */
@Service
public class InvestorService extends AccountService {

	private AssignmentTableRepository investmentPhaseRepository;

	private InvestorRepository investorRepository;

	private AssignmentTableRepository supportRepository;

	private AssignmentTableRepository continentRepository;

	private AssignmentTableRepository countryRepository;

	private AssignmentTableRepository industryRepository;


	@Autowired
	public InvestorService(AccountRepository accountRepository, InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc, JwtUtil jwtUtil, PasswordEncoder encoder,
			AssignmentTableRepositoryFactory atrFactory, MediaRepositoryFactory mrFactory,
			SettingRepository settingRepo) throws SQLException {
		super(accountRepository, mailUtil, resetCodeUtil, jwtUtil, encoder, atrFactory, mrFactory, jdbc, settingRepo);

		this.investmentPhaseRepository = atrFactory.getRepositoryForInvestor("investmentphase");
		this.investorRepository = investorRepository;
		this.countryRepository = atrFactory.getRepository("country").withRowMapper(MapUtil::mapRowToCountry);
		this.continentRepository = atrFactory.getRepository("continent");
		this.supportRepository = atrFactory.getRepository("support");
		this.industryRepository = atrFactory.getRepository("industry");
	}
	
	@Override
	protected long registerAccount(Account requestInvestor) throws InvalidProfileException, DataAccessException,
			SQLException, MediaException, DatabaseOperationException {

		Investor invReq = (Investor) requestInvestor;

		if (!invReq.isComplete()) {
			throw new InvalidProfileException("Profile is incomplete", invReq);
		}
		long accountId = super.registerAccount(invReq);
		invReq.setAccountId(accountId);
		investorRepository.add(invReq);
		investmentPhaseRepository.addEntriesToAccount(accountId, invReq.getInvestmentPhases());
		return accountId;

	}

	@Override
	public Investor getAccount(long id) throws DataAccessException, SQLException, DatabaseOperationException {
		Account acc = super.getAccount(id);
		List<Long> invPhase = investmentPhaseRepository.findIdByAccountId(id);
		Investor inv = investorRepository.find(id);
		return new Investor(acc, inv, invPhase);
	}

	/**
	 * Update investor profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws Exception
	 */
	@Override
	public LoginResponse updateAccount(int id, Account acc, String token) throws DataAccessException, SQLException, 
		NotAuthorizedException {
		LoginResponse response = super.updateAccount(id, acc, token);
		Investor inv = (Investor) acc;
		investmentPhaseRepository.updateAssignment(id, inv.getInvestmentPhases());
		investorRepository.update(id, inv);

		return response;
	}

	/**
	 * Get matching profile of investor (the required information for matching)
	 * 
	 * @return Matching profile of investor
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public MatchingProfile getMatchingProfile(Investor investor) throws DataAccessException, SQLException {
		if (investor == null)
			return null;

		MatchingProfile profile = new MatchingProfile();

		List<Long> continents = continentRepository.findIdByAccountId(investor.getAccountId());
		List<Long> industries = industryRepository.findIdByAccountId(investor.getAccountId());
		List<Long> investmentPhases = investmentPhaseRepository.findIdByAccountId(investor.getAccountId());
		List<Long> supports = supportRepository.findIdByAccountId(investor.getAccountId());
		profile.addInvestorType(investor.getInvestorTypeId());

		countryRepository.findByAccountId(investor.getAccountId()).forEach(country -> {
			profile.addCountry((Country) country);
		});

		profile.setAccountId(investor.getAccountId());
		profile.setName(investor.getCompanyName());
		profile.setDescription(investor.getDescription());
		profile.setInvestmentMax(investor.getTicketMaxId());
		profile.setInvestmentMin(investor.getTicketMinId());
		profile.setStartup(false);

		profile.setContinents(continents);
		profile.setIndustries(industries);
		profile.setInvestmentPhases(investmentPhases);
		profile.setSupport(supports);

		return profile;
	}

	/**
	 * Get all matching profiles of all investors
	 * 
	 * @return List of matching profiles
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public List<MatchingProfile> getAllMatchingProfiles() throws DataAccessException, SQLException {
		List<Investor> investors = investorRepository.getAll();
		List<MatchingProfile> profiles = new ArrayList<>();
		if (investors.size() == 0)
			return null;

		for (Investor investor : investors) {
			profiles.add(getMatchingProfile(investor));
		}

		return profiles;
	}
}