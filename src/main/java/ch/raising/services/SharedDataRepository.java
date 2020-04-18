package ch.raising.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import ch.raising.models.Share;

@Repository
public class SharedDataRepository {

	private final RowMapper<Share> ShareMapper = new ShareMapper();
	private final JdbcTemplate jdbc;
	private final String FIND_BY_ACCOUNT_ID;
	private final String FIND_BY_INTERACTION_ID;
	private final String ADD_SHARED_DATA;
	private final String DELETE_BY_INTERACTION_ID;
	private final String DELETE_BY_ACCOUNT_ID;;
	private final String DELETE_BY_ID;
	private final String DELETE_BY_ID_AND_ACCOUNT_ID;

	@Autowired
	public SharedDataRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_BY_ACCOUNT_ID = "SELECT * FROM share WHERE accountid = ?";
		this.FIND_BY_INTERACTION_ID = "SELECT * FROM share WHERE interactionId = ? AND accountid = ?";
		this.ADD_SHARED_DATA = "INSERT INTO share(accountid, firstname, lastname, email, phone, businessplanid, availableuntil) VALUES (?,?,?,?,?,?,?) RETURNING id";
		this.DELETE_BY_INTERACTION_ID = "DELETE FROM share WHERE interactionid = ?";
		this.DELETE_BY_ACCOUNT_ID = "DELETE FROM share WHERE accountid = ?";
		this.DELETE_BY_ID = "DELETE FROM share WHERE id = ?";
		this.DELETE_BY_ID_AND_ACCOUNT_ID = "DELETE FROM share WHERE interactionid = ? and accountid = ?";
	}

	public List<Share> findByAccountId(long accountId)throws EmptyResultDataAccessException, SQLException {
		return jdbc.query(FIND_BY_ACCOUNT_ID, new Object[] { accountId }, ShareMapper);
	}

	public Share findByInteractionIdAndAccountId(long interactionId, long accountId) {
		return jdbc.queryForObject(FIND_BY_INTERACTION_ID, new Object[] {interactionId, accountId}, ShareMapper);
	}
	
	public void deleteByInteractionIdAndAccountId(long interactionId, long accountId) {
		jdbc.update(FIND_BY_INTERACTION_ID, new Object[] {interactionId, accountId}, ShareMapper);
	}
	
	public long addSharedData(Share data)throws SQLException, DataAccessException {
		return jdbc.execute(ADD_SHARED_DATA, insertSharedDataCallback(data));
	}
	
	public void deleteByInteractionId(long interactionId) throws SQLException, DataAccessException{
		jdbc.update(DELETE_BY_INTERACTION_ID, new Object[] {interactionId}, new int[] {Types.BIGINT});
	}
	public void deleteByAccountId(long accountId)throws SQLException, DataAccessException{
		jdbc.update(DELETE_BY_ACCOUNT_ID, new Object[] {accountId}, new int[] {Types.BIGINT});
	}
	public void deleteById(long dataId) {
		jdbc.update(DELETE_BY_ID, new Object[] {dataId}, new int[] {Types.BIGINT});
	}
	private PreparedStatementCallback<Long> insertSharedDataCallback(Share data) {
		return new PreparedStatementCallback<Long>() {

			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, data.getAccountId());
				ps.setString(c++, data.getFirstName());
				ps.setString(c++, data.getLastName());
				ps.setString(c++, data.getEmail());
				ps.setInt(c++, data.getPhone());
				if(data.getBusinessPlanId() == -1) {
					ps.setNull(c++, Types.BIGINT);
				}else {
					ps.setLong(c++, data.getBusinessPlanId());
				}
				ps.setTimestamp(c++, new Timestamp(System.currentTimeMillis() + 1000*60*60*24*14)); //two weeks
				if(ps.executeUpdate() > 1) {
					if(ps.getResultSet() != null) {
						return ps.getResultSet().getLong("id");
					}
				}
					return -1l;
			}
		};
	}

	private static class ShareMapper implements RowMapper<Share> {

		@Override
		public Share mapRow(ResultSet rs, int row) throws SQLException {
			return Share.builder()
			.id(rs.getLong("id") != 0 ? rs.getLong("id") : -1)
			.accountId(rs.getLong("accountid"))
			.availableUntil(rs.getTimestamp("availableUntil"))
			.firstName(rs.getString("firstname"))
			.lastName(rs.getString("lastname"))
			.email(rs.getString("email"))
			.phone(rs.getInt("phone"))
			.businessPlanId(rs.getLong("businessplanid"))
			.build();
		}

	}

	public Share findByIdAndDelete(long interactionId, long accountId) {
		Share data = findByInteractionIdAndAccountId(interactionId, accountId);
		deleteByInteractionIdAndAccountId(interactionId, accountId);
		return data;
	}
}
