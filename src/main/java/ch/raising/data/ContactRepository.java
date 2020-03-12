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
	public void deleteContactById(int id) {
		jdbc.execute("DELETE FROM contact WHERE id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setInt(1, id);
				return ps.execute();
			}
		});

	}

	public void addContact(Contact contact) {
		jdbc.execute("INSERT INTO contact(id, startupid, name, role, email, phone) VALUES (?,?,?,?,?,?)",
				new PreparedStatementCallback<Boolean>() {
					@Override
					public Boolean doInPreparedStatement(PreparedStatement ps)
							throws SQLException, DataAccessException {
						ps.setInt(1, contact.getId());
						ps.setInt(2, contact.getStartupId());
						ps.setString(3, contact.getName());
						ps.setString(4,  contact.getRole());
						ps.setString(5, contact.getEmail());
						ps.setString(6, contact.getPhone());
						return ps.execute();
					}
				});
	}

	@Override
	public int getStartupIdOfTableById(int contactId) {
		return jdbc.queryForObject("SELECT startupid FROM contact WHERE id = ?", new Object[] {contactId}, this::mapRowToId);
	}

	@Override
	public Contact find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(int id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Contact sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(int id) {
		// TODO Auto-generated method stub
		
	}
}
