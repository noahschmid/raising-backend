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
	private LabelRepository labelRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			InvestmentPhaseRepository investmentPhaseRepository, InvestorTypeRepository investorTypeRepository,
			IndustryRepository industryRepository, SupportRepository supportRepository,
			ContinentRepository continentRepository, CountryRepository countryRepository, ContactRepository contactRepository,
			BoardmemberRepository bmemRepository, FounderRepository founderRepository, LabelRepository labelRepository) {
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
		this.labelRepository = labelRepository;
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

		List<InvestorType> types = investorTypeRepository.findByStartupId(startup.getAccountId());

		List<Continent> continents = continentRepository.findByAccountId(startup.getAccountId());
		List<Country> countries = countryRepository.findByAccountId(startup.getAccountId());

		List<Industry> industries = industryRepository.findByAccountId(startup.getAccountId());

		InvestmentPhase investmentPhase = investmentPhaseRepository.find(startup.getInvestmentPhaseId());
		List<Support> supports = supportRepository.findByAccountId(startup.getAccountId());

		response.setAccount(account);
		response.setInvestmentMax(startup.getInvestmentMax());
		response.setInvestmentMin(startup.getInvestmentMin());
		response.setName(startup.getName());
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
	public ResponseEntity<?> updateStartup(long id, StartupUpdateRequest request) {

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

	public ResponseEntity<?> deleteContactByStartupId(long id) {
		try {
			if(!belongsToStartup(id, contactRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			contactRepository.deleteContactByIdByStartupId(id);
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
	
	public ResponseEntity<?> addContactByStartupId(Contact contact){
		try {
			contactRepository.addContactByStartupId(contact);
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
	
	public ResponseEntity<?> deleteBoardmemberByStartupId(long id){
		try {
			if(!belongsToStartup(id, bmemRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			bmemRepository.deleteBoardMemberByStartupId(id);	
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

	public ResponseEntity<?> addBoardmemberByStartupId(Boardmember bMem) {
		try {
			bmemRepository.addBoardMemberByStartupId(bMem);
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
	
	public ResponseEntity<?> deleteFounderByStartupId(long id){
		try {
			if(!belongsToStartup(id, founderRepository)) {
				return ResponseEntity.status(403).body(new ErrorResponse("this contact does not belong to that startup"));
			}
			founderRepository.deleteFounderByStartupId(id);	
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

	public ResponseEntity<?> addFounderByStartupId(Founder founder) {
		try {
			founderRepository.addFounderByStartupId(founder);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Deletes the entry in labelassignment with the specified labelId and the id in tokens.
	 * @param labelId
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteLabelByStartupId(long labelId){
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			labelRepository.deleteLabelOfStartup(labelId, accdet.getId());
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Adds a new entry in the labelassignment repository
	 * @param labelId of the label a new founder
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> addLabelByStartupId(long labelId) {
		try {
			AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			investorTypeRepository.addInvestorTypeToStartup(labelId, accdetails.getId());
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Adds a new entry in the investortypeassignment repository
	 * @param investortyped of the label a new founder
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addInvestorTypeByStartupId(long invTypeId) {
		try {
			AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			investorTypeRepository.addInvestorTypeToStartup(invTypeId, accdetails.getId());
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * Deletes the entry in investortypeassignment with the specified labelId and the id in tokens.
	 * @param investortypeid
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteInvestorTypeByStartupId(long invTypeId){
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			investorTypeRepository.deleteInvestorTypeOfStartupId(invTypeId, accdet.getId());
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Checks if a requesting startup belongs to the StartupMember 
	 * @param sideTableEntryId
	 * @param addinfRepo The sidetable repository that should check if the startup belongs to the sidetable entry with given id
	 * @return
	 */
	private boolean belongsToStartup(long sideTableEntryId, IAdditionalInformationRepository<?, UpdateQueryBuilder> addinfRepo) {
		AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return accdetails.getId() == addinfRepo.getStartupIdOfTableById(sideTableEntryId);
	}
}
