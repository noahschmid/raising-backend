package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import ch.raising.data.CorporateShareholderRepository;
import ch.raising.models.CorporateShareholder;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class CorporateShareholderRepositoryTest implements IAdditionalInformationTest {

	@Autowired
	JdbcTemplate jdbc;

	CorporateShareholderRepository psr;

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long countryId;

	@BeforeAll
	@Override
	public void setup() {
		psr = new CorporateShareholderRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("corporateshareholder").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("name", Type.VARCHAR)
				.pair("website", Type.VARCHAR).pair("equityshare", Type.INT).pair("corporatebodyid", Type.BIGINT)
				.pair("countryid", Type.BIGINT).createTable();
		jdbc.execute(sql);
	}

	@BeforeEach
	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("corporateshareholder")
				.attribute("startupid, name, website, equityShare, corporatebodyid, countryid").value("2")
				.value("Pendergast").value("soreal.ch").value("100").value("12").value("456").insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("corporateshareholder").whereEquals("name", "Pendergast").select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterAll
	@Override
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "corporateshareholder");
	}

	@AfterEach
	@Override
	public void deleteMember() {
		JdbcTestUtils.deleteFromTables(jdbc, "corporateshareholder");
		jdbc.execute("ALTER SEQUENCE corporateshareholder_id_seq RESTART WITH 1");
	}

	@Test
	@Override
	public void testGetStartupByMemberId() {
		long suId = psr.getStartupIdByMemberId(1);
		assertEquals(2, suId);
	}

	@Test
	@Override
	public void testAddMemberByStartupId() {
		CorporateShareholder psh = CorporateShareholder.builder().startupId(7).corpName("D Agosta")
				.website("soreal.ch").equityShare(99).corporateBodyId(1).countryId(768).build();
		psr.addMemberByStartupId(psh, 7);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "corporateshareholder"));

		String sql = QueryBuilder.getInstance().tableName("corporateshareholder").whereEquals("name", "D Agosta")
				.select();
		CorporateShareholder added = jdbc.queryForObject(sql, MapUtil::mapRowToCorporateShareholder);
		assertEquals(2, added.getId());
		assertEquals(7, added.getStartupId());
		assertEquals("D Agosta", added.getCorpName());
		assertEquals("soreal.ch", added.getWebsite());
		assertEquals(99, added.getEquityShare());
		assertEquals(1, added.getCorporateBodyId());
		assertEquals(768, added.getCountryId());
	}

	@Test
	@Override
	public void testDeleteMemberByStartupId() {
		psr.deleteMemberByStartupId(2);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "corporateshareholder"));
	}

	@Test
	@Override
	public void testFindByStartupId() {
		List<CorporateShareholder> foundList = psr.findByStartupId(2);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		CorporateShareholder found = foundList.get(0);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Pendergast", found.getCorpName());
		assertEquals("soreal.ch", found.getWebsite());
		assertEquals(100, found.getEquityShare());
		assertEquals(12, found.getCorporateBodyId());
		assertEquals(456, found.getCountryId());
	}

	@Test
	@Override
	public void testFind() {
		CorporateShareholder found = psr.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Pendergast", found.getCorpName());
		assertEquals("soreal.ch", found.getWebsite());
		assertEquals(100, found.getEquityShare());
		assertEquals(12, found.getCorporateBodyId());
		assertEquals(456, found.getCountryId());
	}

}
