package ch.raising.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.StartupRepository;
import ch.raising.models.*;
import ch.raising.data.*;

@Service
public class StartupService {
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
    private StartupRepository startupRepository;
	
	@Autowired
	public StartupService(AccountRepository accountRepository,
	StartupRepository startupRepository,
	InvestmentPhaseRepository investmentPhaseRepository,
	InvestorTypeRepository investorTypeRepository,
	IndustryRepository industryRepository,
	SupportRepository supportRepository,
	ContinentRepository continentRepository,
	CountryRepository countryRepository){
		this.accountRepository = accountRepository;
        this.countryRepository = countryRepository;
        this.continentRepository = continentRepository;
        this.supportRepository = supportRepository;
        this.investmentPhaseRepository = investmentPhaseRepository;
        this.industryRepository = industryRepository;
        this.investorTypeRepository = investorTypeRepository;
        this.startupRepository = startupRepository;
	}
	
	/**
     * Get full profile of startup
     * @param id the id of the startup
     * @return response entity with status code and full startup profile
     */
    public ResponseEntity<?> getStartupProfile(int id) {
        Startup startup = startupRepository.find(id);
        if(startup == null)
            return ResponseEntity.status(404).body(new ErrorResponse("Startup not found"));

			StartupProfileResponse response = new StartupProfileResponse();
			Account account = accountRepository.find(startup.getAccountId());
        
        List<InvestorType> types = investorTypeRepository.findByStartupId(startup.getId());
        
        List<Continent> continents = continentRepository.findByAccountId(startup.getAccountId());
        List<Country> countries = countryRepository.findByAccountId(startup.getAccountId());
		
		List<Industry> industries = industryRepository.findByAccountId(startup.getAccountId());
		
		InvestmentPhase investmentPhase = investmentPhaseRepository.find(startup.getInvestmentPhaseId());
        List<Support> supports = supportRepository.findByAccountId(startup.getAccountId());

		response.setAccount(account);
        response.setInvestmentMax(startup.getInvestmentMax());
        response.setInvestmentMin(startup.getInvestmentMin());
        response.setName(startup.getName());
		response.setId(startup.getId());
		response.setCity(startup.getCity());
		response.setZipCode(startup.getZipCode());
		response.setStreet(startup.getStreet());
		response.setWebsite(startup.getWebsite());
		
		for(InvestorType type : types) {
            response.addInvestorType(type);
        }

        for(Continent cntnt : continents) {
            response.addContinent(cntnt);
		}

        for(Country cntry : countries) {
            response.addCountry(cntry);
        }

        for(Industry ind : industries) {
            response.addIndustry(ind);
        }

        response.setInvestmentPhase(investmentPhase);

        for(Support spprt : supports) {
            response.addSupport(spprt);
        }

        return ResponseEntity.ok().body(response);
	}
	
	    /**
     * Add new investor profile
     * @param investor the investor to add
     */
    public ResponseEntity<?> addStartup(Startup startup) {
        if(startup.getAccountId() == -1 || startup.getStreet () == null || 
		startup.getInvestmentMax() == -1 || startup.getInvestmentMin() == -1 || 
		startup.getWebsite() == null || startup.getZipCode() == null || 
		startup.getNumberOfFTE() == -1 || startup.getTurnover() == -1 || startup.getBreakEvenYear() == -1 ||
		startup.getName() == null || startup.getInvestmentPhaseId() == -1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Please fill out all required fields"));
        try {
            startupRepository.add(startup);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
        }
    }
}
