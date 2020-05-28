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

import ch.raising.models.AndroidSubscription;
import ch.raising.utils.MapUtil;
/**
 * manages the subscriptions
 * @author manus
 *
 */
@Repository
public class AndroidSubscriptionRepository {

	private final JdbcTemplate jdbc;
	private static final String INSERT_RECEIPT = "INSERT into androidsubscription(purchasetoken, orderId, subscriptionid, expires_date, accountId) VALUES (?,?,?,?,?)";
	private static final String UPDATE_RECEIPT = "UPDATE androidsubscription SET purchasetoken = ?, orderid=?, expires_date=?, subscriptionid = ? WHERE accountid = ?";
	private static final String HAS_ROW = "select accountId from androidsubscription where accountid =?";
	private static final String FIND_EXPIRES = "SELECT expires_date FROM androidsubscription WHERE accountid = ?";
	private static final String FIND_SUBSCRIPTION = "SELECT * FROM androidsubscription WHERE accountId = ?";

	@Autowired
	public AndroidSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	/**
	 * 
	 * @param sub {@link AndroidSubscription} to be added
	 * @param accountId the subscritpion should be added to
	 * @throws DataAccessException
	 */
	public void addNewPurchaseToken(AndroidSubscription sub, long accountId) throws DataAccessException {
		jdbc.update(INSERT_RECEIPT,
				new Object[] { sub.getPurchaseToken(), sub.getOrderId(), sub.getSubscriptionId(), sub.getExpiresDate(),
						accountId },
				new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.BIGINT });
	}
	/**
	 * 
	 * @param sub {@link AndroidSubscription} to be updated fields that should be unchanged should have their initial value
	 * @param accountId
	 * @throws DataAccessException
	 */
	public void updatePurchseToken(AndroidSubscription sub, long accountId) throws DataAccessException {
		jdbc.update(UPDATE_RECEIPT,
				new Object[] { sub.getPurchaseToken(), sub.getOrderId(), sub.getExpiresDate(), sub.getSubscriptionId(),
						accountId },
				new int[] { Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.BIGINT });
	}
	/**
	 * checks if an account already has an entry
	 * @param accountId
	 * @return true if account has entry
	 */
	public boolean hasEntry(long accountId) {
		try {
			jdbc.queryForObject(HAS_ROW, new Object[] { accountId }, MapUtil::mapRowToAccountId);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
	/**
	 * g
	 * @param accountId
	 * @return the expiresdate in ms
	 */
	public long getExpiresDateInMs(long accountId) {
		try {
			return jdbc.queryForObject(FIND_EXPIRES, new Object[] { accountId }, new ExpiresDateMapper());
		} catch (EmptyResultDataAccessException e) {
			return 0l;
		}
	}
	/**
	 * finds a subscription by accountid
	 * @param accountId
	 * @return {@link AndroidSubscription} that was found
	 * @throws DataAccessException
	 */
	public AndroidSubscription findSubscription(long accountId) throws DataAccessException {
		return jdbc.queryForObject(FIND_SUBSCRIPTION, new Object[] {accountId}, new SubscriptionMapper());
	}

	private class ExpiresDateMapper implements RowMapper<Long> {
		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			Timestamp res = rs.getTimestamp("expires_date");
			return res == null ? -1l : res.getTime();
		}

	}

	private class SubscriptionMapper implements RowMapper<AndroidSubscription> {
		@Override
		public AndroidSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
			return AndroidSubscription.builder()
					.expiresDate(rs.getTimestamp("expires_date"))
					.subscriptionId(rs.getString("subscriptionid"))
					.orderId(rs.getString("orderid"))
					.purchaseToken(rs.getString("purchasetoken")).build();
		}

	}

}
