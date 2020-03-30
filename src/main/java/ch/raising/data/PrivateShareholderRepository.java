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
public class PrivateShareholderRepository implements IAdditionalInformationRepository<PrivateShareholder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public PrivateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public PrivateShareholder find(long id) {
		return jdbc.queryForObject("SELECT * FROM privateshareholder WHERE id = ?", new Object[] { id },
				this::mapRowToModel);
	}

	@Override
	public PrivateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return PrivateShareholder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).city(rs.getString("city"))
				.equityShare(rs.getInt("equityshare")).investortypeId(rs.getLong("investortypeid"))
				.countryId(rs.getLong("countryid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) {
		return jdbc.queryForObject("SELECT * FROM privateshareholder WHERE id = ?",
				new Object[] { id }, this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(PrivateShareholder sumem, long startupId) {
		jdbc.execute(
				"INSERT INTO privateshareholder(startupid, firstname, lastname, city, equityshare, investortypeid, countryid) VALUES (?,?,?,?,?,?,?)",
				addByStartupId(sumem, startupId));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM privateshareholder WHERE id = ?", deleteById(id));
	}

	@Override
	public PreparedStatementCallback<Boolean> addByStartupId(PrivateShareholder psh, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, startupId);
				ps.setString(c++, psh.getFirstName());
				ps.setString(c++, psh.getLastName());
				ps.setString(c++, psh.getCity());
				ps.setInt(c++, psh.getEquityShare());
				ps.setLong(c++, psh.getInvestortypeId());
				ps.setLong(c++, psh.getCountryId());
				return ps.execute();
			}
		};
	}

	@Override
	public List<PrivateShareholder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM privateshareholder WHERE startupid = ?", new Object[] { startupId },
				this::mapRowToModel);
	}

	@Override
	public void update(long id, PrivateShareholder req) throws Exception {
		UpdateQueryBuilder update = new UpdateQueryBuilder("privateshareholder", id, this);
		update.setJdbc(jdbc);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getCity(), "city");
		update.addField(req.getEquityShare(), "equityshare");
		update.addField(req.getInvestortypeId(), "investortypeid");
		update.addField(req.getCountryId(), "countryid");
	}

}
