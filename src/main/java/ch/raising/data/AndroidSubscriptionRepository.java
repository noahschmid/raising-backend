package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.utils.MapUtil;

@Repository
public class AndroidSubscriptionRepository {

	private final JdbcTemplate jdbc;
	private static final String INSERT_RECEIPT = "INSERT into androidsubscription(purchasetoken, subscriptionid, accountId) VALUES (?,?,?)";
	private static final String UPDATE_RECEIPT = "UPDATE androidsubscription SET purchasetoken = ?, subscriptionid = ? WHERE accountid = ?";
	private static final String HAS_ROW = "select accountId from androidsubscription where accountid =?";
	private static final String FIND_EXPIRES = "SELECT expires_date FROM iossubscription WHERE accountid = ?";
	@Autowired
	public AndroidSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void addNewReceipt(String receipt, String subscriptionId, long accountId) throws DataAccessException {
		jdbc.update(INSERT_RECEIPT, new Object[] { receipt, subscriptionId, accountId }, new int[] { Types.VARCHAR, Types.VARCHAR, Types.BIGINT });
	}

	public void updateReceipt(String receipt, String subscriptionId, long accountId) throws DataAccessException {
		jdbc.update(UPDATE_RECEIPT, new Object[] { receipt, subscriptionId, accountId }, new int[] { Types.VARCHAR, Types.VARCHAR, Types.BIGINT });
	}

	public boolean hasEntry(long accountId) {
		try {
			jdbc.queryForObject(HAS_ROW, new Object[] {accountId}, MapUtil::mapRowToAccountId);
			return true;
		}catch(EmptyResultDataAccessException e) {
			return false;
		}
	}

	public long getExpiresDateInMs(long accountId) {
		try {
			return jdbc.queryForObject(FIND_EXPIRES, new Object[] { accountId }, new ExpiresDateMapper());
		}catch(EmptyResultDataAccessException e) {
			return 0l;
		}
	}
	
	private class ExpiresDateMapper implements RowMapper<Long> {
		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getTimestamp("expires_date").getTime();
		}

	}
	public Object findSubscription(long accountId) {
		// TODO Auto-generated method stub
		return null;
	}
}
