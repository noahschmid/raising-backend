package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.Account;

@Repository
public class AccountRepository {
	
	private JdbcTemplate jdbc;
	
	@Autowired
	public AccountRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Get all accounts
	 * @return list of all accounts
	 */
	public List<Account> getAllAccounts() {
		String getAll = "SELECT * FROM account";
		List<Account> users = jdbc.query(getAll, this::mapRowToAccount);
		return users;
	}

	/**
	 * Check whether a combination of given username and passwort exist in database
	 * @param username username of the user
	 * @param password password of the user
	 * @return true if login was successful, false if no matching entry could be found
	 */
	public boolean login(String username, String password) {
		String query = "SELECT * FROM account WHERE username = ? AND password = ?";
		Object[] params = new Object[] { username, password };
		try {
            jdbc.queryForObject(query, params, this::mapRowToAccount);
            return true;
        } catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * Add a new account to the database
	 * @param acc
	 */
	public void add(Account acc) {
		acc.hashPassword();
		String sql = "INSERT INTO account(username, password) VALUES ('" + acc.getUsername() + "', '" + acc.getPassword() + "')";
		jdbc.execute(sql);
	}

	/**
	 * Delete user account
	 * @param id
	 */
	public void delete(int id) {
		jdbc.execute("DELETE FROM account WHERE id = " + id); 
	}
	
	/**
	 * Find user account by id
	 * @param id id of the desired account
	 * @return instance of the found account
	 */
	public Account find(int id) {
		return jdbc.queryForObject("SELECT * FROM account WHERE id = ?", new Object[] { id }, this::mapRowToAccount);
	}

	/**
	 * Check whether given username already exists in the database
	 * @param username the username to search for
	 * @return true if username already exists, false if username doesn't exist
	 */
	public boolean usernameExists(String username) {
		String query = "SELECT * FROM account WHERE username = ?";
		Object[] params = new Object[] { username };
		try {
            jdbc.queryForObject(query, params, this::mapRowToAccount);
            return true;
        } catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * Map a row of a result set to an account instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Account instance of the result set
	 * @throws SQLException
	 */
	private Account mapRowToAccount(ResultSet rs, int rowNum) throws SQLException {
		return new Account(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
	}

}
