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
import ch.raising.models.Founder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class FounderRepository implements IAdditionalInformationRepository<Founder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public FounderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public long getStartupIdByMemberId(long founderId) {
		return jdbc.queryForObject("SELECT * FROM founder WHERE id = ?", new Object[] { founderId }, this::mapRowToId);
	}

	@Override
	public Founder find(long id) {
		return jdbc.queryForObject("SELECT * FROM founder WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void addMemberByStartupId(Founder founder, long accountId) {
		jdbc.execute(
				"INSERT INTO founder(startupid, firstname, lastname, education, position, countryid) VALUES (?, ?,?,?,?,?)",
				addByStartupId(founder, accountId));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM founder WHERE id = ?", deleteById(id));
	}

	@Override
	public void update(long id, Founder req) throws Exception {
		UpdateQueryBuilder update = new UpdateQueryBuilder("founder", id, this);
		update.setJdbc(jdbc);
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
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).position(rs.getString("position")).countryId(rs.getLong("countryid"))
				.education(rs.getString("education")).build();
	}

	@Override
	public PreparedStatementCallback<Boolean> addByStartupId(Founder founder, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, accountId);
				ps.setString(c++, founder.getFirstName());
				ps.setString(c++, founder.getLastName());
				ps.setString(c++, founder.getEducation());
				ps.setString(c++, founder.getPosition());
				ps.setLong(c++, founder.getCountryId());
				return ps.execute();
			}
		};
	}

	@Override
	public List<Founder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM founder WHERE startupid = ?", new Object[] { startupId }, this::mapRowToModel);
	}

}
