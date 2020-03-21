package ch.raising.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.models.*;
import ch.raising.utils.MailUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.data.*;
import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.interfaces.IAssignmentTableModel;

@Service
public class StartupService extends AccountService {

	@Autowired
	private AssignmentTableRepository investorTypeRepository;

	@Autowired
	private StartupRepository startupRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BoardmemberRepository bmemRepository;

	@Autowired
	private FounderRepository founderRepository;

	@Autowired
	private AssignmentTableRepository labelRepository;

	@Autowired
	private PrivateShareholderRepository pshRepository;

	@Autowired
	private CorporateShareholderRepository cshRepository;

	@Autowired
	public StartupService(AccountRepository accountRepository, StartupRepository startupRepository,
			ContactRepository contactRepository, BoardmemberRepository bmemRepository,
			FounderRepository founderRepository,  InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc,
			PrivateShareholderRepository pshRepository, CorporateShareholderRepository cshRepository) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc);

		this.investorTypeRepository = new AssignmentTableRepository(jdbc, "investortype");
		this.startupRepository = startupRepository;
		this.contactRepository = contactRepository;
		this.bmemRepository = bmemRepository;
		this.founderRepository = founderRepository;
		this.labelRepository = new AssignmentTableRepository(jdbc, "label");
		this.pshRepository = pshRepository;
		this.cshRepository = cshRepository;
	}

	@Override
	protected long registerAccount(Account account) throws Exception {
		Startup suReq = (Startup) account;

		checkRequestValid(account);

		long accountId = super.registerAccount(account);
		Startup su = Startup.startupBuilder().accountId(accountId).investmentPhaseId(suReq.getInvestmentPhaseId())
				.street(suReq.getStreet()).city(suReq.getCity()).zipCode(suReq.getZipCode()).website(suReq.getWebsite())
				.breakEvenYear(suReq.getBreakEvenYear()).numberOfFTE(suReq.getNumberOfFTE())
				.turnover(suReq.getTurnover()).build();

		startupRepository.add(su);

		contactRepository.addMemberByStartupId(suReq.getContact(), accountId);

		suReq.getLabels().forEach(label -> labelRepository.addEntryToAccountById(label.getId(), accountId));
		suReq.getInvTypes().forEach(it -> investorTypeRepository.addEntryToAccountById(accountId, it.getId()));
		suReq.getFounders().forEach(founder -> founderRepository.addByStartupId(founder, accountId));

		return accountId;
	}

	@Override
	/**
	 * @param long the tableEntryId of the startup
	 * @returns Account a fully initialised Startup object
	 */
	public Account getAccount(long startupId) {

		List<IAssignmentTableModel> invTypes = investorTypeRepository.findByAccountId(startupId);
		List<IAssignmentTableModel> labels = labelRepository.findByAccountId(startupId);
		Contact contact = contactRepository.findByStartupId(startupId).get(0);
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
	public ResponseEntity<?> updateStartup(long id, Startup su) {
		return ResponseEntity.status(500).body(new ErrorResponse("Not implemented yet"));
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
	 * Adds a new entry in the investortypeassignment repository
	 * 
	 * @param investortyped of the label a new founder
	 * @return response with code and optional body
	 */

	public ResponseEntity<?> addInvestorTypeByStartupId(long invTypeId) {
		try {
			AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investorTypeRepository.addEntryToAccountById(invTypeId, accdetails.getId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Deletes the entry in investortypeassignment with the specified labelId and
	 * the tableEntryId in tokens.
	 * 
	 * @param investortypeid
	 * @return response with code and optional body
	 */
	public ResponseEntity<?> deleteInvestorTypeByStartupId(long invTypeId) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investorTypeRepository.deleteEntryFromAccountById(invTypeId, accdet.getId());
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
