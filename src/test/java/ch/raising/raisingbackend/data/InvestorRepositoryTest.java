package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Date;
import java.sql.SQLException;
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
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.InvestorRepository;
import ch.raising.models.Investor;
import ch.raising.models.Startup;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class InvestorRepositoryTest {

	private JdbcTemplate jdbc;
	private InvestorRepository invRepo;
	private Investor inv;
	private String tableName;

	@Autowired
	public InvestorRepositoryTest(JdbcTemplate jdbc) {
		this.invRepo = new InvestorRepository(jdbc);
		this.jdbc = jdbc;
		this.tableName = "investor";
	}

	@BeforeEach
	public void setup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("accountid", Type.SERIAL)
				.pair("investorTypeid", Type.INT).createTable();

		jdbc.execute(sql);

		inv = Investor.investorBuilder().investorTypeId(7).build();
		addInvestor();
	}

	@AfterEach
	public void cleanup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).dropTable(tableName);
		jdbc.execute(sql);
	}

	public void addInvestor() {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("investorTypeId").value("7").insert();

		jdbc.execute(sql);
	}

	@Test
	public void testFind() throws DataAccessException, SQLException, DatabaseOperationException {
		Investor found = invRepo.find(1);
		assertNotNull(found);
		assertEquals(found, inv);
	}

	@Test
	public void testGetAll() throws DataAccessException, SQLException {
		List<Investor> foundList = invRepo.getAll();
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		assertEquals(inv, foundList.get(0));
	}

	@Test
	public void testUpdate() throws Exception {
		Investor update = Investor.investorBuilder().investorTypeId(4).build();
		invRepo.update(1, update);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, tableName));
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEquals("accountid", "1").select();
		Investor found = jdbc.queryForObject(sql, MapUtil::mapRowToInvestor);
		assertEquals(1, found.getAccountId());
		assertEquals(update.getInvestorTypeId(), found.getInvestorTypeId());
	}

	@Test
	public void testAdd() {
		invRepo.add(inv);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

}
