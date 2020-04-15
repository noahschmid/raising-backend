package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import ch.raising.data.CorporateShareholderRepository;
import ch.raising.models.CorporateShareholder;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class CorporateShareholderRepositoryTest implements IAdditionalInformationTest {

	@Autowired
	JdbcTemplate jdbc;

	CorporateShareholderRepository csr;

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long countryId;

	@BeforeEach
	@Override
	public void setup() {
		csr = new CorporateShareholderRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("corporateshareholder").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("name", Type.VARCHAR).pair("website", Type.VARCHAR)
				.pair("equityshare", Type.INT).pair("corporatebodyid", Type.BIGINT).pair("countryid", Type.BIGINT)
				.createTable();
		jdbc.execute(sql);
		addMember();
	}

	
	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("corporateshareholder")
				.attribute("startupid, name, website, equityShare, corporatebodyid, countryid").value("2")
				.value("Pendergast").value("soreal.ch").value("100").value("12").value("456").insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("corporateshareholder").whereEquals("name", "Pendergast").select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterEach
	@Override
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "corporateshareholder");
	}

	@Test
	@Override
	public void testGetStartupIdByMemberId() throws DataAccessException, SQLException {
		long suId = csr.getStartupIdByMemberId(1);
		assertEquals(2, suId);
	}

	@Test
	@Override
	public void testAddMemberByStartupId() throws DataAccessException, SQLException {
		CorporateShareholder psh = CorporateShareholder.builder().startupId(7).corpName("D Agosta").website("soreal.ch")
				.equityShare(99).corporateBodyId(1).countryId(768).build();
		csr.addMemberByStartupId(psh, 7);
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
	public void testDeleteMemberById() throws DataAccessException, SQLException {
		csr.deleteMemberById(2);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "corporateshareholder"));
	}

	@Test
	@Override
	public void testFindByStartupId() {
		List<CorporateShareholder> foundList = csr.findByStartupId(2);
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
	public void testFind() throws DataAccessException, SQLException {
		CorporateShareholder found = csr.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Pendergast", found.getCorpName());
		assertEquals("soreal.ch", found.getWebsite());
		assertEquals(100, found.getEquityShare());
		assertEquals(12, found.getCorporateBodyId());
		assertEquals(456, found.getCountryId());
	}

	@Override
	public void testupdate() throws Exception {
		CorporateShareholder csh = CorporateShareholder.builder().startupId(234).corpName("umbrellaCorp").website("soreal.ch")
				.equityShare(12).corporateBodyId(13).countryId(123).build();
		csr.update(1, csh);

		String sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("id", "1").select();
		CorporateShareholder found = jdbc.queryForObject(sql, csr::mapRowToModel);
		assertEquals(7, found.getStartupId());
		assertEquals("umbrellacopr", found.getCorpName());
		assertEquals("soreal.ch", found.getWebsite());
		assertEquals(12, found.getEquityShare());
		assertEquals(13, found.getCorporateBodyId());
		assertEquals(123, found.getCountryId());
		
	}

}
