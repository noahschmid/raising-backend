package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Contact;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class ContactRepository implements IAdditionalInformationRepository<Contact, UpdateQueryBuilder>{
	
	@Autowired
	JdbcTemplate jdbc;
	
	
	@Autowired
	public ContactRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Deletes the specified Contact by id
	 * 
	 * @param id
	 */
	public void deleteContactByIdByStartupId(long id) {
		jdbc.execute("DELETE FROM contact WHERE id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		});

	}

	public void addContactByStartupId(Contact contact) {
		jdbc.execute("INSERT INTO contact(id, startupid, name, role, email, phone) VALUES (?,?,?,?,?,?)",
				new PreparedStatementCallback<Boolean>() {
					@Override
					public Boolean doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ps.setLong(1, contact.getId());
						ps.setLong(2, contact.getStartupId());
						ps.setString(3, contact.getName());
						ps.setString(4,  contact.getRole());
						ps.setString(5, contact.getEmail());
						ps.setString(6, contact.getPhone());
						return ps.execute();
					}
				});
	}

	@Override
	public long getStartupIdOfTableById(long contactId) {
		return jdbc.queryForObject("SELECT startupid FROM contact WHERE id = ?", new Object[] {contactId}, this::mapRowToId);
	}

	@Override
	public Contact find(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Contact sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(long id) {
		// TODO Auto-generated method stub
		
	}
}
