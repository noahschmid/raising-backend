package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Date;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.InvestorRepository;
import ch.raising.models.Investor;
import ch.raising.models.Startup;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { InvestorRepository.class, TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class InvestorRepositoryTest {
	
	private JdbcTemplate jdbc;
	private InvestorRepository invRepo;
	private Investor inv;
	private String tableName;
	@Autowired
	public InvestorRepositoryTest(InvestorRepository invRepo, JdbcTemplate jdbc) {
		this.invRepo = invRepo;
		this.jdbc =jdbc;
		this.tableName = "investor";
	}
	@BeforeAll
	public void setup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("accountid", Type.SERIAL).pair("investorTypeid", Type.INT).createTable();

		jdbc.execute(sql);
	
		inv = Investor.investorBuilder().investorTypeId(7).build();

	}

	@AfterAll
	public void cleanup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).dropTable(tableName);
		jdbc.execute(sql);
	}

	@BeforeEach
	public void addInvestor() {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("investorTypeId").value("7").insert();

		jdbc.execute(sql);
	}

	@AfterEach
	public void cleanAccounts() {
		JdbcTestUtils.deleteFromTables(jdbc, tableName);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, tableName));
		jdbc.execute("ALTER SEQUENCE "+tableName+"_accountid_seq RESTART WITH 1");
	}
	
	@Test
	public void testFind() {
		Investor found = invRepo.find(1);
		assertNotNull(found);
		assertEquals(found, inv);
	}
	@Test
	public void testGetAll() {
		List<Investor> foundList = invRepo.getAll();
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		assertEquals(inv, foundList.get(0));
	}
	//@Test 
	public void testUpdate() {
		
	}
	@Test
	public void testAdd() {
		invRepo.add(inv);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

}
