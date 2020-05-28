package ch.raising.services;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import ch.raising.models.Founder;
import ch.raising.models.PrivateShareholder;
import ch.raising.utils.NotAuthorizedException;
/**
 * service that is used to get the stakeholder of a startup 
 * @author manus
 *
 */
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
	 * @throws NotAuthorizedException
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	public void deleteBoardmemberByStartupId(long id) throws NotAuthorizedException, DataAccessException, SQLException {
		if (!belongsToStartup(id, bmemRepository)) {
			throw new NotAuthorizedException("this boardmember does not belong to that startup");
		}
		bmemRepository.deleteMemberById(id);
	}

	/**
	 * updates a given boardmember
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws NotAuthorizedException
	 */
	public void updateBoardmemberByStartupId(Boardmember bmem, long id)
			throws DataAccessException, SQLException, NotAuthorizedException {
		if (!belongsToStartup(id, bmemRepository)) {
			throw new NotAuthorizedException("this boardmember does not belong to that startup");
		}
		bmemRepository.update(id, bmem);
	}

	/**
	 * Adds a new boardmember
	 * 
	 * @param bMem a new Boardmember
	 * @return response with code and optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	public void addBoardmemberByStartupId(Boardmember bMem) throws DataAccessException, SQLException {
		bmemRepository.addMemberByStartupId(bMem, bMem.getStartupId());
	}

	/**
	 * Deletes a Founder
	 * 
	 * @param tableEntryId of the founder to be deleted
	 * @return response with code and optional body
	 * @throws NotAuthorizedException
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	public void deleteFounderByStartupId(long id) throws NotAuthorizedException, DataAccessException, SQLException {
		if (!belongsToStartup(id, founderRepository)) {
			throw new NotAuthorizedException("this founder does not belong to that startup");
		}
		founderRepository.deleteMemberById(id);
	}

	/**
	 * updates a given founder
	 * 
	 * @param id
	 * @return
	 * @throws NotAuthorizedException
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void updateFounderByStartupId(Founder founder, int id)
			throws NotAuthorizedException, DataAccessException, SQLException {
		if (!belongsToStartup(id, founderRepository)) {
			throw new NotAuthorizedException("this founder does not belong to that startup");
		}
		founderRepository.update(id, founder);
	}

	/**
	 * Adds a new founder
	 * 
	 * @param bMem a new founder
	 * @return response with code and optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */

	public void addFounderByStartupId(Founder founder) throws DataAccessException, SQLException {
		founderRepository.addMemberByStartupId(founder, founder.getStartupId());
	}

	/**
	 * delete the privateShareholder belonging to the startup
	 * 
	 * @param id memberid
	 * @return
	 * @throws NotAuthorizedException
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void deletePShareholderByStartupId(int id) throws NotAuthorizedException, DataAccessException, SQLException {
		if (!belongsToStartup(id, pShareholderRepository)) {
			throw new NotAuthorizedException("this shareholder does not belong to that startup");
		}
		pShareholderRepository.deleteMemberById(id);
	}

	/**
	 * Update privateshareholder by id
	 * 
	 * @param psh {@link ch.raising.test.models.PrivateShareholder}
	 * @return
	 * @throws Exception
	 */

	public void updatePShareholderByStartupId(PrivateShareholder psh, int id) throws Exception {
		if (!belongsToStartup(id, pShareholderRepository)) {
			throw new NotAuthorizedException("this shareholder does not belong to that startup");
		}
		pShareholderRepository.update(id, psh);
	}

	/**
	 * Add privateshareholder to startup by id
	 * 
	 * @param psh {@link ch.raising.test.models.PrivateShareholder}
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */

	public void addPShareholderByStartupId(PrivateShareholder psh) throws DataAccessException, SQLException {
		pShareholderRepository.addMemberByStartupId(psh, psh.getStartupId());
	}

	/**
	 * delete the corporateShareholder belonging to the startup
	 * 
	 * @param id memberid
	 * @return
	 * @throws NotAuthorizedException 
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void deleteCShareholderByStartupId(int id) throws NotAuthorizedException, DataAccessException, SQLException {
			if (!belongsToStartup(id, cShareholderRepository)) {
				throw new NotAuthorizedException("throws SQLException, DataAccessException");
			}
			cShareholderRepository.deleteMemberById(id);
	}

	/**
	 * Update privateshareholder by id
	 * 
	 * @param psh {@link ch.raising.test.models.CorporateShareholder}
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 * @throws NotAuthorizedException 
	 */

	public void updateCShareholderByStartupId(CorporateShareholder csh, int id) throws DataAccessException, SQLException, NotAuthorizedException {
			if (!belongsToStartup(id, cShareholderRepository)) {
				throw new NotAuthorizedException("this shareholder does not belong to that startup");
			}
			cShareholderRepository.update(id, csh);
	}

	/**
	 * add corporateshareholder
	 * 
	 * @param csh {@link ch.raising.modles.CorporateShareholder}
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void addCShareholderByStartupId(CorporateShareholder csh) throws DataAccessException, SQLException {
			cShareholderRepository.addMemberByStartupId(csh, csh.getStartupId());
	}

	/**
	 * Checks if a requesting startup belongs to the StartupMember
	 * 
	 * @param sideTableEntryId
	 * @param addinfRepo       The sidetable repository that should check if the
	 *                         startup belongs to the sidetable entry with given
	 *                         tableEntryId
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	private boolean belongsToStartup(long sideTableEntryId, IAdditionalInformationRepository<?> addinfRepo) throws DataAccessException, SQLException {
		AccountDetails accdetails = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return accdetails.getId() == addinfRepo.getStartupIdByMemberId(sideTableEntryId);
	}

}
