package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.models.PrivateShareholder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class PrivateShareholderRepository implements IAdditionalInformationRepository<PrivateShareholder> {

	@Autowired
	private final JdbcTemplate jdbc;
	private final String ADD_MEMBER;
	private final String DELETE_MEMBER;
	private final String FIND_BY_STARTUP_ID;
	private final String FIND_BY_ID;

	@Autowired
	public PrivateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.ADD_MEMBER = "INSERT INTO privateshareholder(startupid, firstname, lastname, city, equityshare, investortypeid, countryid) VALUES (?,?,?,?,?,?,?)";
		this.FIND_BY_STARTUP_ID = "SELECT * FROM privateshareholder WHERE startupid = ?";
		this.DELETE_MEMBER = "DELETE FROM privateshareholder WHERE id = ?";
		this.FIND_BY_ID = "SELECT * FROM privateshareholder WHERE id = ?";
	}

	@Override
	public PrivateShareholder find(long id) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public PrivateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return PrivateShareholder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).city(rs.getString("city"))
				.equityShare(rs.getInt("equityshare")).investorTypeId(rs.getLong("investortypeid"))
				.countryId(rs.getLong("countryid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(PrivateShareholder sumem, long startupId)
			throws SQLException, DataAccessException {
		if(sumem != null)
			jdbc.execute(ADD_MEMBER, getCallback(Arrays.asList(sumem), startupId));
	}
	
	@Override
	public void addMemberListByStartupId(List<PrivateShareholder> models, long startupId)
			throws SQLException, DataAccessException {
		if(models == null || models.size() == 1)
			return;
		String sql = ADD_MEMBER;
		for (int i = 1; i < models.size(); i++) {
			sql += ",(?,?,?,?,?,?,?)";
		}
		jdbc.execute(sql, getCallback(models, startupId));
	}

	@Override
	public void deleteMemberById(long id) throws SQLException, DataAccessException {
		jdbc.execute(DELETE_MEMBER, deleteById(id));
	}

	@Override
	public PreparedStatementCallback<Boolean> getCallback(List<PrivateShareholder> pshList, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				for (PrivateShareholder psh : pshList) {
					ps.setLong(c++, startupId);
					ps.setString(c++, psh.getFirstName());
					ps.setString(c++, psh.getLastName());
					ps.setString(c++, psh.getCity());
					ps.setInt(c++, psh.getEquityShare());
					ps.setLong(c++, psh.getInvestorTypeId());
					ps.setLong(c++, psh.getCountryId());
				}
				return ps.execute();
			}
		};
	}

	@Override
	public List<PrivateShareholder> findByStartupId(long startupId) throws SQLException, DataAccessException {
		return jdbc.query("SELECT * FROM privateshareholder WHERE startupid = ?", new Object[] { startupId },
				this::mapRowToModel);
	}

	@Override
	public void update(long id, PrivateShareholder req) throws SQLException, DataAccessException {
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "privateshareholder", id);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getCity(), "city");
		update.addField(req.getEquityShare(), "equityshare");
		update.addField(req.getInvestorTypeId(), "investortypeid");
		update.addField(req.getCountryId(), "countryid");
	}

}
