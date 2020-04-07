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

@ContextConfiguration(classes = {RepositoryTestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class StartupRepositoryTest {

	private JdbcTemplate jdbc;
	private StartupRepository suRepo;
	private final String tableName;

	private Startup su;
	private Startup su2;

	@Autowired
	public StartupRepositoryTest(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.suRepo = new StartupRepository(jdbc);
		this.tableName = "startup";
	}

	@BeforeEach
	public void setup() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("accountid", Type.SERIAL)
				.pair("boosts", Type.INT).pair("numberoffte", Type.INT)
				.pair("breakevenyear", Type.INT).pair("premoneyvaluation", Type.INT)
				.pair("closingtime", Type.DATE).pair("financetypeid", Type.INT).pair("investmentphaseid", Type.INT)
				.pair("revenuemaxid", Type.INT).pair("revenueminid", Type.INT).pair("scope", Type.INT)
				.pair("uid", Type.VARCHAR).pair("foundingyear", Type.INT).pair("raised", Type.INT).createTable();

		jdbc.execute(sql);

		su = Startup.startupBuilder().numberOfFte(2).breakEvenYear(2025)
				.preMoneyEvaluation(1234).closingTime(Date.valueOf("2020-10-10")).financeTypeId(6).investmentPhaseId(5)
				.revenueMaxId(22).revenueMinId(20).scope(8).uId("CH-132").foundingYear(1997).raised(100).build();
		addStartup();
	}

	@AfterEach
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

	public void addStartup() {
		String sql = QueryBuilder.getInstance().tableName(tableName)
				.attribute("numberoffte, breakevenyear")
				.attribute("premoneyvaluation, closingtime, financetypeid, investmentphaseid")
				.attribute("revenuemaxid, revenueminid, scope, uid, foundingyear, raised")
				.value("" + su.getNumberOfFte())
				.value("" + su.getBreakEvenYear()).value("" + su.getPreMoneyValuation())
				.value(su.getClosingTime().toString()).value("" + su.getFinanceTypeId())
				.value("" + su.getInvestmentPhaseId()).value("" + su.getRevenueMaxId()).value("" + su.getRevenueMinId())
				.value("" + su.getScope()).value("" + su.getUId()).value("" + su.getFoundingYear())
				.value("" + su.getRaised()).insert();

		jdbc.execute(sql);
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

	@Test
	public void testUpdate() throws Exception {
		Date date = new Date(1633816800000L);

		Startup update = Startup.startupBuilder().accountId(1).investmentPhaseId(1).breakEvenYear(1234)
				.numberOfFte(145).preMoneyEvaluation(12).revenueMaxId(1).revenueMinId(2).scope(45)
				.uId("CH321").foundingYear(3456).financeTypeId(12).raised(0).closingTime(date).build();
		suRepo.update(1, update);
		String sql = QueryBuilder.getInstance().tableName("startup").whereEquals("accountid", "1").select();
		Startup found = jdbc.queryForObject(sql, suRepo::mapRowToModel);
		assertEquals(update.getAccountId(), found.getAccountId());
		assertEquals(update.getNumberOfFte(), found.getNumberOfFte());
	}

	@Test
	public void testAdd() throws Exception {
		Startup su = Startup.startupBuilder().boosts(0).numberOfFte(1)
				.breakEvenYear(2000).preMoneyEvaluation(10000).closingTime(Date.valueOf("2021-10-10")).financeTypeId(3)
				.investmentPhaseId(4).revenueMaxId(5).revenueMinId(6).scope(7).uId("DE-9999").foundingYear(2020)
				.build();
		suRepo.add(su);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, tableName));
	}

}
