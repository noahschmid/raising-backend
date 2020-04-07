package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.BoardmemberRepository;
import ch.raising.data.CorporateShareholderRepository;
import ch.raising.data.FounderRepository;
import ch.raising.data.PrivateShareholderRepository;
import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Boardmember;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.ErrorResponse;
import ch.raising.models.Founder;
import ch.raising.models.PrivateShareholder;

@Service
public class AdditionalInformationService {
	
	FounderRepository founderRepository;
	BoardmemberRepository bmemRepository;
	PrivateShareholderRepository pShareholderRepository;

	CorporateShareholderRepository cShareholderRepository;
	
	
	@Autowired
	public AdditionalInformationService(JdbcTemplate jdbc) {
		this.founderRepository = new FounderRepository(jdbc);
		this.bmemRepository = new BoardmemberRepository(jdbc);
		this.pShareholderRepository = new PrivateShareholderRepository(jdbc);
		this.cShareholderRepository = new CorporateShareholderRepository(jdbc);
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
	 * updates a given boardmember
	 * @param id
	 * @return
	 */
	public ResponseEntity<?> updateBoardmemberByStartupId(Boardmember bmem, long id) {
		try {
			if (!belongsToStartup(id, bmemRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this founder does not belong to that startup"));
			}
			bmemRepository.update(id, bmem);
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
	 * updates a given founder
	 * @param id
	 * @return
	 */
	public ResponseEntity<?> updateFounderByStartupId(Founder founder, int id) {
		try {
			if (!belongsToStartup(id, founderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this founder does not belong to that startup"));
			}
			founderRepository.update(id, founder);
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
	 * delete the privateShareholder belonging to the startup
	 * @param id memberid
	 * @return
	 */
	public ResponseEntity<?> deletePShareholderByStartupId(int id) {
		try {
			if (!belongsToStartup(id, pShareholderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this shareholder does not belong to that startup"));
			}
			pShareholderRepository.deleteMemberByStartupId(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}


	/**
	 * Update privateshareholder by id
	 * @param psh {@link ch.raising.models.PrivateShareholder}
	 * @return
	 */

	public ResponseEntity<?> updatePShareholderByStartupId(PrivateShareholder psh, int id) {
		try {
			if (!belongsToStartup(id, pShareholderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this shareholder does not belong to that startup"));
			}
			pShareholderRepository.update(id, psh);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Add privateshareholder to startup by id
	 * @param psh {@link ch.raising.models.PrivateShareholder}
	 * @return
	 */

	public ResponseEntity<?> addPShareholderByStartupId(PrivateShareholder psh) {
		try {
			pShareholderRepository.addMemberByStartupId(psh, psh.getStartupId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	/**
	 * delete the corporateShareholder belonging to the startup
	 * @param id memberid
	 * @return
	 */
	public ResponseEntity<?> deleteCShareholderByStartupId(int id) {
		try {
			if (!belongsToStartup(id, cShareholderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this shareholder does not belong to that startup"));
			}
			cShareholderRepository.deleteMemberByStartupId(id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}


	/**
	 * Update privateshareholder by id
	 * @param psh {@link ch.raising.models.CorporateShareholder}
	 * @return
	 */

	public ResponseEntity<?> updateCShareholderByStartupId(CorporateShareholder csh, int id) {
		try {
			if (!belongsToStartup(id,cShareholderRepository)) {
				return ResponseEntity.status(403)
						.body(new ErrorResponse("this shareholder does not belong to that startup"));
			}
			cShareholderRepository.update(id, csh);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}


	/**
	 * add corporateshareholder
	 * @param csh {@link ch.raising.modles.CorporateShareholder}
	 * @return
	 */
	public ResponseEntity<?> addCShareholderByStartupId(CorporateShareholder csh) {
		try {
			cShareholderRepository.addMemberByStartupId(csh, csh.getStartupId());
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
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
