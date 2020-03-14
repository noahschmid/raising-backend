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

import ch.raising.models.Boardmember;
import ch.raising.models.Contact;
import ch.raising.utils.NotImplementedException;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class ContactRepository implements IAdditionalInformationRepository<Contact, UpdateQueryBuilder> {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	public ContactRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public long getStartupIdByMemberId(long contactId) {
		return jdbc.queryForObject("SELECT startupid FROM contact WHERE id = ?", new Object[] { contactId },
				this::mapRowToId);
	}

	@Override
	public Contact find(long id) {
		return jdbc.queryForObject("SELECT * FROM contact WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public void addMemberByStartupId(Contact contact, long startupId) {
		jdbc.execute("INSERT INTO contact(id, startupid, name, role, email, phone) VALUES (?,?,?,?,?,?)",
				addByStartupId(contact, startupId));
	}
	
	@Override
	public void addMemberByStartupId(Contact sumem) {
		jdbc.execute("INSERT INTO contact(id, startupid, name, role, email, phone) VALUES (?,?,?,?,?,?)",
				addByMember(sumem));
	}

	@Override
	public void deleteMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM contact WHERE id = ?", deleteById(id));
	}

	@Override
	public Contact mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Contact.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid")).name(rs.getString("name"))
				.phone(rs.getString("phone")).email(rs.getString("email")).role(rs.getString("role")).build();
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
	public PreparedStatementCallback<Boolean> addByStartupId(Contact contact, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, contact.getId());
				ps.setLong(2, startupId);
				ps.setString(3, contact.getName());
				ps.setString(4, contact.getRole());
				ps.setString(5, contact.getEmail());
				ps.setString(6, contact.getPhone());
				return ps.execute();
			}
		};
	}
	
	@Override
	public PreparedStatementCallback<Boolean> addByMember(Contact contact) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, contact.getId());
				ps.setLong(2, contact.getStartupId());
				ps.setString(3, contact.getName());
				ps.setString(4, contact.getRole());
				ps.setString(5, contact.getEmail());
				ps.setString(6, contact.getPhone());
				return ps.execute();
			}
		};
	}


	public Contact findByStartupId(long startupId) {
		return jdbc.queryForObject("SELECT * FROM contact WHERE startupid = ?", new Object[] { startupId }, 
				this::mapRowToModel);
	}
}
