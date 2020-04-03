package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
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

@ContextConfiguration(classes = {RepositoryTestConfig.class })
@ActiveProfiles("RepositoryTest")
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class AccountRepositoryTest {

	private AccountRepository accountRepo;
	private JdbcTemplate jdbc;
	private BCryptPasswordEncoder encoder;

	long id = -1;
	Account account;
	Account account2;
	String tableName;
	String emailhash;
	String email;
	String name;

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
				.pair("pitch", Type.VARCHAR).pair("description", Type.VARCHAR).pair("companyName", Type.VARCHAR)
				.pair("password", Type.VARCHAR).pair("roles", Type.VARCHAR)
				.pair("emailhash", Type.VARCHAR).pair("ticketminid", Type.INT).pair("ticketmaxid", Type.INT)
				.createTable();
		jdbc.execute(createTable);
		
		makeAccounts();
	}

	@AfterEach
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

	
	public void makeAccounts() {
		email = "testmail";
		emailhash = encoder.encode(email);
		name = "testname";
		account = Account.accountBuilder().accountId(1).companyName(name).password("testpassword").email(emailhash).build();
		account2 = Account.accountBuilder().companyName("testname2").password("testpasswordw")
				.email(encoder.encode("testmanil2")).build();

		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("companyName").attribute("password")
				.attribute("emailhash").value(name).value("testpassword").value(emailhash).insert();
		jdbc.execute(sql);

		sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("account.companyName", name).select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@Test
	public void testGetAccount() {
		assertNotEquals(-1, id);
		Account account = accountRepo.find(id);
		assertNotNull(account);
	}

	@Test
	public void findByEmailHash() throws EmailNotFoundException {
		Account found = accountRepo.findByEmailHash(emailhash);
		assertNotNull(found);
		assertEquals(1, found.getAccountId());
	}

	@Test
	public void testAddAccount() throws Exception {
		accountRepo.add(account2);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "account"));
	}

	@Test
	public void testGetAllAccounts() {
		List<Account> accounts = accountRepo.getAll();
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
	}

	@Test
	public void testAccountExists() {
		assertTrue(accountRepo.accountExists(account));
	}

	@Test
	public void findByEmail() throws EmailNotFoundException {
		Account foundByMail = accountRepo.findByEmail(email);
		assertNotNull(foundByMail);
		assertEquals(account, foundByMail);
	}

	@Test
	public void testDeleteAccount() {
		accountRepo.delete(2);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

	@Test
	public void testEmailExists() {
		assertTrue(accountRepo.emailExists(email));
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
		accountRepo.update(1, accup);
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("id", "1").select();
		Account found = jdbc.queryForObject(sql, MapUtil::mapRowToAccount);
		assertNotNull(found);
		assertTrue(encoder.matches(newMail, found.getEmail()));
		assertEquals(newName, found.getCompanyName());

	}
}
