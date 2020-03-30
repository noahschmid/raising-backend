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
import ch.raising.models.Contact;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class ContactRepository implements IAdditionalInformationRepository<Contact> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public ContactRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public long getStartupIdByMemberId(long contactId) {
		return jdbc.queryForObject("SELECT * FROM contact WHERE id = ?", new Object[] { contactId }, this::mapRowToId);
	}

	@Override
	public Contact find(long id) {
		return jdbc.queryForObject("SELECT * FROM contact WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void addMemberByStartupId(Contact contact, long startupId) {
		jdbc.execute("INSERT INTO contact(startupid, firstname, lastname, position, email, phone) VALUES (?,?,?,?,?,?)",
				addByStartupId(contact, startupId));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM contact WHERE id = ?", deleteById(id));
	}

	@Override
	public Contact mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Contact.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).phone(rs.getString("phone"))
				.email(rs.getString("email")).position(rs.getString("position")).build();
	}

	@Override
	public PreparedStatementCallback<Boolean> addByStartupId(Contact contact, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, startupId);
				ps.setString(c++, contact.getFirstName());
				ps.setString(c++, contact.getLastName());
				ps.setString(c++, contact.getPosition());
				ps.setString(c++, contact.getEmail());
				ps.setString(c++, contact.getPhone());
				return ps.execute();
			}
		};
	}

	@Override
	public List<Contact> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM contact WHERE startupid = ?", new Object[] { startupId }, this::mapRowToModel);
	}

	@Override
	public void update(long id, Contact req) throws Exception {
		UpdateQueryBuilder update = new UpdateQueryBuilder("contact", id, this);
		update.setJdbc(jdbc);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getPosition(), "position");
		update.addField(req.getEmail(), "email");
		update.addField(req.getPhone(), "phone");
		update.execute();
	}
}
