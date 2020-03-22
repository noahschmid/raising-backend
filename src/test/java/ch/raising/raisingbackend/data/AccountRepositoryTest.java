package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@ContextConfiguration(classes = { AccountRepository.class, TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountRepositoryTest {

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	JdbcTemplate jdbc;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	long id = -1;
	Account account;
	Account account2;
	String tableName;
	String emailhash;
	String email;
	String name;

	@BeforeAll
	public void setup() {
		tableName = "account";

		String createTable = QueryBuilder.getInstance().tableName(tableName).pair("id", Type.SERIAL)
				.pair("name", Type.VARCHAR).pair("password", Type.VARCHAR).pair("roles", Type.VARCHAR).pair("emailhash", Type.VARCHAR)
				.pair("investmentmin", Type.INT).pair("investmentmax", Type.INT).createTable();

		jdbc.execute(createTable);

	}

	@AfterAll
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

	@BeforeEach
	public void makeAccounts() {
		email = "testmail";
		emailhash = encoder.encode(email);
		name = "testname";
		account = Account.accountBuilder().accountId(1).name(name).password("testpassword").email(emailhash).build();
		account2 = Account.accountBuilder().name("testname2").password("testpasswordw")
				.email(encoder.encode("testmanil2")).build();

		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("name").attribute("password")
				.attribute("emailhash").value(name).value("testpassword").value(emailhash).insert();
		jdbc.execute(sql);

		sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("account.name", name).select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterEach
	public void cleanAccounts() {
		JdbcTestUtils.deleteFromTables(jdbc, tableName);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, tableName));
		jdbc.execute("ALTER SEQUENCE "+tableName+"_id_seq RESTART WITH 1");
	}

	@AfterEach
	public void deleteAccounts() {
		JdbcTestUtils.deleteFromTables(jdbc, tableName);
	}

	@Test
	public void testGetAccount() {
		assertNotEquals(-1, id);
		Account account = accountRepo.find(id);
		assertNotNull(account);
		assertEquals("testname", account.getName());
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
	public void findByEmail() throws EmailNotFoundException{
		Account foundByMail = accountRepo.findByEmail(email);
		assertNotNull(foundByMail);
		assertEquals(account, foundByMail);
	}

	//@Test
	public void testAddAccountNotUniqeMail() throws Exception {
		Account sameMail = Account.accountBuilder().name("testname3").email(email).password("testpw").build();
		try{
			accountRepo.add(sameMail);
			fail("should not work");
		}catch(Exception e) {}
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, tableName));
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

	//@Test
	public void testUpdateAccount() throws Exception {
		String newMail = "testmail3";
		String newMailHash = encoder.encode(newMail);
		String newPassword = "newpassword";
		String newPasswordHash = encoder.encode(newPassword);
		String newName = "aloysius pendergast";
		Account accup = new Account();
		accup.setEmail(newMail);
		accup.setPassword(newPassword);
		accup.setRoles("ROLE_TESTER");
		accup.setName(newName);
		accountRepo.update(1, accup);
		String sql = QueryBuilder.getInstance().tableName(tableName)
				.whereEquals("emailhash", newMailHash).select();
		Account updated = jdbc.queryForObject(sql, MapUtil::mapRowToAccount);
		assertNotNull(updated);
		assertEquals(newName, updated.getName());
	}
}
