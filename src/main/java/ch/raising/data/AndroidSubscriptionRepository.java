package ch.raising.data;

import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.utils.MapUtil;

@Repository
public class AndroidSubscriptionRepository {

	private final JdbcTemplate jdbc;
	private static final String INSERT_RECEIPT = "INSERT into androidsubscription(purchasetoken, accountId) VALUES (?,?)";
	private static final String UPDATE_RECEIPT = "UPDATE androidsubscription SET purchasetoken = ? WHERE accountid = ?";
	private static final String HAS_ROW = "select accountId from androidsubscription where accountid =?";

	@Autowired
	public AndroidSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void addNewReceipt(String receipt, long accountId) throws DataAccessException {
		jdbc.update(INSERT_RECEIPT, new Object[] { receipt, accountId }, new int[] { Types.VARCHAR, Types.BIGINT });
	}

	public void updateReceipt(String receipt, long accountId) throws DataAccessException {
		jdbc.update(UPDATE_RECEIPT, new Object[] { receipt, accountId }, new int[] { Types.VARCHAR, Types.BIGINT });
	}

	public boolean hasEntry(long accountId) {
		try {
			jdbc.queryForObject(HAS_ROW, new Object[] {accountId}, MapUtil::mapRowToAccountId);
			return true;
		}catch(EmptyResultDataAccessException e) {
			return false;
		}
	}
}
