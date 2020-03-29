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

import ch.raising.data.StartupRepository;
import ch.raising.models.Startup;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { StartupRepository.class, TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class StartupRepositoryTest {

	private JdbcTemplate jdbc;
	private StartupRepository suRepo;
	private final String tableName;

	private Startup su;
	private Startup su2;
	
	@Autowired
	public StartupRepositoryTest(JdbcTemplate jdbc, StartupRepository suRepo) {
		this.jdbc = jdbc;
		this.suRepo = suRepo;
		this.tableName = "startup";
	}

	@BeforeAll
	public void setup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("accountid", Type.SERIAL)
				.pair("boosts", Type.INT).pair("numberoffte", Type.INT).pair("turnover", Type.INT)
				.pair("street", Type.VARCHAR).pair("city", Type.VARCHAR).pair("website", Type.VARCHAR)
				.pair("breakevenyear", Type.INT).pair("zipcode", Type.INT).pair("premoneyvaluation", Type.INT)
				.pair("closingtime", Type.DATE).pair("financetypeid", Type.INT).pair("investmentphaseid", Type.INT)
				.pair("revenuemaxid", Type.INT).pair("revenueminid", Type.INT).pair("scope", Type.INT)
				.pair("uid", Type.VARCHAR).pair("foundingyear", Type.INT).pair("raised", Type.INT)
				.createTable();

		jdbc.execute(sql);
		
		su = Startup.startupBuilder().numberOfFte(2).turnover(1).street("Chumgässli").city("Aeschi").website("soreal.ch").breakEvenYear(2025)
				.zipCode(3703).preMoneyEvaluation(1234).closingTime(Date.valueOf("2020-10-10")).financeTypeId(6).investmentPhaseId(5).revenueMaxId(22).revenueMinId(20).scope(8)
				.uId("CH-132").foundingYear(1997).raised(100).build();

	}

	@AfterAll
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

	@BeforeEach
	public void addStartup() {
		String sql = QueryBuilder.getInstance().tableName(tableName)
				.attribute("numberoffte, turnover, street, city, website, breakevenyear, zipcode")
				.attribute("premoneyvaluation, closingtime, financetypeid, investmentphaseid")
				.attribute("revenuemaxid, revenueminid, scope, uid, foundingyear, raised")
				.value(""+su.getNumberOfFte())
				.value(""+su.getTurnover())
				.value(""+su.getStreet())
				.value(""+su.getCity())
				.value(""+su.getWebsite())
				.value(""+su.getBreakEvenYear())
				.value(""+su.getZipCode())
				.value(""+su.getPreMoneyValuation())
				.value(su.getClosingTime().toString())
				.value(""+su.getFinanceTypeId())
				.value(""+su.getInvestmentPhaseId())
				.value(""+su.getRevenueMaxId())
				.value(""+su.getRevenueMinId())
				.value(""+su.getScope())
				.value(""+su.getUId())
				.value(""+su.getFoundingYear())
				.value(""+su.getRaised())
				.insert();

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
		Startup found = suRepo.find(1);
		assertNotNull(found);
		assertEquals(su, found);
	}

	@Test
	public void testGetAll() {
		List<Startup> suList = suRepo.getAll();
		assertNotNull(suList);
		assertEquals(1, suList.size());
		assertEquals(su, suList.get(0));

	}

	// @Test
	public void testUpdate() {

	}

	@Test
	public void testAdd() throws Exception {
		Startup su = Startup.startupBuilder().boosts(0).numberOfFte(1).turnover(2).street("fischermätteli").city("hood")
				.website("gang.ch").breakEvenYear(2000).zipCode(3000).preMoneyEvaluation(10000)
				.closingTime(Date.valueOf("2021-10-10")).financeTypeId(3).investmentPhaseId(4).revenueMaxId(5)
				.revenueMinId(6).scope(7).uId("DE-9999").foundingYear(2020).build();
		suRepo.add(su);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

}
