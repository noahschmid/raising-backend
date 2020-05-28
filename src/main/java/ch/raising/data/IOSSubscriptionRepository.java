package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.models.IOSSubscription;
import ch.raising.utils.MapUtil;
/**
 * repository for managing the subscription of ios users
 * @author manus
 *
 */
@Repository
public class IOSSubscriptionRepository {

	private final JdbcTemplate jdbc;
	private static final String INSERT_RECEIPT = "INSERT into iossubscription(latestreceiptdata, accountId, expires_date, originalTransactionId, subscriptionid) VALUES (?,?,?,?,?)";
	private static final String UPDATE_RECEIPT = "UPDATE iossubscription SET latestreceiptdata = ?, expires_date = ?, originalTransactionId = ?, subscriptionid=? WHERE accountid = ?";
	private static final String FIND_EXPIRES = "SELECT expires_date FROM iossubscription WHERE accountid = ?";
	private static final String FIND_INFO = "SELECT * FROM iossubscription WHERE accountid = ?";

	private static final String HAS_ROW = "select accountId from iossubscription where accountid =?";

	@Autowired
	public IOSSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void addNewReceipt(IOSSubscription receipt, long accountId) throws DataAccessException {
		jdbc.update(INSERT_RECEIPT,
				new Object[] { receipt.getLatestReceiptData(), accountId, receipt.getExpiresDate(),
						receipt.getOriginalTransactionId(), receipt.getSubscriptionId() },
				new int[] {Types.VARCHAR, Types.BIGINT, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR});
	}

	public void updateReceipt(IOSSubscription receipt, long accountId) throws DataAccessException {
		jdbc.update(UPDATE_RECEIPT, new Object[] { receipt.getLatestReceiptData(), receipt.getExpiresDate(),
				receipt.getOriginalTransactionId(), receipt.getSubscriptionId(), accountId }, new int[] {Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.BIGINT});
	}

	public long getExpiresDateInMs(long accountId) {
		try {
			return jdbc.queryForObject(FIND_EXPIRES, new Object[] { accountId }, new ExpiresDateMapper());
		} catch (EmptyResultDataAccessException e) {
			return -1l;
		}
	}

	public boolean hasEntry(long accountId) {

		try {
			jdbc.queryForObject(HAS_ROW, new Object[] { accountId }, MapUtil::mapRowToAccountId);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}

	}

	public IOSSubscription getSubscriptionInfo(long accountId) throws DataAccessException, SQLException {
		return jdbc.queryForObject(FIND_INFO, new Object[] {accountId}, new SubscriptionMapper());
	}
	
	private class SubscriptionMapper implements RowMapper<IOSSubscription> {

		@Override
		public IOSSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
			return IOSSubscription.builder().expiresDate(rs.getTimestamp("expires_date")).subscriptionId(rs.getString("subscriptionid")).build();
		}

	}
	
	private class ExpiresDateMapper implements RowMapper<Long> {

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			Timestamp res = rs.getTimestamp("expires_date");
			return res == null ? -1l : res.getTime();
		}

	}

}
