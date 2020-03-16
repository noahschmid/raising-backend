package ch.raising.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.ContinentRepository;
import ch.raising.data.CountryRepository;
import ch.raising.data.InvestmentPhaseRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.data.InvestorTypeRepository;
import ch.raising.data.SupportRepository;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.Continent;
import ch.raising.models.Country;
import ch.raising.models.ErrorResponse;
import ch.raising.models.Industry;
import ch.raising.models.InvestmentPhase;
import ch.raising.models.Investor;
import ch.raising.models.InvestorProfileResponse;
import ch.raising.models.InvestorUpdateRequest;
import ch.raising.models.MatchingProfile;
import ch.raising.models.InvestorType;
import ch.raising.models.Support;
import ch.raising.data.IndustryRepository;

@Service
public class InvestorService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InvestorTypeRepository investorTypeRepository;

    @Autowired
    private InvestmentPhaseRepository investmentPhaseRepository;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private ContinentRepository continentRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired 
    private IndustryRepository industryRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    public InvestorService(
        AccountRepository accountRepository,
        InvestmentPhaseRepository investmentPhaseRepository,
        InvestorTypeRepository investorTypeRepository,
        IndustryRepository industryRepository,
        SupportRepository supportRepository,
        ContinentRepository continentRepository,
        CountryRepository countryRepository,
        InvestorRepository investorRepository
        ) {
        this.accountRepository = accountRepository;
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
        this.supportRepository = supportRepository;
        this.investmentPhaseRepository = investmentPhaseRepository;
        this.industryRepository = industryRepository;
        this.investorTypeRepository = investorTypeRepository;
        this.investorRepository = investorRepository;
    }

    /**
     * Get full profile of investor
     * @param id the id of the investor
     * @return response entity with status code and full investor profile
     */
    public ResponseEntity<?> getInvestorProfile(int id) {
        Investor investor = investorRepository.find(id);
        if(investor == null)
            return ResponseEntity.status(404).body(new ErrorResponse("Investor not found"));

        InvestorProfileResponse response = new InvestorProfileResponse();
        InvestorType type = investorTypeRepository.find(investor.getInvestorTypeId());
        Account account = accountRepository.find(investor.getAccountId());
        List<Continent> continents = continentRepository.findByAccountId(investor.getAccountId());
        List<Country> countries = countryRepository.findByAccountId(investor.getAccountId());
        List<Industry> industries = industryRepository.findByAccountId(investor.getAccountId());
        List<InvestmentPhase> investmentPhases = investmentPhaseRepository.findByInvestorId(investor.getAccountId());
        List<Support> supports = supportRepository.findByAccountId(investor.getAccountId());
 
        response.setAccount(account);
        response.setInvestorType(type.getName());
        response.setUsername(account.getUsername());
        response.setInvestmentMax(investor.getInvestmentMax());
        response.setInvestmentMin(investor.getInvestmentMin());
        response.setName(investor.getName());
        response.setDescription(investor.getDescription());

        for(Continent cntnt : continents) {
            response.addContinent(cntnt);
        }

        for(Country cntry : countries) {
            response.addCountry(cntry);
        }

        for(Industry ind : industries) {
            response.addIndustry(ind);
        }

        for(InvestmentPhase invPhs : investmentPhases) {
            response.addInvestmentPhase(invPhs);
        }

        for(Support spprt : supports) {
            response.addSupport(spprt);
        }

        return ResponseEntity.ok().body(response);
    }

    /**
     * Update investor profile
     * @param request the data to update
     * @return response entity with status code and message
     */
    public ResponseEntity<?> updateInvestor(int id, InvestorUpdateRequest request) {
        try {
            investorRepository.update(id, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Add new investor profile
     * @param investor the investor to add
     */
    public ResponseEntity<?> addInvestor(Investor investor) {
        if(isIncomplete(investor))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Please fill out all required fields"));
        try {
            investorRepository.add(investor);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
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
        InvestorType type = investorTypeRepository.find(investor.getInvestorTypeId());
        List<Continent> continents = continentRepository.findByAccountId(investor.getAccountId());
        List<Country> countries = countryRepository.findByAccountId(investor.getAccountId());
        List<Industry> industries = industryRepository.findByAccountId(investor.getAccountId());
        List<InvestmentPhase> investmentPhases = investmentPhaseRepository.findByInvestorId(investor.getAccountId());
        List<Support> supports = supportRepository.findByAccountId(investor.getAccountId());

        profile.setAccountId(investor.getAccountId());
        profile.setName(investor.getName());
        profile.setDescription(investor.getDescription());
        profile.setInvestmentMax(investor.getInvestmentMax());
        profile.setInvestmentMin(investor.getInvestmentMin());
        profile.setStartup(false);

        profile.addInvestorType(type);

        for(Continent cntnt : continents) {
            profile.addContinent(cntnt);
        }

        for(Country cntry : countries) {
            profile.addCountry(cntry);
        }

        for(Industry ind : industries) {
            profile.addIndustry(ind);
        }

        for(InvestmentPhase invPhs : investmentPhases) {
            profile.addInvestmentPhase(invPhs);
        }

        for(Support spprt : supports) {
            profile.addSupport(spprt);
        }

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

	private boolean isIncomplete(Investor investor) {
		return investor.getAccountId() == -1 || investor.getDescription() == null || 
            investor.getInvestmentMax() == -1 || investor.getInvestmentMin() == -1 || 
            investor.getInvestorTypeId() == -1 || investor.getName() == null;
	}

	public ResponseEntity<?> addInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			investmentPhaseRepository.addInvestmentPhaseToInvestor(accdet.getId(), id);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	public ResponseEntity<?> deleteInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			investmentPhaseRepository.deleteInvestmentPhaseOfInvestor(accdet.getId(), id);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}