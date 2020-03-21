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
import ch.raising.models.PrivateShareholder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class PrivateShareholderRepository
		implements IAdditionalInformationRepository<PrivateShareholder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public PrivateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public PrivateShareholder find(long id) {
		return jdbc.queryForObject("SELECT * FROM privateshareholder WHERE tableEntryId = ?", new Object[] { id },
				this::mapRowToModel);
	}

	@Override
	public PrivateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return PrivateShareholder.builder().prename(rs.getString("prename")).name(rs.getString("name"))
				.city(rs.getString("city")).equityShare(rs.getInt("equityShare"))
				.investortypeId(rs.getLong("investortypeid")).startupId(rs.getLong("startupid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) {
		return jdbc.queryForObject("SELECT startupid FROM privateshareholder WHERE tableEntryId = ?", new Object[] { id },
				this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(PrivateShareholder sumem, long startupId) {
		jdbc.execute(
				"INSERT INTO privateshareholder(prename, name, city, equityshare, investorid, startupid) VALUES (?,?,?,?,?,?,?)",
				addByStartupId(sumem, startupId));
	}

	@Override
	public void addMemberByStartupId(PrivateShareholder sumem) {
		jdbc.execute(
				"INSERT INTO privateshareholder(prename, name, city, equityshare, investorid, startupid) VALUES (?,?,?,?,?,?,?)",
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
	public PreparedStatementCallback<Boolean> addByStartupId(PrivateShareholder psh, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(1, psh.getPrename());
				ps.setString(2, psh.getName());
				ps.setString(3, psh.getCity());
				ps.setInt(4,  psh.getEquityShare());
				ps.setLong(5, psh.getInvestortypeId());
				ps.setLong(6, startupId);
				return ps.execute();
			}
		};
	}

	@Override
	public PreparedStatementCallback<Boolean> addByMember(PrivateShareholder psh) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(1, psh.getPrename());
				ps.setString(2, psh.getName());
				ps.setString(3, psh.getCity());
				ps.setInt(4,  psh.getEquityShare());
				ps.setLong(5, psh.getInvestortypeId());
				ps.setLong(6, psh.getStartupId());
				return ps.execute();
			}
		};
	}

	@Override
	public List<PrivateShareholder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM privateshareholder WHERE startupid = ?", new Object[] {startupId}, this::mapRowToModel);
	}

}
