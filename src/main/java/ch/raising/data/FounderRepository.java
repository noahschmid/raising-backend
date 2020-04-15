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
import ch.raising.models.Founder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class FounderRepository implements IAdditionalInformationRepository<Founder> {

	@Autowired
	private final JdbcTemplate jdbc;
	private final String ADD_MEMBER;
	private final String DELETE_MEMBER;
	private final String FIND_BY_STARTUP_ID;
	private final String FIND_BY_ID;

	@Autowired
	public FounderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.ADD_MEMBER = "INSERT INTO founder(startupid, firstname, lastname, education, position, countryid) VALUES (?,?,?,?,?,?)";
		this.FIND_BY_STARTUP_ID = "SELECT * FROM founder WHERE startupid = ?";
		this.FIND_BY_ID = "SELECT * FROM founder WHERE id  = ?";
		this.DELETE_MEMBER = "DELETE FROM founder WHERE id = ?";
	}

	@Override
	public List<Founder> findByStartupId(long startupId) {
		return jdbc.query(FIND_BY_STARTUP_ID, new Object[] { startupId }, this::mapRowToModel);
	}
	
	@Override
	public long getStartupIdByMemberId(long founderId) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { founderId }, this::mapRowToId);
	}

	@Override
	public Founder find(long id) {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void addMemberByStartupId(Founder founder, long accountId) throws SQLException, DataAccessException {
		if(founder != null)
			jdbc.execute(ADD_MEMBER, getCallback(Arrays.asList(founder), accountId));
	}

	@Override
	public void addMemberListByStartupId(List<Founder> models, long startupId)
			throws SQLException, DataAccessException {
		String sql = ADD_MEMBER;
		if(models == null || models.size() == 0)
			return;
		for (int i = 1; i < models.size(); i++) {
			sql += ", (?,?,?,?,?,?)";
		}
		jdbc.execute(sql, getCallback(models, startupId));

	}

	@Override
	public void deleteMemberById(long id) throws SQLException, DataAccessException {
		jdbc.execute(DELETE_MEMBER, deleteById(id));
	}

	@Override
	public void update(long id, Founder req) throws DataAccessException, SQLException {
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "founder", id);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getEducation(), "education");
		update.addField(req.getPosition(), "position");
		update.addField(req.getCountryId(), "countryid");
		update.execute();
	}

	@Override
	public Founder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Founder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname"))
				.position(rs.getString("position")).countryId(rs.getLong("countryid"))
				.education(rs.getString("education")).build();
	}

	@Override
	public PreparedStatementCallback<Boolean> getCallback(List<Founder> founderList, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				for (Founder founder : founderList) {
					ps.setLong(c++, accountId);
					ps.setString(c++, founder.getFirstName());
					ps.setString(c++, founder.getLastName());
					ps.setString(c++, founder.getEducation());
					ps.setString(c++, founder.getPosition());
					ps.setLong(c++, founder.getCountryId());
				}
				return ps.execute();
			}
		};
	}

}
