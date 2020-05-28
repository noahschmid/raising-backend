package ch.raising.services;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.SharedDataRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.SharedData;
import ch.raising.utils.DatabaseOperationException;
/**
 * service for retreiving shared data
 * @author manus
 *
 */
@Service
public class SharedDataService {

	SharedDataRepository shareRepo;

	@Autowired
	public SharedDataService(SharedDataRepository shareRepo) {
		this.shareRepo = shareRepo;
	}

	public List<SharedData> getAllByAccount() throws EmptyResultDataAccessException, SQLException {
		long accountId = getAccountId();
		List<SharedData> data = shareRepo.findByAccountId(accountId);
		shareRepo.deleteByAccountId(accountId);
		return data;
	}

	public SharedData getByInteractionId(long shareId) throws EmptyResultDataAccessException, SQLException {
		return shareRepo.findByInteractionIdAndAccountId(shareId, getAccountId());
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}

}
