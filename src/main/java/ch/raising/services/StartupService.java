package ch.raising.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.models.*;
import ch.raising.utils.MailUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.utils.UpdateQueryBuilder;
import ch.raising.data.*;

@Service
public class StartupService extends AccountService {
	@Autowired
	private InvestorTypeRepository investorTypeRepository;

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
	private PrivateShareholderRepository pshRepository;
	
	@Autowired
	private CorporateShareholderRepository cshRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private IndustryRepository industryRepository;

	@Autowired
	private SupportRepository supportRepository;

	@Autowired
	private ContinentRepository continentRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private InvestmentPhaseRepository investmentPhaseRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			InvestorTypeRepository investorTypeRepository, ContactRepository contactRepository,
			BoardmemberRepository bmemRepository, FounderRepository founderRepository, LabelRepository labelRepository,
			IndustryRepository industryRepository, SupportRepository supportRepository,
			ContinentRepository continentRepository, CountryRepository countryRepository,
			InvestorRepository investorRepository, MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc,
			PrivateShareholderRepository pshRepository, CorporateShareholderRepository cshRepository) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc, countryRepository, continentRepository,
				supportRepository, industryRepository);

		this.investorTypeRepository = investorTypeRepository;
		this.startupRepository = startupRepository;
		this.contactRepository = contactRepository;
		this.bmemRepository = bmemRepository;
		this.founderRepository = founderRepository;
		this.labelRepository = labelRepository;
		this.accountRepository = accountRepository;
		this.supportRepository = supportRepository;
		this.continentRepository = continentRepository;
		this.countryRepository = countryRepository;
		this.pshRepository = pshRepository;
		this.cshRepository = cshRepository;
	}

	@Override
	protected long registerAccount(Account account) throws Exception {
		Startup suReq = (Startup) account;
		
		checkRequestValid(account);
		
		long accountId = super.registerAccount(account);
		Startup su = Startup.startupBuilder().accountId(accountId).investmentPhaseId(suReq.getInvestmentPhaseId()).street(suReq.getStreet())
				.city(suReq.getCity()).zipCode(suReq.getZipCode()).website(suReq.getWebsite()).breakEvenYear(suReq.getBreakEvenYear())
				.numberOfFTE(suReq.getNumberOfFTE()).turnover(suReq.getTurnover()).build();
		
		
		startupRepository.add(su);
		
		contactRepository.addMemberByStartupId(suReq.getContact(), accountId);
		
		suReq.getLabels().forEach(label -> labelRepository.addLabelToStartup(label.getId(), accountId));
		suReq.getInvTypes().forEach(it -> investorTypeRepository.addInvestorTypeToStartup(accountId, it.getId()));
		suReq.getFounders().forEach(founder -> founderRepository.addByStartupId(founder, accountId));
		
		return accountId;
	}

	@Override
	/**
	 * @param long the id of the startup
	 * @returns Account a fully initialised Startup object
	 */
	public Account getAccount(long startupId) {

		List<InvestorType> invTypes = investorTypeRepository.findByStartupId(startupId);
		List<Label> labels = labelRepository.findByStartupId(startupId);
		Contact contact = contactRepository.findByStartupId(startupId);
		List<Founder> founders = founderRepository.findByStartupId(startupId);

		Account acc = super.getAccount(startupId);
		Startup su = startupRepository.find(startupId);
		List<PrivateShareholder> psh = pshRepository.findByStartupId(startupId);
		List<CorporateShareholder> csh = cshRepository.findByStartupId(startupId);

		return new Startup(acc, su, invTypes, labels, contact, founders, psh, csh);
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
	 * 
	 * @param id
	 * @return
	 */

	public ResponseEntity<?> deleteContactByStartupId(long id) {
		try {
			if (!belongsToStartup(id, contactRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this contact does not belong to that startup"));
			}
			contactRepository.deleteMemberByStartupId(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new contact
	 * 
	 * @param contact to be inserted in the DB
	 * @return a response with id and maybe body
	 */

	public ResponseEntity<?> addContactByStartupId(Contact contact) {
		try {
			contactRepository.addMemberByStartupId(contact);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes a Boardmember
	 * 
	 * @param id of the boardmember to be deleted
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> deleteBoardmemberByStartupId(long id) {
		try {
			if (!belongsToStartup(id, bmemRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this boardmember does not belong to that startup"));
			}
			bmemRepository.deleteMemberByStartupId(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new boardmember
	 * 
	 * @param bMem a new Boardmember
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addBoardmemberByStartupId(Boardmember bMem) {
		try {
			bmemRepository.addMemberByStartupId(bMem);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes a Founder
	 * 
	 * @param id of the founder to be deleted
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> deleteFounderByStartupId(long id) {
		try {
			if (!belongsToStartup(id, founderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this founder does not belong to that startup"));
			}
			founderRepository.deleteMemberByStartupId(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new founder
	 * 
	 * @param bMem a new founder
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addFounderByStartupId(Founder founder) {
		try {
			founderRepository.addByMember(founder);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes the entry in labelassignment with the specified labelId and the id in
	 * tokens.
	 * 
	 * @param labelId
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteLabelByStartupId(long labelId) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			labelRepository.deleteLabelOfStartup(labelId, accdet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
		}
	}

	/**
	 * Adds a new entry in the labelassignment repository
	 * 
	 * @param labelId of the label a new founder
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> addLabelByStartupId(long labelId) {
		try {
			AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investorTypeRepository.addInvestorTypeToStartup(labelId, accdetails.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds a new entry in the investortypeassignment repository
	 * 
	 * @param investortyped of the label a new founder
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addInvestorTypeByStartupId(long invTypeId) {
		try {
			AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investorTypeRepository.addInvestorTypeToStartup(invTypeId, accdetails.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
     * Get matching profile of startup (the required information for matching)
     * @return Matching profile of startup
     */
    public MatchingProfile getMatchingProfile(Startup startup) {
        if(startup == null)
            return null;
        
        MatchingProfile profile = new MatchingProfile();
        List<InvestorType> types = investorTypeRepository.findByStartupId(startup.getAccountId());
        List<Continent> continents = continentRepository.findByAccountId(startup.getAccountId());
        List<Country> countries = countryRepository.findByAccountId(startup.getAccountId());
        List<Industry> industries = industryRepository.findByAccountId(startup.getAccountId());
        List<InvestmentPhase> investmentPhases = investmentPhaseRepository.findByInvestorId(startup.getAccountId());
        List<Support> supports = supportRepository.findByAccountId(startup.getAccountId());

        profile.setAccountId(startup.getAccountId());
        profile.setName(startup.getName());
        profile.setDescription(startup.getDescription());
        profile.setInvestmentMax(startup.getInvestmentMax());
        profile.setInvestmentMin(startup.getInvestmentMin());
        profile.setStartup(true);

		for(InvestorType type : types) {
			profile.addInvestorType(type);
		}
        
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
        List<Startup> startups = startupRepository.getAll();
        List<MatchingProfile> profiles = new ArrayList<>();
        if(startups.size() == 0)
            return null;

        for(Startup startup : startups) {
            profiles.add(getMatchingProfile(startup));
        }

        return profiles;
    }
	
	/**
	 * Deletes the entry in investortypeassignment with the specified labelId and
	 * the id in tokens.
	 * 
	 * @param investortypeid
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteInvestorTypeByStartupId(long invTypeId) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investorTypeRepository.deleteInvestorTypeOfStartupId(invTypeId, accdet.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getLocalizedMessage()));
		}
	}

	/**
	 * Checks if a requesting startup belongs to the StartupMember
	 * 
	 * @param sideTableEntryId
	 * @param addinfRepo       The sidetable repository that should check if the
	 *                         startup belongs to the sidetable entry with given id
	 * @return
	 */
	private boolean belongsToStartup(long sideTableEntryId,
			IAdditionalInformationRepository<?, UpdateQueryBuilder> addinfRepo) {
		AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return accdetails.getId() == addinfRepo.getStartupIdByMemberId(sideTableEntryId);
	}
}
