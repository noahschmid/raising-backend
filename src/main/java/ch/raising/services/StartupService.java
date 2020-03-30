package ch.raising.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ch.raising.models.*;
import ch.raising.utils.InValidProfileException;
import ch.raising.utils.MailUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.data.*;
import ch.raising.interfaces.IAdditionalInformationRepository;

@Service
public class StartupService extends AccountService {

	@Autowired
	private StartupRepository startupRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BoardmemberRepository bmemRepository;

	@Autowired
	private FounderRepository founderRepository;

	private AssignmentTableRepository labelRepository;

	@Autowired
	private PrivateShareholderRepository pshRepository;

	@Autowired
	private CorporateShareholderRepository cshRepository;
	
	private AssignmentTableRepository investmentPhaseRepository;
	
	private AssignmentTableRepository investorTypeRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			ContactRepository contactRepository, BoardmemberRepository bmemRepository,
			FounderRepository founderRepository,  InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc,
			PrivateShareholderRepository pshRepository, CorporateShareholderRepository cshRepository) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc);

		this.investorTypeRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("investorType").withAccountIdName("startupid");
		this.labelRepository = AssignmentTableRepository.getInstance(jdbc).withTableName("label").withAccountIdName("startupid");
		this.startupRepository = startupRepository;
		this.contactRepository = contactRepository;
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

		if(su.getContact() != null)	
			contactRepository.addMemberByStartupId(su.getContact(), accountId);
		if(su.getBoardmembers() != null)
			su.getBoardmembers().forEach(bmem -> bmemRepository.addMemberByStartupId(bmem, accountId));
		if(su.getLabels() != null)
			su.getLabels().forEach(label -> labelRepository.addEntryToAccountById(label.getId(), accountId));
		if(su.getInvestorTypes() != null)
			su.getInvestorTypes().forEach(it -> investorTypeRepository.addEntryToAccountById(it.getId(), accountId));
		if(su.getFounders() != null)
			su.getFounders().forEach(founder -> founderRepository.addMemberByStartupId(founder, accountId));
		if(su.getPrivateShareholders() != null)
			su.getPrivateShareholders().forEach(ps -> pshRepository.addMemberByStartupId(ps, accountId));
		if(su.getCorporateShareholders() != null)
			su.getCorporateShareholders().forEach(cs -> cshRepository.addMemberByStartupId(cs, accountId));
		return accountId;
	}

	@Override
	/**
	 * @param long the tableEntryId of the startup
	 * @returns Account a fully initialised Startup object
	 */
	public Account getAccount(long startupId) {

		List<AssignmentTableModel> invTypes = investorTypeRepository.findByAccountId(startupId);
		List<AssignmentTableModel> labels = labelRepository.findByAccountId(startupId);
		Contact contact = contactRepository.findByStartupId(startupId).get(0);
		List<Founder> founders = founderRepository.findByStartupId(startupId);
		List<PrivateShareholder> psh = pshRepository.findByStartupId(startupId);
		List<CorporateShareholder> csh = cshRepository.findByStartupId(startupId);
		List<Boardmember> bmems = bmemRepository.findByStartupId(startupId);
		
		Account acc = super.getAccount(startupId);
		Startup su = startupRepository.find(startupId);
		
		
		return new Startup(acc, su, invTypes, labels, contact, founders, psh, csh, bmems);
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
	 * Deletes the contact specified by tableEntryId
	 * 
	 * @param tableEntryId
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
	 * @return a response with tableEntryId and maybe body
	 */

	public ResponseEntity<?> addContactByStartupId(Contact contact) {
		try {
			contactRepository.addMemberByStartupId(contact, contact.getStartupId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes a Boardmember
	 * 
	 * @param tableEntryId of the boardmember to be deleted
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
			bmemRepository.addMemberByStartupId(bMem, bMem.getStartupId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes a Founder
	 * 
	 * @param tableEntryId of the founder to be deleted
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
			founderRepository.addMemberByStartupId(founder, founder.getStartupId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes the entry in labelassignment with the specified labelId and the
	 * tableEntryId in tokens.
	 * 
	 * @param labelId
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteLabelByStartupId(long labelId) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			labelRepository.deleteEntryFromAccountById(labelId, accdet.getId());
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
			labelRepository.addEntryToAccountById(labelId, accdetails.getId());
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
        List<AssignmentTableModel> types = investorTypeRepository.findByAccountId(startup.getAccountId());
        List<AssignmentTableModel> continents = continentRepo.findByAccountId(startup.getAccountId());
        List<AssignmentTableModel> countries = countryRepo.findByAccountId(startup.getAccountId());
        List<AssignmentTableModel> industries = industryRepo.findByAccountId(startup.getAccountId());
        List<AssignmentTableModel> investmentPhases = investmentPhaseRepository.findByAccountId(startup.getAccountId());
        List<AssignmentTableModel> supports = supportRepo.findByAccountId(startup.getAccountId());

        profile.setAccountId(startup.getAccountId());
        profile.setName(startup.getName());
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
	 * Checks if a requesting startup belongs to the StartupMember
	 * 
	 * @param sideTableEntryId
	 * @param addinfRepo       The sidetable repository that should check if the
	 *                         startup belongs to the sidetable entry with given
	 *                         tableEntryId
	 * @return
	 */
	private boolean belongsToStartup(long sideTableEntryId, IAdditionalInformationRepository<?> addinfRepo) {
		AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return accdetails.getId() == addinfRepo.getStartupIdByMemberId(sideTableEntryId);
	}
}
