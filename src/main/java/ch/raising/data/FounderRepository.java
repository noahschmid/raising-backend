package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Founder;
import ch.raising.utils.NotImplementedException;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class FounderRepository implements IAdditionalInformationRepository<Founder, UpdateQueryBuilder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public FounderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public long getStartupIdByMemberId(long founderId) {
		return jdbc.queryForObject("SELECT startupid FROM founder WHERE id = ?", new Object[] { founderId },
				this::mapRowToId);
	}

	@Override
	public Founder find(long id) {
		return jdbc.queryForObject("SELECT * FROM founder WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void addMemberByStartupId(Founder founder, long accountId) {
		jdbc.execute("INSERT INTO founder(id ,startupid, name, role, education) VALUES (?,?,?,?,?,?,?)",
				addByStartupId(founder, accountId));
	}
	
	@Override
	public void addMemberByStartupId(Founder founder) {
		jdbc.execute("INSERT INTO founder(id ,startupid, name, role, education) VALUES (?,?,?,?,?,?,?)",
				addByMember(founder));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM founder WHERE founder.id = ?", deleteById(id));
	}

	@Override
	public Founder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Founder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid")).name(rs.getString("name"))
				.role(rs.getString("role")).education(rs.getString("education")).build();
	}

	@Override
	public PreparedStatementCallback<Boolean> deleteById(long id) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		};
	}

	@Override
	public PreparedStatementCallback<Boolean> addByStartupId(Founder founder, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, founder.getId());
				ps.setLong(2, accountId);
				ps.setString(3, founder.getName());
				ps.setString(4, founder.getRole());
				ps.setString(5, founder.getEducation());
				return ps.execute();
			}
		};
	}
	@Override
	public PreparedStatementCallback<Boolean> addByMember(Founder founder) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, founder.getId());
				ps.setLong(2, founder.getStartupId());
				ps.setString(3, founder.getName());
				ps.setString(4, founder.getRole());
				ps.setString(5, founder.getEducation());
				return ps.execute();
			}
		};
	}
	@Override
	public List<Founder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM founder WHERE startupid = ?", new Object[] { startupId }, this::mapRowToModel);
	}

	
}
