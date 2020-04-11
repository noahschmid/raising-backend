package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.AccountRepository;
import ch.raising.models.Account;
import ch.raising.utils.EmailNotFoundException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { RepositoryTestConfig.class })
@ActiveProfiles("RepositoryTest")
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AccountRepositoryTest {

	private AccountRepository accountRepo;
	private JdbcTemplate jdbc;
	private BCryptPasswordEncoder encoder;
	
	private Account expected;
	private Account account;
	private String tableName;
	private String emailHash;
	private String passwordHash;

	protected long accountId = 1;
	private String firstName = "Marty";
	private String lastName = "Burt";
	protected String companyName = "Umbrella Corp";
	private String password = "testword";
	private String roles = "ROLE_SUPER_USER";
	private String email = "test@test.ch";
	private String pitch = "testpitch";
	private String description = "testcription";
	private String website = "testsite";
	private int ticketMinId = 3;
	private int ticketMaxId = 4;
	private long countryId = 123;
	private long profilePictureId = 14;

	@Autowired
	public AccountRepositoryTest(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.accountRepo = new AccountRepository(jdbc);
		this.encoder = new BCryptPasswordEncoder();
	}

	@BeforeEach
	public void setup() throws SQLException {
		tableName = "account";

		String createTable = QueryBuilder.getInstance().tableName(tableName).pair("id", Type.IDENTITY)
				.pair("lastname", Type.VARCHAR).pair("firstname", Type.VARCHAR).pair("pitch", Type.VARCHAR)
				.pair("description", Type.VARCHAR).pair("companyName", Type.VARCHAR).pair("password", Type.VARCHAR)
				.pair("roles", Type.VARCHAR).pair("emailhash", Type.VARCHAR).pair("ticketminid", Type.INT)
				.pair("ticketmaxid", Type.INT).pair("countryId", Type.BIGINT).pair("website", Type.VARCHAR)
				.pair("profilepictureid", Type.VARCHAR).createTable();
		jdbc.execute(createTable);

		makeAccounts();
	}

	@AfterEach
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

	public void makeAccounts() {
		emailHash = encoder.encode(email);
		passwordHash = encoder.encode(password);
		account = Account.accountBuilder().accountId(accountId).firstName(firstName).lastName(lastName).profilePictureId(profilePictureId).companyName(companyName).password(password).roles(roles)
				.email(email).pitch(pitch).description(description).ticketMinId(ticketMinId).ticketMaxId(ticketMaxId)
				.countryId(countryId).website(website).build();

		String sql = QueryBuilder.getInstance().tableName(tableName).attribute(
				"firstname, lastname, profilepictureid, pitch, description, companyName, password, roles, emailhash, ticketminid, ticketmaxid, countryid, website")
				.value(firstName).value(lastName).value(profilePictureId).value(pitch).value(description)
				.value(companyName).value(passwordHash).value(roles).value(emailHash).value(ticketMinId)
				.value(ticketMaxId).value(countryId).value(website).insert();

		jdbc.execute(sql);
		
		expected = new Account(account);
		expected.setEmail(emailHash);
		expected.setPassword(passwordHash);

	}

	@Test
	public void testGetAccount() throws DataAccessException, SQLException {
		Account found = accountRepo.find(1);
		assertNotNull(found);
		assertEquals(expected, found);
	
	}

	@Test
	public void findByEmailHash() throws EmailNotFoundException {
		Account found = accountRepo.findByEmailHash(emailHash);
		assertNotNull(found);
		assertEquals(expected, found);
	}

	@Test
	public void testAddAccount() throws Exception {
		account.setEmail(email);
		account.setPassword(password);
		accountRepo.add(account);
		account.setEmail(emailHash);
		account.setPassword(passwordHash);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "account"));
		Account found = accountRepo.find(2);
		assertNotNull(found);
		assertEquals(2, found.getAccountId());
		assertEquals(companyName, found.getCompanyName());
		assertTrue(encoder.matches(password, found.getPassword()));
		assertEquals(null, found.getRoles());
		assertTrue(encoder.matches(email, found.getEmail()));
		assertEquals(pitch, found.getPitch());
		assertEquals(description, found.getDescription());
		assertEquals(ticketMinId, found.getTicketMinId());
		assertEquals(ticketMaxId, found.getTicketMaxId());
		assertEquals(countryId, found.getCountryId());
		assertEquals(website, found.getWebsite());
	}

	@Test
	public void testGetAllAccounts() {
		List<Account> accounts = accountRepo.getAll();
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
	}

	@Test
	public void findByEmail() throws EmailNotFoundException {
		Account foundByMail = accountRepo.findByEmail(email);
		assertNotNull(foundByMail);
		account.setAccountId(1);
		account.setPassword(passwordHash);
		account.setEmail(emailHash);
		assertEquals(account, foundByMail);
	}

	@Test
	public void testDeleteAccount() throws DataAccessException, SQLException {
		accountRepo.delete(2);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

	@Test
	public void testUpdateAccount() throws Exception {
		String newMail = "testmail3";
		String newPassword = "newpassword";
		String newName = "aloysius pendergast";
		Account accup = new Account();
		accup.setEmail(newMail);
		accup.setPassword(newPassword);
		accup.setRoles("ROLE_TESTER");
		accup.setCompanyName(newName);
		accup.setWebsite("newsite");
		accountRepo.update(1, accup);
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("id", "1").select();
		Account found = jdbc.queryForObject(sql, MapUtil::mapRowToAccount);
		assertNotNull(found);
		assertTrue(encoder.matches(newMail, found.getEmail()));
		assertEquals(newName, found.getCompanyName());
		assertEquals("newsite", found.getWebsite());

	}

	@Test
	public void testCannotUpdatePassword() throws Exception {
		String newPassword = "123secure";
		String newPasswordHash = encoder.encode(newPassword);
		Account accup = new Account();
		accup.setPassword(newPassword);
		try{
			accountRepo.update(1, accup);
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("id", "1").select();
		Account found = jdbc.queryForObject(sql, MapUtil::mapRowToAccount);
		assertNotEquals(newPasswordHash, found.getPassword());
	}

	@Test
	public void testEmailNotDeletedIfNotProvided() throws Exception {
		String companyName = "Niesenbahnen";
		Account accup = new Account();
		accup.setCompanyName(companyName);
		accountRepo.update(1, accup);
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("id", "1").select();
		Account found = jdbc.queryForObject(sql, MapUtil::mapRowToAccount);
		assertNotEquals("", found.getEmail());
		assertNotNull(found.getEmail());
		assertEquals(emailHash, found.getEmail());
		assertEquals(companyName, found.getCompanyName());
	}
}
