package ch.raising.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.catalina.valves.ErrorReportValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.models.*;
import ch.raising.utils.UpdateQueryBuilder;
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
	private ContactRepository contactRepository;
	
	@Autowired
	private BoardmemberRepository bmemRepository;
	
	@Autowired
	private FounderRepository founderRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			InvestmentPhaseRepository investmentPhaseRepository, InvestorTypeRepository investorTypeRepository,
			IndustryRepository industryRepository, SupportRepository supportRepository,
			ContinentRepository continentRepository, CountryRepository countryRepository, ContactRepository contactRepository,
			BoardmemberRepository bmemRepository, FounderRepository founderRepository) {
		this.accountRepository = accountRepository;
		this.countryRepository = countryRepository;
		this.continentRepository = continentRepository;
		this.supportRepository = supportRepository;
		this.investmentPhaseRepository = investmentPhaseRepository;
		this.industryRepository = industryRepository;
		this.investorTypeRepository = investorTypeRepository;
		this.startupRepository = startupRepository;
		this.contactRepository = contactRepository;
		this.bmemRepository = bmemRepository;
		this.founderRepository = founderRepository;
	}

	/**
	 * Get full profile of startup
	 * 
	 * @param id the id of the startup
	 * @return response entity with status code and full startup profile
	 */
	public ResponseEntity<?> getStartupProfile(int id) {
		Startup startup = startupRepository.find(id);
		if (startup == null)
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

		for (InvestorType type : types) {
			response.addInvestorType(type);
		}

		for (Continent cntnt : continents) {
			response.addContinent(cntnt);
		}

		for (Country cntry : countries) {
			response.addCountry(cntry);
		}

		for (Industry ind : industries) {
			response.addIndustry(ind);
		}

		response.setInvestmentPhase(investmentPhase);

		for (Support spprt : supports) {
			response.addSupport(spprt);
		}

		return ResponseEntity.ok().body(response);
	}

	/**
	 * Add new investor profile
	 * 
	 * @param investor the investor to add
	 */
	public ResponseEntity<?> addStartup(Startup startup) {
		if (isIncomplete(startup))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorResponse("Please fill out all required fields"));
		try {
			startupRepository.add(startup);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
		}
	}

	private boolean isIncomplete(Startup startup) {
		return startup.getAccountId() == -1 || startup.getStreet() == null || startup.getInvestmentMax() == -1
				|| startup.getInvestmentMin() == -1 || startup.getWebsite() == null || startup.getZipCode() == null
				|| startup.getNumberOfFTE() == -1 || startup.getTurnover() == -1 || startup.getBreakEvenYear() == -1
				|| startup.getName() == null || startup.getInvestmentPhaseId() == -1;
	}

	/**
	 * Update startup profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 */
	public ResponseEntity<?> updateStartup(int id, StartupUpdateRequest request) {

		try {
			startupRepository.update(id, request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}

	}
	
	/**
	 * Deletes the contact specified by id
	 * @param id
	 * @return
	 */

	public ResponseEntity<?> deleteContact(int id) {
		try {
			if(!belongsToStartup(id, contactRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			contactRepository.deleteContactById(id);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new contact
	 * @param contact to be inserted in the DB
	 * @return a response with id and maybe body
	 */
	
	public ResponseEntity<?> addContact(Contact contact){
		try {
			contactRepository.addContact(contact);
			return ResponseEntity.ok().build();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Deletes a Boardmember
	 * @param id of the boardmember to be deleted
	 * @return response with code and optional body
	 */
	
	public ResponseEntity<?> deleteBoardmember(int id){
		try {
			if(!belongsToStartup(id, bmemRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			bmemRepository.deleteBoardMember(id);	
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new boardmember
	 * @param bMem a new Boardmember
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addBoardmember(Boardmember bMem) {
		try {
			bmemRepository.addBoardMember(bMem);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Deletes a Founder
	 * @param id of the founder to be deleted
	 * @return response with code and optional body
	 */
	
	public ResponseEntity<?> deleteFounder(int id){
		try {
			if(!belongsToStartup(id, founderRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			founderRepository.deleteFounder(id);	
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new founder
	 * @param bMem a new founder
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addFounder(Founder founder) {
		try {
			founderRepository.addFounder(founder);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Checks if a requesting startup belongs to the StartupMember 
	 * @param sideTableEntryId
	 * @param srp The sidetable repository that should check if the startup belongs to the sidetable entry with given id
	 * @return
	 */
	private boolean belongsToStartup(int sideTableEntryId, IAdditionalInformationRepository<?, UpdateQueryBuilder> srp) {
		AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return accdetails.getId() == srp.getStartupIdOfTableById(sideTableEntryId);
	}
}
