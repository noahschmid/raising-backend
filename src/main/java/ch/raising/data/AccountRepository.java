package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import ch.raising.models.Account;
import ch.raising.models.AccountUpdateRequest;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class AccountRepository implements IRepository<Account, AccountUpdateRequest> {
	
	private JdbcTemplate jdbc;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
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
	 * @param account account instance of the desired account
	 * @return true if account was found, false if no matching entry could be found
	 */
	public boolean accountExists(Account account) {
		String query = "SELECT * FROM account WHERE username = ? AND password = ?";
		Object[] params = new Object[] { account.getUsername(), account.getPassword() };
		try {
            jdbc.queryForObject(query, params, this::mapRowToAccount);
            return true;
        } catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * Find accounts by email hash
	 * @param emailHash the email hash to search for
	 */
	public Account findByEmailHash(String emailHash) {
		List<Account> accounts = getAllAccounts();
		
		for(Account acc : accounts) {
			if(encoder.matches(emailHash, acc.getEmailHash()))
				return acc;
		}

		return null;
	}

	/**
	 * Add a new account to the database
	 * @param acc the account to add
	 */
	public void add(Account acc) throws Exception {
		if(findByEmailHash(encoder.encode(acc.getEmailHash())) != null)
			throw new Error("Account with same email already exists!");
		try {
			String query = "INSERT INTO account(username, password, emailHash) VALUES (?, ?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setString(1,acc.getUsername());  
					ps.setString(2,encoder.encode(acc.getPassword()));
					ps.setString(3,encoder.encode(acc.getEmailHash()));
						
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}

	/**
	 * Delete user account
	 * @param id the id of the account
	 */
	public void delete(int id) throws Exception {
		try {
			String query = "DELETE FROM account WHERE id = ?;"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setInt(1,id); 	
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
	
	/**
	 * Find user account by id
	 * @param id id of the desired account
	 * @return instance of the found account
	 */
	public Account find(int id) {
		try {
			Account account = jdbc.queryForObject("SELECT * FROM account WHERE id = ?", new Object[] { id }, this::mapRowToAccount);
			return account;
		} catch(DataAccessException e) {
			return null;
		}
	}

	/**
	 * Find user account by username
	 * @param username the username to search for
	 * @return instance of the found user account
	 */
	public Account findByUsername(String username) {
		try {
			Account account = jdbc.queryForObject("SELECT * FROM account WHERE username = ?", new Object[] { username }, this::mapRowToAccount);
			return account;
		} catch(DataAccessException e) {
			return null;
		}
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
		return new Account(rs.getInt("id"), 
			rs.getString("username"), 
			rs.getString("password"),
			rs.getString("roles"),
			rs.getString("emailHash"));
	}

	/**
	 * Update user account
	 * @param id the id of the account to update
	 * @param req request containing fields to update
	 */
	public void update(int id, AccountUpdateRequest req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("account", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getUsername(), "username");
			updateQuery.addField(req.getPassword(), "password");
			updateQuery.addField(req.getRoles(), "roles");

			updateQuery.addField(req.getEmailHash(), "emailHash");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
