package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.models.responses.IOSSubscription;
import ch.raising.utils.MapUtil;

@Repository
public class IOSSubscriptionRepository {

	private final JdbcTemplate jdbc;
	private static final String INSERT_RECEIPT = "INSERT into iossubscription(latestreceiptdata, accountId, expires_date, originalTransactionId) VALUES (?,?,?,?)";
	private static final String UPDATE_RECEIPT = "UPDATE iossubscription SET latestreceiptdata = ?, expires_date = ?, originalTransactionId = ? WHERE accountid = ?";
	private static final String FIND_EXPIRES = "SELECT expires_date FROM iossubscription WHERE accountid = ?";

	private static final String HAS_ROW = "select accountId from iossubscription where accountid =?";

	@Autowired
	public IOSSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void addNewReceipt(IOSSubscription receipt, long accountId) throws DataAccessException {
		jdbc.update(INSERT_RECEIPT,
				new Object[] { receipt.getLatestReceiptData(), accountId, receipt.getExpiresDate(),
						receipt.getOriginalTransactionId() },
				new int[] {Types.VARCHAR, Types.BIGINT, Types.TIMESTAMP, Types.VARCHAR});
	}

	public void updateReceipt(IOSSubscription receipt, long accountId) throws DataAccessException {
		jdbc.update(UPDATE_RECEIPT, new Object[] { receipt.getLatestReceiptData(), receipt.getExpiresDate(),
				receipt.getOriginalTransactionId(), accountId }, new int[] {Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.BIGINT});
	}

	public long getExpiresDateInMs(long accountId) {
		try {
			return jdbc.queryForObject(FIND_EXPIRES, new Object[] { accountId }, new ExpiresDateMapper());
		} catch (EmptyResultDataAccessException e) {
			return 0l;
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

	private class ExpiresDateMapper implements RowMapper<Long> {

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getTimestamp("expires_date").getTime();
		}

	}

}
