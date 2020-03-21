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
import ch.raising.models.CorporateShareholder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class CorporateShareholderRepository
		implements IAdditionalInformationRepository<CorporateShareholder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public CorporateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public CorporateShareholder find(long id) {
		return jdbc.queryForObject("SELECT * FROM corporateshareholder WHERE tableEntryId = ?", new Object[] { id },
				this::mapRowToModel);
	}

	@Override
	public CorporateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return CorporateShareholder.builder().name(rs.getString("name")).website(rs.getString("website"))
				.equityShare(rs.getInt("equityshare")).corporateBodyId(rs.getLong("startupid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) {
		return jdbc.queryForObject("SELECT startupid FROM corporateshareholder WHERE tableEntryId = ?", new Object[] { id },
				this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(CorporateShareholder sumem, long startupId) {
		jdbc.execute(
				"INSERT INTO corporateshareholder(name,website,equityshare,coporatebodyid,startupid) VALUES (?,?,?,?,?)",
				addByStartupId(sumem, startupId));
	}

	@Override
	public void addMemberByStartupId(CorporateShareholder sumem) {
		jdbc.execute(
				"INSERT INTO corporateshareholder(name,website,equityshare,coporatebodyid,startupid) VALUES (?,?,?,?,?)",
				addByMember(sumem));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM privateshareholder WHERE tableEntryId = ?", deleteById(id));
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
	public PreparedStatementCallback<Boolean> addByStartupId(CorporateShareholder psh, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setString(c++, psh.getName());
				ps.setString(c++, psh.getWebsite());
				ps.setLong(c++, psh.getCorporateBodyId());
				ps.setLong(c++, startupId);
				return ps.execute();
			}
		};
	}

	@Override
	public PreparedStatementCallback<Boolean> addByMember(CorporateShareholder psh) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setString(c++, psh.getName());
				ps.setString(c++, psh.getWebsite());
				ps.setLong(c++, psh.getCorporateBodyId());
				ps.setLong(c++, psh.getStartupId());
				return ps.execute();
			}
		};
	}

	@Override
	public List<CorporateShareholder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM corporateshareholder WHERE startupid = ?", new Object[] {startupId}, this::mapRowToModel);
	}

}
