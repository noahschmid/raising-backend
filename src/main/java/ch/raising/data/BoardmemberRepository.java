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

import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.models.Boardmember;
import ch.raising.utils.NotImplementedException;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class BoardmemberRepository implements IAdditionalInformationRepository<Boardmember> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public BoardmemberRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void addMemberByStartupId(Boardmember bmem) {
		jdbc.execute(
				"INSERT INTO boardmember(startupid, name, education, profession, pulldowntype, pulldownduration) VALUES (?,?,?,?,?,?)",
				addByMember(bmem));

	}

	@Override 
	public void addMemberByStartupId(Boardmember bmem, long startupid) {
		jdbc.execute(
				"INSERT INTO boardmember(startupid, name, education, profession, pulldowntype, pulldownduration) VALUES (?,?,?,?,?,?,?)",
				addByStartupId(bmem, startupid));
	}

	@Override
	public long getStartupIdByMemberId(long bmemId) {
		return jdbc.queryForObject("SELECT startupid FROM boardmember WHERE tableEntryId = ?", new Object[] { bmemId },
				this::mapRowToId);
	}

	@Override
	public Boardmember find(long id) {
		return jdbc.queryForObject("SELECT * FROM boardmember WHERE tableEntryId = ?", new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM boardmember WHERE tableEntryId = ?", deleteById(id));
	}

	@Override
	public Boardmember mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Boardmember.builder().id(rs.getLong("tableEntryId")).startupid(rs.getLong("startupid")).name(rs.getString("name"))
				.education(rs.getString("education")).profession(rs.getString("profession"))
				.pullDownType(rs.getString("pulldowntype")).pullDownDuration(rs.getInt("pulldownduration")).build();
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
	public PreparedStatementCallback<Boolean> addByMember(Boardmember bmem) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, bmem.getStartupId());
				ps.setString(c++, bmem.getName());
				ps.setString(c++, bmem.getEducation());
				ps.setString(c++, bmem.getProfession());
				ps.setString(c++, bmem.getPullDownType());
				ps.setInt(c++, bmem.getPullDownDuration());
				return ps.execute();
			}
		};
	}
	
	@Override
	public PreparedStatementCallback<Boolean> addByStartupId(Boardmember bmem, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, bmem.getId());
				ps.setLong(2, accountId);
				ps.setString(3, bmem.getName());
				ps.setString(4, bmem.getEducation());
				ps.setString(5, bmem.getProfession());
				ps.setString(6, bmem.getPullDownType());
				ps.setInt(7, bmem.getPullDownDuration());
				return ps.execute();
			}
		};
	}

	@Override
	public List<Boardmember> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM boardmember WHERE startupid = ?", new Object[] {startupId}, this::mapRowToModel);
	}

}
