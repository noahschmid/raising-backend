package ch.raising.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.ContinentRepository;
import ch.raising.data.CountryRepository;
import ch.raising.data.InvestmentPhaseRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.data.InvestorTypeRepository;
import ch.raising.data.SupportRepository;
import ch.raising.models.Account;
import ch.raising.models.Continent;
import ch.raising.models.Country;
import ch.raising.models.Industry;
import ch.raising.models.InvestmentPhase;
import ch.raising.models.Investor;
import ch.raising.models.InvestorProfileResponse;
import ch.raising.models.InvestorProfileUpdateRequest;
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
     * @param id
     * @return
     */
    public ResponseEntity<?> getInvestorProfile(int id) {
        Investor investor = investorRepository.find(id);
        if(investor == null)
            return ResponseEntity.status(404).build();

        InvestorProfileResponse response = new InvestorProfileResponse();
        InvestorType type = investorTypeRepository.find(investor.getInvestorTypeId());
        Account account = accountRepository.find(investor.getAccountId());
        List<Continent> continents = continentRepository.findByAccountId(investor.getAccountId());
        List<Country> countries = countryRepository.findByAccountId(investor.getAccountId());
        List<Industry> industries = industryRepository.findByAccountId(investor.getAccountId());
        List<InvestmentPhase> investmentPhases = investmentPhaseRepository.findByInvestorId(investor.getId());
        List<Support> supports = supportRepository.findByAccountId(investor.getAccountId());

        response.setAccountId(investor.getAccountId());
        response.setInvestorType(type.getName());
        response.setUsername(account.getUsername());
        response.setInvestmentMax(investor.getInvestmentMax());
        response.setInvestmentMin(investor.getInvestmentMin());
        response.setName(investor.getName());
        response.setDescription(investor.getDescription());

        for(Continent cntnt : continents) {
            response.addContinent(cntnt.getName());
        }

        for(Country cntry : countries) {
            response.addCountry(cntry.getName());
        }

        for(Industry ind : industries) {
            response.addIndustry(ind.getName());
        }

        for(InvestmentPhase invPhs : investmentPhases) {
            response.addInvestmentPhase(invPhs.getName());
        }

        for(Support spprt : supports) {
            response.addSupport(spprt.getName());
        }

        return ResponseEntity.ok().body(response);
    }

    /**
     * Update investor profile
     * @param request the data to update
     * @return response entity with status code and message
     */
    public ResponseEntity<?> updateInvestorProfile(InvestorProfileUpdateRequest request) {
        return ResponseEntity.ok().build();
    }
}