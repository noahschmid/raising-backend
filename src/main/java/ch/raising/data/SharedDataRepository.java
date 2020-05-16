package ch.raising.data;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import ch.raising.models.SharedData;

@Repository
public class SharedDataRepository {

	private final RowMapper<SharedData> shareMapper = new ShareMapper();
	private final JdbcTemplate jdbc;
	private final String FIND_BY_ACCOUNT_ID;
	private final String FIND_BY_INTERACTION_ID_AND_ACCOUNT_ID;
	private final String INSERT_SHARED_DATA;
	private final String DELETE_BY_INTERACTION_ID;
	private final String DELETE_BY_ACCOUNT_ID;;
	private final String DELETE_BY_ID;
	private final String DELETE_BY_INTERACTION_ID_AND_ACCOUNT_ID;

	@Autowired
	public SharedDataRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_BY_ACCOUNT_ID = "SELECT * FROM share WHERE accountid = ?";
		this.FIND_BY_INTERACTION_ID_AND_ACCOUNT_ID = "SELECT * FROM share WHERE interactionid = ? AND accountid = ?";
		this.INSERT_SHARED_DATA = "INSERT INTO share(accountid, interactionid, email, phone, businessplanid) VALUES (?,?,?,?,?) RETURNING id";
		this.DELETE_BY_INTERACTION_ID = "DELETE FROM share WHERE interactionid = ?";
		this.DELETE_BY_ACCOUNT_ID = "DELETE FROM share WHERE accountid = ?";
		this.DELETE_BY_ID = "DELETE FROM share WHERE id = ?";
		this.DELETE_BY_INTERACTION_ID_AND_ACCOUNT_ID = "DELETE FROM share WHERE interactionid = ? and accountid = ?";
	}

	public List<SharedData> findByAccountId(long accountId)throws EmptyResultDataAccessException, SQLException {
		return jdbc.query(FIND_BY_ACCOUNT_ID, new Object[] { accountId }, shareMapper);
	}

	public SharedData findByInteractionIdAndAccountId(long interactionId, long accountId) {
		LoggerFactory.getLogger(this.getClass().getName()).info("interactionId: {} accountid: {}", interactionId, accountId);
		return jdbc.queryForObject(FIND_BY_INTERACTION_ID_AND_ACCOUNT_ID, new Object[] {interactionId, accountId}, shareMapper);
	}
	
	public void deleteByInteractionIdAndAccountId(long interactionId, long accountId) throws SQLException{
		jdbc.update(DELETE_BY_INTERACTION_ID_AND_ACCOUNT_ID, new Object[] {interactionId, accountId}, new int[] {Types.BIGINT, Types.BIGINT});
	}
	
	public long addSharedData(SharedData data)throws SQLException, DataAccessException {
		return jdbc.execute(new AddSharePreparedStaement(), insertSharedDataCallback(data));
	}
	
	public void deleteByInteractionId(long interactionId) throws SQLException, DataAccessException{
		jdbc.update(DELETE_BY_INTERACTION_ID, new Object[] {interactionId}, new int[] {Types.BIGINT});
	}
	public void deleteByAccountId(long accountId) throws SQLException, DataAccessException{
		jdbc.update(DELETE_BY_ACCOUNT_ID, new Object[] {accountId}, new int[] {Types.BIGINT});
	}
	public void deleteById(long dataId) {
		jdbc.update(DELETE_BY_ID, new Object[] {dataId}, new int[] {Types.BIGINT});
	}
	private PreparedStatementCallback<Long> insertSharedDataCallback(SharedData data) {
		return new PreparedStatementCallback<Long>() {

			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, data.getAccountId());
				ps.setLong(c++, data.getInteractionId());
				ps.setString(c++, data.getEmail());
				ps.setString(c++, data.getPhone());
				if(data.getBusinessPlanId() == -1) {
					ps.setNull(c++, Types.BIGINT);
				}else {
					ps.setLong(c++, data.getBusinessPlanId());
				}
				if(ps.executeUpdate() > 1) {
					if(ps.getGeneratedKeys() != null) {
						return ps.getGeneratedKeys().getLong("id");
					}
				}
					return -1l;
			}
		};
	}
	
	private class AddSharePreparedStaement implements PreparedStatementCreator{

		@Override
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(INSERT_SHARED_DATA, Statement.RETURN_GENERATED_KEYS);
		}
		
	}

	private static class ShareMapper implements RowMapper<SharedData> {

		@Override
		public SharedData mapRow(ResultSet rs, int row) throws SQLException {
			return SharedData.builder()
			.id(rs.getLong("id") != 0 ? rs.getLong("id") : -1)
			.accountId(rs.getLong("accountid"))
			.interactionId(rs.getLong("interactionid"))
			.email(rs.getString("email"))
			.phone(rs.getString("phone"))
			.businessPlanId(rs.getLong("businessplanid"))
			.build();
		}

	}
}
