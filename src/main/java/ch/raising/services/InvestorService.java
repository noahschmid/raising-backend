package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;

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
import ch.raising.utils.ResetCodeUtil;

import ch.raising.models.MatchingProfile;
import ch.raising.models.responses.ErrorResponse;

@Service
public class InvestorService extends AccountService {

	private AssignmentTableRepository investmentPhaseRepository;

	private InvestorRepository investorRepository;

	private AssignmentTableRepository supportRepository;

	private AssignmentTableRepository continentRepository;

	private AssignmentTableRepository countryRepository;

	private AssignmentTableRepository industryRepository;

	private AssignmentTableRepository investorTypeRepository;

	@Autowired
	public InvestorService(AccountRepository accountRepository, InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc, JwtUtil jwtUtil, PasswordEncoder encoder,
			AssignmentTableRepositoryFactory atrFactory, MediaRepositoryFactory mrFactory) throws SQLException {
		super(accountRepository, mailUtil, resetCodeUtil, jwtUtil, encoder, atrFactory, mrFactory, jdbc);

		this.investmentPhaseRepository = atrFactory.getRepositoryForInvestor("investmentphase");
		this.investorRepository = investorRepository;
		this.countryRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("country")
				.withRowMapper(MapUtil::mapRowToCountry);
		this.continentRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("continent");
		this.supportRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("support");
		this.industryRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("industry");
		this.investorTypeRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("investortype");
	}

	@Override
	protected long registerAccount(Account requestInvestor)
			throws InvalidProfileException, DataAccessException, SQLException, DatabaseOperationException {

		Investor invReq = (Investor) requestInvestor;

		if (!invReq.isComplete()) {
			throw new InvalidProfileException("Profile is incomplete", invReq);
		}

		try {
			accountRepository.findByEmail(invReq.getEmail());
			throw new InvalidProfileException("Email already exists");
		} catch (EmailNotFoundException e) {
			long accountId = super.registerAccount(invReq);
			invReq.setAccountId(accountId);
			investorRepository.add(invReq);

			investmentPhaseRepository.addEntriesToAccount(accountId, invReq.getInvestmentPhases());

			return accountId;
		}
	}

	@Override
	public Investor getAccount(long id) throws DataAccessException, SQLException {

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
	protected void updateAccount(int id, Account acc) throws DataAccessException, SQLException {
		super.updateAccount(id, acc);
		Investor inv = (Investor) acc;
		investmentPhaseRepository.updateAssignment(id, inv.getInvestmentPhases());
		investorRepository.update(id, inv);
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
		AssignmentTableModel investorType = investorTypeRepository.find(investor.getInvestorTypeId());
		List<AssignmentTableModel> continents = continentRepository.findByAccountId(investor.getAccountId());
		List<AssignmentTableModel> countries = countryRepository.findByAccountId(investor.getAccountId());
		List<AssignmentTableModel> industries = industryRepository.findByAccountId(investor.getAccountId());
		List<AssignmentTableModel> investmentPhases = investmentPhaseRepository
				.findByAccountId(investor.getAccountId());
		List<AssignmentTableModel> supports = supportRepository.findByAccountId(investor.getAccountId());

		profile.setAccountId(investor.getAccountId());
		profile.setName(investor.getCompanyName());
		profile.setDescription(investor.getDescription());
		profile.setInvestmentMax(investor.getTicketMaxId());
		profile.setInvestmentMin(investor.getTicketMinId());
		profile.setStartup(false);

		profile.addInvestorType(investorType);

		profile.setContinents(continents);
		profile.setCountries(countries);
		profile.setIndustries(industries);
		profile.setInvestmentPhases(investmentPhases);
		profile.setSupport(supports);

		return profile;
	}
    /**
     * Get matching profile of investor (the required information for matching)
     * @return Matching profile of investor
     * @throws SQLException 
     * @throws DataAccessException 
     */
    public MatchingProfile getMatchingProfile(Investor investor) throws DataAccessException, SQLException {
        if(investor == null)
            return null;
        
		MatchingProfile profile = new MatchingProfile();

		List<AssignmentTableModel> continents = continentRepository.findByAccountId(investor.getAccountId());
        List<AssignmentTableModel> industries = industryRepository.findByAccountId(investor.getAccountId());
        List<AssignmentTableModel> investmentPhases = investmentPhaseRepository.findByAccountId(investor.getAccountId());
		List<AssignmentTableModel> supports = supportRepository.findByAccountId(investor.getAccountId());
		profile.addInvestorType(investorTypeRepository.find(investor.getInvestorTypeId()));
		
		countryRepository.findByAccountId(investor.getAccountId()).forEach(country -> {
			profile.addCountry((Country)country);
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
     * @return List of matching profiles
     * @throws SQLException 
     * @throws DataAccessException 
     */
    public List<MatchingProfile> getAllMatchingProfiles() throws DataAccessException, SQLException {
        List<Investor> investors = investorRepository.getAll();
        List<MatchingProfile> profiles = new ArrayList<>();
        if(investors.size() == 0)
            return null;

        for(Investor investor : investors) {
            profiles.add(getMatchingProfile(investor));
        }

        return profiles;
    }
}