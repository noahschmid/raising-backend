package ch.raising.data;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	private final String ADD_ACCOUNT;

	@Autowired
	public AccountRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.ADD_ACCOUNT = "INSERT INTO account(companyname, password, emailhash, pitch, description, ticketminid, ticketmaxid, countryid, website) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
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
	 * Find user account by email does not care if hashed or not. It checks for
	 * both.
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
	 * @throws SQLException 
	 */
	public long add(Account account) throws DatabaseOperationException, SQLException {
			PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(ADD_ACCOUNT,
					Statement.RETURN_GENERATED_KEYS);
			
			String emailHash = encoder.encode(account.getEmail());
			String passwordHash = encoder.encode(account.getPassword());
			
			int c = 1;
			ps.setString(c++, account.getCompanyName());
			ps.setString(c++, passwordHash);
			ps.setString(c++, emailHash);
			ps.setString(c++, account.getPitch());
			ps.setString(c++, account.getDescription());
			ps.setInt(c++, account.getTicketMinId());
			ps.setInt(c++, account.getTicketMaxId());
			ps.setLong(c++, account.getCountryId());
			ps.setString(c++, account.getWebsite());
			if (ps.executeUpdate() > 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getLong(1);
					}
				}
			}
			throw new DatabaseOperationException("primary key could not be retreived");
	}

	/**
	 * Delete user account
	 * 
	 * @param id the id of the account
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
	 * Find user account by id
	 * 
	 * @param id id of the desired account
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
	 * Check whether given unhashed email already exists in the database
	 * 
	 * @param username the email to search for (unhashed)
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
				.ticketMinId(rs.getInt("ticketminid")).password(rs.getString("password"))
				.countryId(rs.getLong("countryId")).website(rs.getString("website")).build();
	}

	public long mapRowToId(ResultSet rs, int rowNum) throws SQLException {
		return rs.getLong("id");
	}

	/**
	 * Update user account
	 * 
	 * @param id  the id of the account to update
	 * @param req request containing fields to update
	 */
	public void update(long id, Account req) throws Exception {
		String emailHash = null;
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
			update.addField(req.getWebsite(), "website");
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

	public static PreparedStatementCallback<Boolean> getAddAccountCallback(Account acc, String emailHash,
			String passwordHash) {
		return new PreparedStatementCallback<Boolean>() {
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
				ps.setLong(c++, acc.getCountryId());
				ps.setString(c++, acc.getWebsite());
				return ps.execute();
			}
		};
	}


}
