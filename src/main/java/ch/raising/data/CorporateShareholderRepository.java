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
public class CorporateShareholderRepository implements IAdditionalInformationRepository<CorporateShareholder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public CorporateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public CorporateShareholder find(long id) {
		return jdbc.queryForObject("SELECT * FROM corporateshareholder WHERE id = ?", new Object[] { id },
				this::mapRowToModel);
	}

	@Override
	public CorporateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return CorporateShareholder.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.lastName(rs.getString("lastname")).firstName(rs.getString("firstname"))
				.website(rs.getString("website")).equityShare(rs.getInt("equityshare"))
				.corporateBodyId(rs.getLong("corporatebodyid")).countryId(rs.getInt("countryid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) {
		return jdbc.queryForObject("SELECT * FROM corporateshareholder WHERE id = ?", new Object[] { id },
				this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(CorporateShareholder sumem, long startupId) {
		jdbc.execute(
				"INSERT INTO corporateshareholder(startupid, firstname, lastname, website, equityshare, corporatebodyid, countryid) VALUES (?,?,?,?,?,?,?)",
				addByStartupId(sumem, startupId));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM corporateshareholder WHERE startupid = ?", deleteById(id));
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
	public PreparedStatementCallback<Boolean> addByStartupId(CorporateShareholder csh, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, startupId);
				ps.setString(c++, csh.getCorpName());
				ps.setString(c++, csh.getWebsite());
				ps.setInt(c++, csh.getEquityShare());
				ps.setLong(c++, csh.getCorporateBodyId());
				ps.setLong(c++, csh.getCountryId());
				return ps.execute();
			}
		};
	}

	@Override
	public List<CorporateShareholder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM corporateshareholder WHERE startupid = ?", new Object[] { startupId },
				this::mapRowToModel);
	}

}
