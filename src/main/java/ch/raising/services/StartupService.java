package ch.raising.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ch.raising.models.*;
import ch.raising.utils.InValidProfileException;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.data.*;
import ch.raising.interfaces.IAdditionalInformationRepository;

@Service
public class StartupService extends AccountService {

	
	private StartupRepository startupRepository;
	private BoardmemberRepository bmemRepository;
	private FounderRepository founderRepository;
	private AssignmentTableRepository labelRepository;
	private PrivateShareholderRepository pshRepository;
	private CorporateShareholderRepository cshRepository;
	private AssignmentTableRepository investmentPhaseRepository;
	private AssignmentTableRepository investorTypeRepository;
	
	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			BoardmemberRepository bmemRepository,
			FounderRepository founderRepository, InvestorRepository investorRepository, MailUtil mailUtil,
			ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc, PrivateShareholderRepository pshRepository,
			CorporateShareholderRepository cshRepository, JwtUtil jwtUtil) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc, jwtUtil);

		this.investorTypeRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("investorType")
				.withAccountIdName("startupid").withRowMapper(MapUtil::mapRowToAssignmentTableWithDescription);
		this.labelRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("label")
				.withAccountIdName("startupid").withRowMapper(MapUtil::mapRowToAssignmentTableWithDescription);
		this.startupRepository = startupRepository;
		this.bmemRepository = bmemRepository;
		this.founderRepository = founderRepository;

		this.pshRepository = pshRepository;
		this.cshRepository = cshRepository;
	}

	@Override
	protected long registerAccount(Account account) throws Exception {
		Startup su = (Startup) account;

		if (su.isInComplete()) {
			throw new InValidProfileException("Profile is incomplete", su);
		} else if (accountRepository.emailExists(su.getEmail())) {
			throw new InValidProfileException("Email already exists");
		}

		long accountId = super.registerAccount(account);
		su.setAccountId(accountId);
		startupRepository.add(su);

		if (su.getBoardmembers() != null)
			su.getBoardmembers().forEach(bmem -> bmemRepository.addMemberByStartupId(bmem, accountId));
		if (su.getLabels() != null)
			su.getLabels().forEach(label -> labelRepository.addEntryToAccountById(label.getId(), accountId));
		if (su.getInvestorTypes() != null)
			su.getInvestorTypes().forEach(it -> investorTypeRepository.addEntryToAccountById(it.getId(), accountId));
		if (su.getFounders() != null)
			su.getFounders().forEach(founder -> founderRepository.addMemberByStartupId(founder, accountId));
		if (su.getPrivateShareholders() != null)
			su.getPrivateShareholders().forEach(ps -> pshRepository.addMemberByStartupId(ps, accountId));
		if (su.getCorporateShareholders() != null)
			su.getCorporateShareholders().forEach(cs -> cshRepository.addMemberByStartupId(cs, accountId));
		return accountId;
	}

	/**
	 * @param long the tableEntryId of the startup
	 * @returns Account a fully initialised Startup object
	 */
	@Override
	protected Account getAccount(long startupId) {

		List<AssignmentTableModel> invTypes = investorTypeRepository.findByAccountId(startupId);
		List<AssignmentTableModel> labels = labelRepository.findByAccountId(startupId);
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
	 */
	@Override
	protected void updateAccount(int id, Account acc) throws Exception {
		super.updateAccount(id, acc);
		Startup su = (Startup) acc;
		startupRepository.update(id, su);
	}

	/**
	 * Get matching profile of startup (the required information for matching)
	 * 
	 * @return Matching profile of startup
	 */
	public MatchingProfile getMatchingProfile(Startup startup) {
		if (startup == null)
			return null;

		MatchingProfile profile = new MatchingProfile();
		List<AssignmentTableModel> types = investorTypeRepository.findByAccountId(startup.getAccountId());
		List<AssignmentTableModel> continents = continentRepo.findByAccountId(startup.getAccountId());
		List<AssignmentTableModel> countries = countryRepo.findByAccountId(startup.getAccountId());
		List<AssignmentTableModel> industries = industryRepo.findByAccountId(startup.getAccountId());
		List<AssignmentTableModel> investmentPhases = investmentPhaseRepository.findByAccountId(startup.getAccountId());
		List<AssignmentTableModel> supports = supportRepo.findByAccountId(startup.getAccountId());

		profile.setAccountId(startup.getAccountId());
		profile.setName(startup.getCompanyName());
		profile.setDescription(startup.getDescription());
		profile.setInvestmentMax(startup.getTicketMaxId());
		profile.setInvestmentMin(startup.getTicketMinId());
		profile.setStartup(true);

		profile.setContinents(continents);
		profile.setInvestorTypes(types);
		profile.setCountries(countries);
		profile.setIndustries(industries);
		profile.setInvestmentPhases(investmentPhases);
		profile.setSupport(supports);

		return profile;
	}

	/**
	 * Get all matching profiles of all investors
	 * 
	 * @return List of matching profiles
	 */
	public List<MatchingProfile> getAllMatchingProfiles() {
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
