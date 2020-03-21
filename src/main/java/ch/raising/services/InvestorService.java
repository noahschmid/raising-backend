package ch.raising.services;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.ErrorResponse;

import ch.raising.models.Investor;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.ResetCodeUtil;

import ch.raising.models.MatchingProfile;
@Service
public class InvestorService extends AccountService {

	private AssignmentTableRepository investmentPhaseRepository;

	@Autowired
	private InvestorRepository investorRepository;

	private AssignmentTableRepository supportRepository;

	private AssignmentTableRepository continentRepository;

	private AssignmentTableRepository countryRepository;

	private AssignmentTableRepository industryRepository;

	private AssignmentTableRepository investorTypeRepository;

	@Autowired

	public InvestorService(AccountRepository accountRepository, InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc) {
		super(accountRepository, mailUtil, resetCodeUtil, jdbc);

		this.investmentPhaseRepository = new AssignmentTableRepository(jdbc, "investmentphase");
		this.investorRepository = investorRepository;
		this.supportRepository = new AssignmentTableRepository(jdbc, "support");
		this.continentRepository = new AssignmentTableRepository(jdbc, "continent");
		this.countryRepository = new AssignmentTableRepository(jdbc, "country", MapUtil::mapRowToCountry);
		this.industryRepository = new AssignmentTableRepository(jdbc, "industry");
	}

	@Override
	protected long registerAccount(Account requestInvestor) throws Exception {

		Investor invReq = (Investor) requestInvestor;

		checkRequestValid(invReq);

		long accountId = super.registerAccount(invReq);
		Investor inv = Investor.investorBuilder().accountId(accountId).company(invReq.getCompany())
				.investorTypeId(invReq.getInvestorTypeId()).build();

		investorRepository.add(inv);
		invReq.getInvPhases()
				.forEach(phase -> investmentPhaseRepository.addEntryToAccountById(accountId, phase.getId()));

		return accountId;
	}

	@Override
	protected Investor getAccount(long id) {

		Account acc = super.getAccount(id);
		List<IAssignmentTableModel> invPhase = investmentPhaseRepository.findByAccountId(id);
		Investor inv = investorRepository.find(id);

		return new Investor(acc, inv, invPhase);
	}

	/**
	 * Update investor profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 */
	public ResponseEntity<?> updateInvestor(int id, Investor request) {
		try {
			investorRepository.update(id, request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}


    /**
     * Get matching profile of investor (the required information for matching)
     * @return Matching profile of investor
     */
    public MatchingProfile getMatchingProfile(Investor investor) {
        if(investor == null)
            return null;
        
        MatchingProfile profile = new MatchingProfile();
        IAssignmentTableModel investorType = investorTypeRepository.find(investor.getInvestorTypeId());
        List<IAssignmentTableModel> continents = continentRepository.findByAccountId(investor.getAccountId());
        List<IAssignmentTableModel> countries = countryRepository.findByAccountId(investor.getAccountId());
        List<IAssignmentTableModel> industries = industryRepository.findByAccountId(investor.getAccountId());
        List<IAssignmentTableModel> investmentPhases = investmentPhaseRepository.findByAccountId(investor.getAccountId());
        List<IAssignmentTableModel> supports = supportRepository.findByAccountId(investor.getAccountId());

        profile.setAccountId(investor.getAccountId());
        profile.setName(investor.getName());
        profile.setDescription(investor.getDescription());
        profile.setInvestmentMax(investor.getInvestmentMax());
        profile.setInvestmentMin(investor.getInvestmentMin());
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
     * Get all matching profiles of all investors
     * @return List of matching profiles
     */
    public List<MatchingProfile> getAllMatchingProfiles() {
        List<Investor> investors = investorRepository.getAll();
        List<MatchingProfile> profiles = new ArrayList<>();
        if(investors.size() == 0)
            return null;

        for(Investor investor : investors) {
            profiles.add(getMatchingProfile(investor));
        }

        return profiles;
	}

	public ResponseEntity<?> addInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.addEntryToAccountById(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	public ResponseEntity<?> deleteInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.deleteEntryFromAccountById(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}