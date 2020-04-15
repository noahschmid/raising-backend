package ch.raising.data;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IRepository;
import ch.raising.models.Account;
import ch.raising.services.AccountService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class AccountRepository implements IRepository<Account> {

	private JdbcTemplate jdbc;
	private PasswordEncoder encoder;


	private static final Logger LOGGER = LoggerFactory.getLogger(AccountRepository.class);
	
	private final String ADD_ACCOUNT;
	private final String ADD_ADMIN;

	@Autowired
	public AccountRepository(JdbcTemplate jdbc, PasswordEncoder encoder) {
		this.jdbc = jdbc;
		this.encoder = encoder;
		this.ADD_ACCOUNT = "INSERT INTO account(firstname, lastname, companyname, password, emailhash, pitch, "
				+ "description, ticketminid, ticketmaxid, countryid, website, profilepictureid) VALUES"
				+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		this.ADD_ADMIN = "INSERT INTO account(firstname, lastname, emailhash, password, roles) VALUES (?,?,?,?,?)";
	}

	/**
	 * Get all accounts
	 * 
	 * @return list of all accounts
	 */
	public List<Account> getAll() throws SQLException, DataAccessException {
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
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public Account findByEmail(String email) throws EmailNotFoundException, DataAccessException, SQLException {
		List<Account> accounts = getAll();
		for (Account acc : accounts) {
			if (encoder.matches(email, acc.getEmail())) {
				acc.setEmail(email);
				return acc;
			}
		}
		throw new EmailNotFoundException("Email " + email + "was not found.");
	}

	/**
	 * Add a new account to the database
	 * 
	 * @param account the account to add
	 * @return
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	public long add(Account account) throws SQLException, DatabaseOperationException {
		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(ADD_ACCOUNT,
				Statement.RETURN_GENERATED_KEYS);

		String emailHash = encoder.encode(account.getEmail());
		String passwordHash = encoder.encode(account.getPassword());
		long ppicId = account.getProfilePictureId();
		int c = 1;
		ps.setString(c++, account.getFirstName());
		ps.setString(c++, account.getLastName());
		ps.setString(c++, account.getCompanyName());
		ps.setString(c++, passwordHash);
		ps.setString(c++, emailHash);
		ps.setString(c++, account.getPitch());
		ps.setString(c++, account.getDescription());
		ps.setInt(c++, account.getTicketMinId());
		ps.setInt(c++, account.getTicketMaxId());
		ps.setLong(c++, account.getCountryId());
		ps.setString(c++, account.getWebsite());
		if(ppicId != -1)
			ps.setLong(c++, ppicId);
		else
			ps.setNull(c++, java.sql.Types.BIGINT);
		if (ps.executeUpdate() > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				long insertedId = rs.getLong(1);
				ps.getConnection().close();
				return insertedId;
			}
		}
		throw new DatabaseOperationException("primary key could not be retreived");
	}

	/**
	 * Delete user account
	 * 
	 * @param id the id of the account
	 */
	public void delete(long id) throws SQLException, DataAccessException {
		String query = "DELETE FROM account WHERE id = ?;";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		});
	}

	/**
	 * Find user account by id
	 * 
	 * @param id id of the desired account
	 * @return instance of the found account
	 */
	public Account find(long id) throws SQLException, DataAccessException {
		Account account = jdbc.queryForObject("SELECT * FROM account WHERE id = ?", new Object[] { id },
				this::mapRowToModel);
		return account;
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
		return Account.accountBuilder().accountId(rs.getLong("id")).firstName(rs.getString("firstname"))
				.lastName(rs.getString("lastname")).companyName(rs.getString("companyName"))
				.pitch(rs.getString("pitch")).description(rs.getString("description")).email(rs.getString("emailHash"))
				.roles(rs.getString("roles")).ticketMaxId(rs.getInt("ticketmaxid"))
				.ticketMinId(rs.getInt("ticketminid")).password(rs.getString("password"))
				.countryId(rs.getLong("countryId")).website(rs.getString("website"))
				.profilePictureId(rs.getLong("profilepictureid")).build();
	}

	public long mapRowToId(ResultSet rs, int rowNum) throws SQLException {
		return rs.getLong("id");
	}

	/**
	 * Update user account
	 * 
	 * @param id  the id of the account to update
	 * @param req request containing fields to update
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws Exception
	 */
	public void update(long id, Account req) throws DataAccessException, SQLException {
		String emailHash = null;
		if (req.getEmail() != "") {
			emailHash = encoder.encode(req.getEmail());
		}
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "account", id);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getCompanyName(), "companyName");
		update.addField(emailHash, "emailhash");
		update.addField(req.getPitch(), "pitch");
		update.addField(req.getDescription(), "description");
		update.addField(req.getTicketMaxId(), "ticketmaxid");
		update.addField(req.getTicketMinId(), "ticketminid");
		update.addField(req.getWebsite(), "website");
		update.addField(req.getCountryId(), "countryId");
		update.execute();
	}

	public boolean isStartup(long id) throws SQLException, DataAccessException {
		try {
			long foundId = jdbc.queryForObject("SELECT accountid FROM startup WHERE accountid = ?", new Object[] { id },
					MapUtil::mapRowToAccountId);
			return foundId == id;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public boolean isInvestor(long id) throws SQLException, DataAccessException {
		try {
			long foundId = jdbc.queryForObject("SELECT accountid FROM investor WHERE accountid = ?",
					new Object[] { id }, MapUtil::mapRowToAccountId);
			return foundId == id;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public long registerAdmin(Account admin) throws SQLException, DatabaseOperationException {
		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(ADD_ADMIN,
				Statement.RETURN_GENERATED_KEYS);

		String emailHash = encoder.encode(admin.getEmail());
		String passwordHash = encoder.encode(admin.getPassword());
		int c = 1;
		ps.setString(c++, admin.getFirstName());
		ps.setString(c++, admin.getLastName());
		ps.setString(c++, passwordHash);
		ps.setString(c++, emailHash);
		ps.setString(c++, admin.getRoles());
		if (ps.executeUpdate() > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				long insertedId = rs.getLong(1);
				ps.getConnection().close();
				return insertedId;
			}
		}
		throw new DatabaseOperationException("primary key could not be retreived");
	}

}
