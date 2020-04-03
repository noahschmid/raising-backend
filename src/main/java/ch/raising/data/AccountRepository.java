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

import ch.raising.interfaces.IRepository;
import ch.raising.models.Account;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class AccountRepository implements IRepository<Account> {

	private JdbcTemplate jdbc;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	public AccountRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Get all accounts
	 * 
	 * @return list of all accounts
	 */
	public List<Account> getAll() {
		String getAll = "SELECT * FROM account";
		List<Account> users = jdbc.query(getAll, this::mapRowToModel);
		return users;
	}

	/**
	 * Check whether a combination of given username and passwort exist in database
	 * 
	 * @param account account instance of the desired account
	 * @return true if account was found, false if no matching entry could be found
	 */
	public boolean accountExists(Account account) {
		String query = "SELECT * FROM account WHERE emailhash = ? AND password = ?";
		Object[] params = new Object[] { account.getEmail(), account.getPassword() };
		try {
			jdbc.queryForObject(query, params, this::mapRowToModel);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	/**
	 * Find user account by email
	 * 
	 * @param email the email to search for
	 * @return instance of the found user account
	 * @throws EmailNotFoundException
	 */
	public Account findByEmail(String email) throws EmailNotFoundException {
		List<Account> accounts = getAll();
		for (Account acc : accounts) {
			if (encoder.matches(email, acc.getEmail()))
				return acc;
		}
		for (Account acc : accounts) {
			if (email.equals(acc.getEmail()))
				return acc;
		}
		throw new EmailNotFoundException("Email " + email + "was not found.");
	}

	/**
	 * Find user account by emailhash
	 * 
	 * @param email the emailhash to search for
	 * @return instance of the found user account
	 * @throws EmailNotFoundException
	 */
	public Account findByEmailHash(String email) throws EmailNotFoundException {
		List<Account> accounts = getAll();
		for (Account acc : accounts) {
			if (email.equals(acc.getEmail()))
				return acc;
		}
		throw new EmailNotFoundException("Email " + email + "was not found.");
	}

	/**
	 * Add a new account to the database
	 * 
	 * @param account the account to add
	 * @return
	 */
	public long add(Account acc) throws DatabaseOperationException {
		try {
			String query = "INSERT INTO account(companyName, password, emailhash, pitch, description, ticketminid, ticketmaxid) VALUES (?, ?, ?, ?, ?, ?, ?)";
			String emailHash = encoder.encode(acc.getEmail());
			String passwordHash = encoder.encode(acc.getPassword());
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					int c = 1;
					ps.setString(c++, acc.getCompanyName());
					ps.setString(c++, passwordHash);
					ps.setString(c++, emailHash);
					ps.setString(c++, acc.getPitch());
					ps.setString(c++, acc.getDescription());
					ps.setInt(c++, acc.getTicketMinId());
					ps.setInt(c++, acc.getTicketMaxId());
					return ps.execute();
				}
			});
			return findByEmail(acc.getEmail()).getAccountId();
		} catch (EmailNotFoundException e) {
			e.printStackTrace();
			throw new DatabaseOperationException("could not find added account");
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseOperationException("could not add account: " + e.getMessage());
		}
	}

	/**
	 * Delete user account
	 * 
	 * @param tableEntryId the tableEntryId of the account
	 */
	public void delete(long id) {
		try {
			String query = "DELETE FROM account WHERE id = ?;";
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

					ps.setLong(1, id);
					return ps.execute();
				}
			});
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}

	/**
	 * Find user account by tableEntryId
	 * 
	 * @param tableEntryId tableEntryId of the desired account
	 * @return instance of the found account
	 */
	public Account find(long id) {
		try {
			Account account = jdbc.queryForObject("SELECT * FROM account WHERE id = ?", new Object[] { id },
					this::mapRowToModel);
			return account;
		} catch (DataAccessException e) {
			return null;
		}
	}

	/**
	 * Check whether given email already exists in the database
	 * 
	 * @param username the email to search for
	 * @return true if email already exists, false if email doesn't exist
	 */
	public boolean emailExists(String email) {
		List<Account> accounts = getAll();
		for (Account account : accounts) {
			if (encoder.matches(email, account.getEmail()))
				return true;
		}
		return false;
	}

	/**
	 * Map a row of a result set to an account instance
	 * 
	 * @param rs     result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Account instance of the result set
	 * @throws SQLException
	 */
	public Account mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return Account.accountBuilder().accountId(rs.getLong("id")).companyName(rs.getString("companyName"))
				.pitch(rs.getString("pitch")).description(rs.getString("description")).email(rs.getString("emailHash"))
				.roles(rs.getString("roles")).ticketMaxId(rs.getInt("ticketmaxid"))
				.ticketMinId(rs.getInt("ticketminid")).password(rs.getString("password")).build();
	}

	public long mapRowToId(ResultSet rs, int rowNum) throws SQLException {
		return rs.getLong("id");
	}

	/**
	 * Update user account
	 * 
	 * @param tableEntryId the tableEntryId of the account to update
	 * @param req          request containing fields to update
	 */
	public void update(long id, Account req) throws Exception {
		String emailHash = "";
		if (req.getEmail() != "") {
			emailHash = encoder.encode(req.getEmail());
		}
		try {
			UpdateQueryBuilder update = new UpdateQueryBuilder("account", id, this, "id");
			update.setJdbc(jdbc);
			update.addField(req.getCompanyName(), "companyName");
			update.addField(emailHash, "emailhash");
			update.addField(req.getPitch(), "pitch");
			update.addField(req.getDescription(), "description");
			update.addField(req.getTicketMaxId(), "ticketmaxid");
			update.addField(req.getTicketMinId(), "ticketminid");
			update.execute();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public boolean isStartup(long id) {
		try {
			long foundId = jdbc.queryForObject("SELECT accountid FROM startup WHERE accountid = ?", new Object[] { id },
					MapUtil::mapRowToAccountId);
			return foundId == id;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public boolean isInvestor(long id) {
		try {
			long foundId = jdbc.queryForObject("SELECT accountid FROM investor WHERE accountid = ?",
					new Object[] { id }, MapUtil::mapRowToAccountId);
			return foundId == id;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
}
