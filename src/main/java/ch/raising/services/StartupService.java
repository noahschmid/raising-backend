package ch.raising.services;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.InvalidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.MediaException;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UidUtil;
import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.AssignmentTableRepositoryFactory;
import ch.raising.data.BoardmemberRepository;
import ch.raising.data.CorporateShareholderRepository;
import ch.raising.data.FounderRepository;
import ch.raising.data.MediaRepositoryFactory;
import ch.raising.data.PrivateShareholderRepository;
import ch.raising.data.SettingRepository;
import ch.raising.data.StartupRepository;
import ch.raising.models.Account;
import ch.raising.models.Boardmember;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Country;
import ch.raising.models.Founder;
import ch.raising.models.MatchingProfile;
import ch.raising.models.PrivateShareholder;
import ch.raising.models.Startup;

@Service
public class StartupService extends AccountService {

	private final StartupRepository startupRepository;
	private final BoardmemberRepository bmemRepository;
	private final FounderRepository founderRepository;
	private final AssignmentTableRepository labelRepository;
	private final PrivateShareholderRepository pshRepository;
	private final CorporateShareholderRepository cshRepository;
	private final AssignmentTableRepository investorTypeRepository;

	// private final MatchingService matchingService;

	private final AssignmentTableRepository countryRepository;
	private final AssignmentTableRepository continentRepository;
	private final AssignmentTableRepository industryRepository;
	private final AssignmentTableRepository supportRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			BoardmemberRepository bmemRepository, FounderRepository founderRepository, MailUtil mailUtil,
			ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc, PrivateShareholderRepository pshRepository,
			CorporateShareholderRepository cshRepository, JwtUtil jwtUtil, PasswordEncoder encoder,
			AssignmentTableRepositoryFactory atrFactory, MediaRepositoryFactory mrFactory,
			SettingRepository settingRepo) throws SQLException {

		super(accountRepository, mailUtil, resetCodeUtil, jwtUtil, encoder, atrFactory, mrFactory, jdbc, settingRepo);

		this.investorTypeRepository = atrFactory.getRepositoryForStartup("investortype");
		this.labelRepository = atrFactory.getRepositoryForStartup("label");

		this.startupRepository = startupRepository;
		this.bmemRepository = bmemRepository;
		this.founderRepository = founderRepository;
		this.pshRepository = pshRepository;
		this.cshRepository = cshRepository;
		this.countryRepository = atrFactory.getRepository("country").withRowMapper(MapUtil::mapRowToCountry);
		this.continentRepository = atrFactory.getRepository("continent");
		this.supportRepository = atrFactory.getRepository("support");
		this.industryRepository = atrFactory.getRepository("industry");

		// this.matchingService = matchingService;
	}

	@Override
	protected long registerAccount(Account account) throws InvalidProfileException, DataAccessException, SQLException,
			DatabaseOperationException, MediaException {
		Startup su = (Startup) account;

		if (!UidUtil.isValidUId(su.getUId())) {
			throw new InvalidProfileException("uid has invalid fromat: " + su.getUId(), su);
		}

		if (!su.isComplete()) {
			throw new InvalidProfileException("startup is incomplete", su);
		}
		long accountId = super.registerAccount(account);
		su.setAccountId(accountId);
		startupRepository.add(su);

		labelRepository.addEntriesToAccount(accountId, su.getLabels());
		investorTypeRepository.addEntriesToAccount(accountId, su.getInvestorTypes());

		if (su.getBoardmembers() != null) {
			for (Boardmember m : su.getBoardmembers()) {
				bmemRepository.addMemberByStartupId(m, accountId);
			}
		}
		if (su.getFounders() != null) {
			for (Founder f : su.getFounders()) {
				founderRepository.addMemberByStartupId(f, accountId);
			}
		}
		if (su.getPrivateShareholders() != null) {
			for (PrivateShareholder p : su.getPrivateShareholders()) {
				pshRepository.addMemberByStartupId(p, accountId);
			}
		}
		if (su.getCorporateShareholders() != null) {
			for (CorporateShareholder c : su.getCorporateShareholders()) {
				cshRepository.addMemberByStartupId(c, accountId);
			}
		}

		return accountId;

	}

	/**
	 * @param long the tableEntryId of the startup
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 * @returns Account a fully initialised Startup object
	 */
	@Override
	public Account getAccount(long startupId) throws DataAccessException, SQLException, DatabaseOperationException {

		List<Long> invTypes = investorTypeRepository.findIdByAccountId(startupId);
		List<Long> labels = labelRepository.findIdByAccountId(startupId);
		List<Founder> founders = founderRepository.findByStartupId(startupId);
		List<PrivateShareholder> psh = pshRepository.findByStartupId(startupId);
		List<CorporateShareholder> csh = cshRepository.findByStartupId(startupId);
		List<Boardmember> bmems = bmemRepository.findByStartupId(startupId);

		Account acc = super.getAccount(startupId);
		Startup su = startupRepository.find(startupId);

		return new Startup(acc, su, invTypes, labels, founders, psh, csh, bmems);
	}

	/**
	 * Update startup profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@Override
	public void updateAccount(int id, Account acc) throws DataAccessException, SQLException {
		super.updateAccount(id, acc);
		Startup su = (Startup) acc;
		investorTypeRepository.updateAssignment(id, su.getInvestorTypes());
		labelRepository.updateAssignment(id, su.getLabels());
		startupRepository.update(id, su);

		// matchingService.match(id, true);
	}

	/**
	 * Get matching profile of startup (the required information for matching)
	 * 
	 * @return Matching profile of startup
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public MatchingProfile getMatchingProfile(Startup startup) throws DataAccessException, SQLException {
		if (startup == null)
			return null;

		MatchingProfile profile = new MatchingProfile();
		List<Long> types = investorTypeRepository.findIdByAccountId(startup.getAccountId());
		List<Long> continents = continentRepository.findIdByAccountId(startup.getAccountId());
		countryRepository.findByAccountId(startup.getAccountId()).forEach(country -> {
			profile.addCountry((Country) country);
		});
		;
		List<Long> industries = industryRepository.findIdByAccountId(startup.getAccountId());
		List<Long> supports = supportRepository.findIdByAccountId(startup.getAccountId());

		profile.setAccountId(startup.getAccountId());
		profile.setName(startup.getCompanyName());
		profile.setDescription(startup.getDescription());
		profile.setInvestmentMax(startup.getTicketMaxId());
		profile.setInvestmentMin(startup.getTicketMinId());
		profile.setStartup(true);

		profile.setContinents(continents);
		profile.setInvestorTypes(types);
		profile.setIndustries(industries);
		profile.setSupport(supports);
		profile.addInvestmentPhase(startup.getInvestmentPhaseId());

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
		List<Startup> startups = startupRepository.getAll();
		List<MatchingProfile> profiles = new ArrayList<>();
		if (startups.size() == 0)
			return null;

		for (Startup startup : startups) {
			profiles.add(getMatchingProfile(startup));
		}

		return profiles;
	}

}
